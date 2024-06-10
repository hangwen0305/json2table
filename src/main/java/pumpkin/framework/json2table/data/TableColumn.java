package pumpkin.framework.json2table.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import pumpkin.framework.json2table.utils.Lists;

@EqualsAndHashCode
@ToString
public class TableColumn {
    @Getter
    private final TableHeader header;
    private final List<Object> cells;

    public TableColumn(final TableHeader header, final List<Object> cells) {
        this.header = header;
        this.cells = Optional.ofNullable(cells).orElseGet(ArrayList::new);
    }

    public int getRowSize() {
        return cells.size();
    }

    public Object getValue(final int rowIndex) {
        return Lists.safeGet(cells, rowIndex);
    }

}
