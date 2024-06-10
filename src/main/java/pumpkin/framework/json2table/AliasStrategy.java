package pumpkin.framework.json2table;

import pumpkin.framework.json2table.selector.Path;

public interface AliasStrategy {
    String alias(Path path);
}
