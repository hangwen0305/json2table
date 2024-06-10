package pumpkin.framework.json2table.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import pumpkin.framework.json2table.data.Table;
import pumpkin.framework.json2table.data.TableColumn;
import pumpkin.framework.json2table.data.TableHeader;

/**
 * @author hangwen
 * @date 2021/3/18
 */
public class Resources {

    public static String loadAsString(final String fileName) {
        URL url = Resources.class.getClassLoader().getResource(fileName);

        StringBuilder content = new StringBuilder();

        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
                content.append(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return content.toString();
    }

    public static Table prepareTable1() {
        List<Object> column1Data = List.of("Row 1 Col 1", "Row 2 Col 1");
        List<Object> column2Data = List.of("Row 1 Col 2", "Row 2 Col 2");

        TableHeader header1 = new TableHeader("Column1");
        TableHeader header2 = new TableHeader("Column2");

        TableColumn column1 = new TableColumn(header1, column1Data);
        TableColumn column2 = new TableColumn(header2, column2Data);

        List<TableColumn> columns = List.of(column1, column2);

        return new Table(columns);
    }
}
