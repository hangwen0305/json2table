package pumpkin.framework.json2table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import pumpkin.framework.json2table.data.TableColumn;
import pumpkin.framework.json2table.selector.ArraySelector;
import pumpkin.framework.json2table.selector.SelectionRoute;
import pumpkin.framework.json2table.utils.Lists;
import pumpkin.framework.json2table.utils.Strings;

/**
 * @author hangwen
 * @date 2021/3/17
 */
@Getter
class ConvertContext {

    private final ArraySelector arraySelector;
    private final AliasResolver aliasResolver;
    private final Map<String, List<Map<String, Object>>> tables;

    ConvertContext(final ArraySelector arraySelector, final AliasResolver aliasResolver) {
        this.aliasResolver = aliasResolver;
        this.arraySelector = arraySelector;
        this.tables = new HashMap<>(arraySelector.getPath().size());

        Lists.first(arraySelector.getPath()).ifPresent(this::checkRootPath);
    }

    public int addMapTree(final Map<String, Object> mapTree, final String selector, final int duplicates) {
        for (int i = 0; i < duplicates; i++) {
            this.tables.computeIfAbsent(selector, k -> new ArrayList<>()).add(mapTree);
        }

        return duplicates;
    }

    public List<Map<String, Object>> getTable(final String selector) {
        return tables.get(selector);
    }

    public List<TableColumn> rename(final List<TableColumn> columns) {
        this.aliasResolver.rename(columns.stream().map(TableColumn::getHeader).toList());

        return columns;
    }

    private void checkRootPath(final SelectionRoute route) {
        if (Strings.isNullOrEmpty(route.getKey()) || "$".equals(route.getKey())) {
            return;
        }

        throw new IllegalStateException("root path's key must be '$' or empty");
    }
}
