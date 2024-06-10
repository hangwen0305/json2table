package pumpkin.framework.json2table.data;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pumpkin.framework.json2table.selector.Path;

@EqualsAndHashCode
public class TableHeader {
    @Getter
    private final Path path;
    @Getter
    @Setter
    private String alias;

    private int index = -1;

    public TableHeader(final Path path) {
        this.path = path.copy();
    }

    //for ut
    public TableHeader(String alias) {
        this.path = null;
        this.alias = alias;
    }

    @Override
    public String toString() {
        if (alias != null) {
            return alias;
        }

        if (path != null) {
            return path.toString();
        }

        return "null";
    }

    void index(final int index) {
        this.index = index;
    }

    int index() {
        return this.index;
    }
}
