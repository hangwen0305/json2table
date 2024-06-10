package pumpkin.framework.json2table.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import pumpkin.framework.json2table.resources.Resources;

public class TableTest {
    @Test
    public void should_itr_rows() {
        final Table table = Resources.prepareTable1();

        String[] cellValues = new String[]{
                "Row 1 Col 1", "Row 1 Col 2",
                "Row 2 Col 1", "Row 2 Col 2"
        };

        int idx = 0;
        for (TableRow row : table.getRows()) {
            for (TableRow.Cell cell : row) {
                Assertions.assertEquals(cellValues[idx++], cell.getValue());
            }
        }
    }

    @Test
    public void should_to_markdown() {
        Assertions.assertEquals(
                Resources.loadAsString("table1.md"),
                Resources.prepareTable1().toMarkdown()
        );
    }
}