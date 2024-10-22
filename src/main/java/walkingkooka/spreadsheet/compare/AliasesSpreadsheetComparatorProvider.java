package walkingkooka.spreadsheet.compare;

import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetComparatorProvider} that uses the given aliases definition and {@link SpreadsheetComparatorProvider}
 * supporting aliases.
 */
final class AliasesSpreadsheetComparatorProvider implements SpreadsheetComparatorProvider {

    static AliasesSpreadsheetComparatorProvider with(final SpreadsheetComparatorAliasSet aliases,
                                                     final SpreadsheetComparatorProvider provider) {
        return new AliasesSpreadsheetComparatorProvider(
                Objects.requireNonNull(aliases, "aliases"),
                Objects.requireNonNull(provider, "provider")
        );
    }

    private AliasesSpreadsheetComparatorProvider(final SpreadsheetComparatorAliasSet aliases,
                                                 final SpreadsheetComparatorProvider provider) {
        this.aliases = aliases;
        this.provider = provider;

        this.infos = aliases.merge(provider.spreadsheetComparatorInfos());
    }

    @Override
    public SpreadsheetComparator spreadsheetComparator(final SpreadsheetComparatorSelector selector,
                                                       final ProviderContext context) {
        return this.provider.spreadsheetComparator(
                this.aliases.selector(selector),
                context
        );
    }

    @Override
    public SpreadsheetComparator spreadsheetComparator(final SpreadsheetComparatorName name,
                                                       final List<?> values,
                                                       final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        SpreadsheetComparator comparator;

        final SpreadsheetComparatorAliasSet aliases = this.aliases;
        final SpreadsheetComparatorProvider provider = this.provider;

        final Optional<SpreadsheetComparatorSelector> selector = aliases.aliasSelector(name);
        if (selector.isPresent()) {
            if (false == values.isEmpty()) {
                throw new IllegalArgumentException("Alias " + name + " should have no values");
            }
            // assumes that $provider caches selectors to comparator
            comparator = provider.spreadsheetComparator(
                    selector.get(),
                    context
            );
        } else {
            comparator = provider.spreadsheetComparator(
                    aliases.aliasOrName(name)
                            .orElseThrow(() -> new IllegalArgumentException("Unknown comparator " + name)),
                    values,
                    context
            );
        }

        return comparator;
    }

    private final SpreadsheetComparatorAliasSet aliases;

    private final SpreadsheetComparatorProvider provider;

    @Override
    public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
        return this.infos;
    }

    private final SpreadsheetComparatorInfoSet infos;

    @Override
    public String toString() {
        return this.spreadsheetComparatorInfos().toString();
    }
}
