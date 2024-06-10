package pumpkin.framework.json2table;

import lombok.Getter;
import pumpkin.framework.json2table.selector.ArraySelector;

@Getter
public class ConvertOptions {
    private final ArraySelector arraySelector;
    private final AliasStrategy aliasStrategy;

    public ConvertOptions(final String selectPath) {
        this(ArraySelector.of(selectPath), PathJoiner.JOINER_PATH_WITH_POINT);
    }

    private ConvertOptions(final ArraySelector arraySelector) {
        this(arraySelector, PathJoiner.JOINER_PATH_WITH_POINT);
    }

    private ConvertOptions(final ArraySelector arraySelector, final AliasStrategy aliasStrategy) {
        this.arraySelector = arraySelector;
        this.aliasStrategy = aliasStrategy;
    }

}
