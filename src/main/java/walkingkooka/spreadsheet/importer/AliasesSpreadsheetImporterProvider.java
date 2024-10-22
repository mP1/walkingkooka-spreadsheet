package walkingkooka.spreadsheet.importer;

import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetImporterProvider} that uses the given aliases definition and {@link SpreadsheetImporterProvider}
 * supporting aliases.
 */
final class AliasesSpreadsheetImporterProvider implements SpreadsheetImporterProvider {

    static AliasesSpreadsheetImporterProvider with(final SpreadsheetImporterAliasSet aliases,
                                                   final SpreadsheetImporterProvider provider) {
        return new AliasesSpreadsheetImporterProvider(
                Objects.requireNonNull(aliases, "aliases"),
                Objects.requireNonNull(provider, "provider")
        );
    }

    private AliasesSpreadsheetImporterProvider(final SpreadsheetImporterAliasSet aliases,
                                               final SpreadsheetImporterProvider provider) {
        this.aliases = aliases;
        this.provider = provider;

        this.infos = aliases.merge(provider.spreadsheetImporterInfos());
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                   final ProviderContext context) {
        return this.provider.spreadsheetImporter(
                this.aliases.selector(selector),
                context
        );
    }
    
    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        SpreadsheetImporter importer;

        final SpreadsheetImporterAliasSet aliases = this.aliases;
        final SpreadsheetImporterProvider provider = this.provider;

        final Optional<SpreadsheetImporterSelector> selector = aliases.aliasSelector(name);
        if (selector.isPresent()) {
            if (false == values.isEmpty()) {
                throw new IllegalArgumentException("Alias " + name + " should have no values");
            }
            // assumes that $provider caches selectors to function
            importer = provider.spreadsheetImporter(
                    selector.get(),
                    context
            );
        } else {
            importer = provider.spreadsheetImporter(
                    aliases.aliasOrName(name)
                            .orElseThrow(() -> new IllegalArgumentException("Unknown importer " + name)),
                    values,
                    context
            );
        }

        return importer;
    }

    private final SpreadsheetImporterAliasSet aliases;

    private final SpreadsheetImporterProvider provider;

    @Override
    public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
        return this.infos;
    }

    private final SpreadsheetImporterInfoSet infos;

    @Override
    public String toString() {
        return this.spreadsheetImporterInfos().toString();
    }
}
