package walkingkooka.spreadsheet.parser;

import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParserProvider} that uses the given aliases definition and {@link SpreadsheetParserProvider}
 * supporting aliases.
 */
final class AliasesSpreadsheetParserProvider implements SpreadsheetParserProvider {

    static AliasesSpreadsheetParserProvider with(final SpreadsheetParserAliasSet aliases,
                                                 final SpreadsheetParserProvider provider) {
        return new AliasesSpreadsheetParserProvider(
                Objects.requireNonNull(aliases, "aliases"),
                Objects.requireNonNull(provider, "provider")
        );
    }

    private AliasesSpreadsheetParserProvider(final SpreadsheetParserAliasSet aliases,
                                             final SpreadsheetParserProvider provider) {
        this.aliases = aliases;
        this.provider = provider;

        this.infos = aliases.merge(provider.spreadsheetParserInfos());
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                               final ProviderContext context) {
        return selector.evaluateText(
                this,
                context
        );
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values,
                                               final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        SpreadsheetParser function;

        final SpreadsheetParserAliasSet aliases = this.aliases;
        final SpreadsheetParserProvider provider = this.provider;

        final Optional<SpreadsheetParserSelector> selector = aliases.aliasSelector(name);
        if (selector.isPresent()) {
            if (false == values.isEmpty()) {
                throw new IllegalArgumentException("Alias " + name + " should have no values");
            }
            // assumes that $provider caches selectors to function
            function = provider.spreadsheetParser(
                    selector.get(),
                    context
            );
        } else {
            function = provider.spreadsheetParser(
                    aliases.aliasOrName(name)
                            .orElseThrow(() -> new IllegalArgumentException("Unknown parser " + name)),
                    values,
                    context
            );
        }

        return function;
    }

    @Override
    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final SpreadsheetParserName name = selector.name();

        return this.aliases.aliasOrName(name)
                .flatMap(n -> this.provider.spreadsheetParserNextToken(selector.setName(n)));
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        return this.aliases.aliasOrName(selector.name())
                .flatMap(
                        n ->
                                this.provider.spreadsheetFormatterSelector(
                                        selector.setName(n)
                                )
                );
    }

    private final SpreadsheetParserAliasSet aliases;

    private final SpreadsheetParserProvider provider;

    @Override
    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
        return this.infos;
    }

    private final SpreadsheetParserInfoSet infos;

    @Override
    public String toString() {
        return this.spreadsheetParserInfos().toString();
    }
}
