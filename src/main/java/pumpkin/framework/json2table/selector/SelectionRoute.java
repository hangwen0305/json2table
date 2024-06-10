package pumpkin.framework.json2table.selector;

import java.util.Objects;

import lombok.Getter;

public class SelectionRoute {
    @Getter
    private final String key;
    @Getter
    private SelectionRoute next;
    @Getter
    private SelectionRoute previous;

    public SelectionRoute(final String key) {
        this.key = key;
    }

    public Path getPath() {
        if (this.previous == null) {
            return new Path();
        } else {
            return this.previous.getPath().push(this.key);
        }
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean isMatchNextRouteKey(final String key) {
        return this.hasNext() && Objects.equals(this.getNext().getKey(), key);
    }

    public Path relativePath(final Path absolutePath) {
        return absolutePath.subPath(this.getPath().depth());
    }

    void after(final SelectionRoute parent) {
        parent.next = this;
        this.previous = parent;
    }

}
