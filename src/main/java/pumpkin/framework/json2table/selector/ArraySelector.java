package pumpkin.framework.json2table.selector;

import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;
import pumpkin.framework.json2table.utils.Lists;
import pumpkin.framework.json2table.utils.Strings;

@Getter
public class ArraySelector {
    private final List<SelectionRoute> path;

    private ArraySelector(final List<SelectionRoute> path) {
        if (Lists.isNullOrEmpty(path)) {
            throw new IllegalStateException("parameter 'path' is empty");
        }

        this.path = path;
        this.init();
    }

    public static ArraySelector of(final String selectPath) {
        if (Strings.isNullOrEmpty(selectPath)) {
            return new ArraySelector(List.of(new SelectionRoute("$")));
        }

        return new ArraySelector(Stream.of(selectPath.split("\\.")).map(SelectionRoute::new).toList());
    }

    public boolean isEmpty() {
        return Lists.isNullOrEmpty(path);
    }

    public SelectionRoute select() {
        return Lists.first(path).orElse(null);
    }

    private void buildRouteChain() {
        SelectionRoute parent = null;
        for (SelectionRoute key : path) {
            if (parent != null) {
                key.after(parent);
            }

            parent = key;
        }
    }

    private void init() {
        this.buildRouteChain();
    }
}
