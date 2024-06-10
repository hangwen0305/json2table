package pumpkin.framework.json2table.data;

import java.util.Iterator;

import lombok.Getter;
import lombok.ToString;

/**
 * @author hangwen
 * @date 2020/1/8
 */
public class TableRow implements Iterable<TableRow.Cell> {

    final Table table;
    private Object[] values;

    TableRow(final Table table) {
        this.table = table;
        this.values = new Object[this.table.headers.size()];
    }

    public Object getValue(final TableHeader header) {
        return this.getValue(header.index());
    }

    public Object getValue(final int headerIndex) {
        return this.values[headerIndex];
    }

    @Override
    public Iterator<Cell> iterator() {
        return new Itr();
    }

    @Override
    public String toString() {
        final var iterator = this.table.getHeaders().iterator();

        StringBuilder sb = new StringBuilder();
        sb.append('{');

        for (; ; ) {
            var header = iterator.next();

            sb.append("%s = %s".formatted(header.getAlias(), this.getValue(header)));

            if (iterator.hasNext()) {
                sb.append(", ");
            } else {
                return sb.append('}').toString();
            }
        }
    }

    void setValue(final int headerIndex, final Object value) {
        this.values[headerIndex] = value;
    }

    void setValue(final TableHeader header, final Object value) {
        this.setValue(header.index(), value);
    }

    private class Itr implements Iterator<Cell> {
        private final Iterator<TableHeader> headerIterator;
        private final Cell cursor = new Cell();

        private Itr() {
            this.headerIterator = table.getHeaders().iterator();
        }

        @Override
        public boolean hasNext() {
            return headerIterator.hasNext();
        }

        @Override
        public Cell next() {
            final TableHeader next = headerIterator.next();

            cursor.headerAlias = next.getAlias();
            cursor.value = getValue(next);

            return cursor;
        }
    }

    @Getter
    @ToString
    public class Cell {
        private String headerAlias;
        private Object value;
    }

}
