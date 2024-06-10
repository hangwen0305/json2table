package pumpkin.framework.json2table;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import pumpkin.framework.json2table.data.Table;
import pumpkin.framework.json2table.resources.Resources;

public class JsonToTableConverterTest {

    /**
     * json转成表格,默认列出root级别属性作为列头；
     * 对于对象类型的属性会拉平到上一级属性；
     * 对于数组类型的属性，不再展开，以json方式展示
     * <p>
     * Single-object json converted to a table, the default list of root level properties as the column header;
     * Attributes of the object type are flattened to the upper level;
     * For properties of array type, it is no longer expanded and displayed in json format
     */
    @Test
    public void should_transfer_profile_json_to_table() {
        final String jsonStr = Resources.loadAsString("profile.json");
        final Table table = JsonToTableConverter.toTable(jsonStr);

        Assertions.assertEquals(Resources.loadAsString("profile_$_table.md"), table.toMarkdown());
    }

    @Test
    public void should_transfer_profile_json_to_table_to_phones() {
        final String jsonStr = Resources.loadAsString("profile.json");
        final Table table = JsonToTableConverter.toTable(jsonStr, "$.phones");

        Assertions.assertEquals(Resources.loadAsString("profile_$_phones_table.md"), table.toMarkdown());
    }
}