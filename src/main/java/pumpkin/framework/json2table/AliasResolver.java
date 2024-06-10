package pumpkin.framework.json2table;

import java.util.List;

import pumpkin.framework.json2table.data.TableHeader;
import pumpkin.framework.json2table.utils.Lists;

class AliasResolver {
    private final AliasStrategy strategy;

    public AliasResolver(final AliasStrategy strategy) {
        this.strategy = strategy;
    }

    public void rename(final List<TableHeader> headers) {
        if (Lists.isNullOrEmpty(headers)) {
            return;
        }

        headers.forEach(header -> {
            header.setAlias(strategy.alias(header.getPath()));
        });
    }

}


