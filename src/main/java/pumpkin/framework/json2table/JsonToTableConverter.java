package pumpkin.framework.json2table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import pumpkin.framework.json2table.data.Table;
import pumpkin.framework.json2table.data.TableColumn;
import pumpkin.framework.json2table.data.TableHeader;
import pumpkin.framework.json2table.error.ErrorCode;
import pumpkin.framework.json2table.selector.ArraySelector;
import pumpkin.framework.json2table.selector.Path;
import pumpkin.framework.json2table.selector.SelectionRoute;
import pumpkin.framework.json2table.utils.Jsons;
import pumpkin.framework.json2table.utils.Lists;
import pumpkin.framework.json2table.utils.Maps;

/**
 * @author hangwen
 * @date 2021/3/17
 */
public class JsonToTableConverter {

    public static Table toTable(final String jsonStr) {
        return toTable(jsonStr, "$");
    }

    public static Table toTable(final String jsonStr, final String selectPath) {
        return toTable(jsonStr, new ConvertOptions(selectPath));
    }

    public static Table toTable(final String jsonStr, final ConvertOptions options) {
        Optional<Map<String, Object>> mapTree = parseMap(jsonStr);
        if (mapTree.isPresent()) {
            return toTable(mapTree.get(), options);
        }

        Optional<List<Map<String, Object>>> list = parseList(jsonStr);
        if (list.isPresent()) {
            return toTable(list.get(), options);
        }

        throw new IllegalStateException(
                "Error Code:%s".formatted(ErrorCode.INVALID_JSON)
        );
    }

    public static Table toTable(final Map<String, Object> mapTree, final ConvertOptions options) {
        if (Maps.isNullOrEmpty(mapTree) || options.getArraySelector().isEmpty()) {
            return null;
        }

        return toTable(List.of(mapTree), options);
    }

    public static Table toTable(final List<Map<String, Object>> mapTrees, final ConvertOptions options) {
        if (Lists.isNullOrEmpty(mapTrees) || options.getArraySelector().isEmpty()) {
            return null;
        }

        final ArraySelector arraySelector = options.getArraySelector();
        final AliasResolver aliasResolver = createAliasResolver(options);

        ConvertContext context = new ConvertContext(arraySelector, aliasResolver);

        mapTrees.forEach(mapTree -> doSplit(mapTree, arraySelector.select(), context));

        List<TableColumn> columns = Lists.mapReduce(arraySelector.getPath(),
                route -> {
                    List<Map<String, Object>> table = context.getTable(route.getKey());

                    if (Lists.isNullOrEmpty(table)) {
                        return Collections.emptyList();
                    }

                    return expandTable(route, table);
                });
        context.rename(columns);

        return new Table(columns);
    }

    private static Object asString(final Object val) {
        if (val instanceof List) {
            return Jsons.toJsonString(val);
        }

        return val;
    }

    private static AliasResolver createAliasResolver(final ConvertOptions options) {
        return new AliasResolver(options.getAliasStrategy());
    }

    private static List<TableHeader> createHeaders(final SelectionRoute route,
                                                   final Path path,
                                                   final Map<String, Object> row,
                                                   final Predicate<Path> created) {
        //row.size means the headers count for the current row
        List<TableHeader> headers = Lists.arrayList(row.size());

        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();

            Path subPath = path.copy().push(key);
            boolean keyIsNextRoute = route.isMatchNextRouteKey(key);

            if (val instanceof Map) {
                SelectionRoute nextRoute = route;
                if (keyIsNextRoute) {
                    nextRoute = route.getNext();
                }

                //noinspection unchecked
                headers.addAll(createHeaders(nextRoute, subPath, (Map<String, Object>) val, created));
            } else {
                if (val instanceof List && keyIsNextRoute) {
                    continue;
                }

                if (created != null && created.test(subPath)) {
                    continue;
                }

                headers.add(new TableHeader(subPath));
            }
        }

        return headers;
    }

    private static int doSplit(final Map<String, Object> mapTree,
                               final SelectionRoute route,
                               final ConvertContext context) {
        if (!route.hasNext()) {
            return context.addMapTree(mapTree, route.getKey(), 1);
        }

        int duplicates = 0;

        ChildrenNode childrenNode = getChildrenByRoute(mapTree, route.getNext());
        for (Map child : childrenNode.children) {
            //noinspection unchecked
            int childDuplicates = doSplit(child, childrenNode.route, context);

            duplicates += childDuplicates;
        }

        return context.addMapTree(mapTree, route.getKey(), duplicates);
    }

    private static List<TableColumn> expandTable(final SelectionRoute route,
                                                 final List<Map<String, Object>> table) {
        List<TableColumn> columns = new ArrayList<>();
        List<TableHeader> headers = new ArrayList<>();

        Set<Path> createdPaths = new HashSet<>();

        Path routePath = route.getPath();
        for (Map<String, Object> row : table) {
            headers.addAll(createHeaders(route, routePath, row, headerPath -> {
                if (createdPaths.contains(headerPath)) {
                    return true;
                }

                createdPaths.add(headerPath);
                return false;
            }));
        }

        for (TableHeader header : headers) {
            Path path = route.relativePath(header.getPath());

            //cache the value for the same row
            AtomicReference<Map<String, Object>> previous = new AtomicReference<>();
            AtomicReference<Object> previousValue = new AtomicReference<>();

            List<Object> cells = table.stream().map(row -> {
                if (row == previous.get()) {
                    return previousValue.get();
                }

                final Object value = getValueByPath(row, path);

                previous.set(row);
                previousValue.set(value);

                return value;
            }).toList();

            columns.add(new TableColumn(header, cells));
        }

        return columns;
    }

    private static ChildrenNode getChildrenByRoute(final Map<String, Object> tree, final SelectionRoute route) {
        String key = route.getKey();
        Object o = tree.get(key);

        if (o == null) {
            return new ChildrenNode(route, new ArrayList<>());
        }

        if (o instanceof List) {
            //noinspection unchecked
            return new ChildrenNode(
                    route,
                    ((List<Object>) o).stream()
                            .filter(Map.class::isInstance)
                            .map(Map.class::cast)
                            .collect(Collectors.toList())
            );
        }

        if (o instanceof Map && route.hasNext()) {
            //noinspection unchecked
            return getChildrenByRoute((Map<String, Object>) o, route.getNext());
        }

        throw new IllegalStateException(
                "Error Code:%s,[%s]'s value type is not array'".formatted(
                        ErrorCode.INVALID_SELECTOR_PATH, key
                )
        );
    }

    private static Object getValueByPath(final Map<String, Object> row, final Path path) {
        if (row == null) {
            return "";
        }

        if (path.isLeaf()) {
            return asString(row.get(path.firstKey()));
        } else {
            //noinspection unchecked
            return getValueByPath(
                    (Map<String, Object>) row.get(path.firstKey()),
                    path.subPath(1)
            );
        }
    }

    private static Optional<List<Map<String, Object>>> parseList(final String jsonStr) {
        try {
            //noinspection unchecked
            return Optional.of(Jsons.toJavaObject(jsonStr, List.class));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private static Optional<Map<String, Object>> parseMap(final String jsonString) {
        try {
            return Optional.of(Jsons.toMap(jsonString));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @AllArgsConstructor
    private static class ChildrenNode {
        private final SelectionRoute route;
        private final List<Map> children;
    }

}
