package pumpkin.framework.json2table.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import pumpkin.framework.json2table.utils.Lists;

public class Table {

    @Getter
    final List<TableHeader> headers = new ArrayList<>();
    @Getter
    final List<TableRow> rows = new ArrayList<>();

    public Table(final List<TableColumn> columns) {
        this.addHeaders(columns.stream().map(TableColumn::getHeader).toList());

        int rowSize = Lists.first(columns).map(TableColumn::getRowSize).orElse(0);

        for (int rowIndex = 0; rowIndex < rowSize; rowIndex++) {
            TableRow row = this.createRow();

            for (TableColumn source : columns) {
                TableHeader target = source.getHeader();

                Object val = source.getValue(rowIndex);
                row.setValue(target, val);
            }

            this.addRow(row);
        }
    }

    public String toMarkdown() {
        StringBuilder markdown = new StringBuilder();

        // Create header row
        List<String> headerNames = headers.stream().map(TableHeader::toString).toList();
        markdown.append("| ").append(String.join(" | ", headerNames)).append(" |").append("\n");

        // Create separator row
        markdown.append("|").append(" --- |".repeat(headers.size())).append("\n");

        // Create data rows
        for (TableRow row : rows) {
            List<String> rowValues = new ArrayList<>();

            for (TableHeader header : headers) {
                Object value = row.getValue(header);
                rowValues.add(value != null ? value.toString() : "");
            }

            String rowString = "| " + String.join(" | ", rowValues) + " |";
            markdown.append(rowString.replaceAll(" \\| ", " | ")).append("\n");
        }

        return markdown.toString();
    }

    private void addHeaders(final Iterable<TableHeader> headers) {
        int index = this.headers.size();
        for (TableHeader header : headers) {
            header.index(index);
            this.headers.add(index++, header);
        }
    }

    private void addRow(final TableRow row) {
        this.rows.add(row);
    }

    private TableRow createRow() {
        return new TableRow(this);
    }

}
