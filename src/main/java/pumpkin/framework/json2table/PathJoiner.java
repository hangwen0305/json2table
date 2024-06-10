package pumpkin.framework.json2table;

import pumpkin.framework.json2table.selector.Path;

public class PathJoiner implements AliasStrategy {

    public static final PathJoiner JOINER_PATH_WITH_POINT = new PathJoiner(".");

    private final String delimiter;

    public PathJoiner(final String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String alias(final Path path) {
        return String.join(delimiter, path.getRoutes());
    }
}
