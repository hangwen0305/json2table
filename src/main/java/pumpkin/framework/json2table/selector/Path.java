package pumpkin.framework.json2table.selector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pumpkin.framework.json2table.utils.Lists;

@Getter
@EqualsAndHashCode
@ToString
public class Path {
    private final List<String> routes;

    public Path() {
        this(null);
    }

    public Path(final List<String> routes) {
        this.routes = Optional.ofNullable(routes).orElseGet(ArrayList::new);
    }

    public Path copy() {
        return new Path(new ArrayList<>(routes));
    }

    public int depth() {
        if (this.routes == null) {
            return 0;
        }

        return this.routes.size();
    }

    public String firstKey() {
        return Lists.first(this.routes).orElse(null);
    }

    public boolean isLeaf() {
        return this.routes.size() == 1;
    }

    public Path push(final String key) {
        this.routes.add(key);

        return this;
    }

    public Path subPath(final int subIndex) {
        if (subIndex == 0) {
            return new Path(new ArrayList<>(routes));
        } else {
            return new Path(new ArrayList<>(routes.subList(subIndex, routes.size())));
        }
    }
}

