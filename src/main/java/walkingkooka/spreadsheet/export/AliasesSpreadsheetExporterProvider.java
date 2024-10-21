package walkingkooka.spreadsheet.export;

import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetExporterProvider} that uses the given aliases definition and {@link SpreadsheetExporterProvider}
 * supporting aliases.
 */
final class AliasesSpreadsheetExporterProvider implements SpreadsheetExporterProvider {

    static AliasesSpreadsheetExporterProvider with(final SpreadsheetExporterAliasSet aliases,
                                                   final SpreadsheetExporterProvider provider) {
        return new AliasesSpreadsheetExporterProvider(
                Objects.requireNonNull(aliases, "aliases"),
                Objects.requireNonNull(provider, "provider")
        );
    }

    private AliasesSpreadsheetExporterProvider(final SpreadsheetExporterAliasSet aliases,
                                               final SpreadsheetExporterProvider provider) {
        this.aliases = aliases;
        this.provider = provider;

        this.infos = aliases.merge(provider.spreadsheetExporterInfos());
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                   final ProviderContext context) {
        return selector.evaluateText(
                this,
                context
        );
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterName name,
                                                   final List<?> values,
                                                   final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        SpreadsheetExporter function;

        final SpreadsheetExporterAliasSet aliases = this.aliases;
        final SpreadsheetExporterProvider provider = this.provider;

        final Optional<SpreadsheetExporterSelector> selector = aliases.aliasSelector(name);
        if (selector.isPresent()) {
            if (false == values.isEmpty()) {
                throw new IllegalArgumentException("Alias " + name + " should have no values");
            }
            // assumes that $provider caches selectors to function
            function = provider.spreadsheetExporter(
                    selector.get(),
                    context
            );
        } else {
            function = provider.spreadsheetExporter(
                    aliases.aliasOrName(name)
                            .orElseThrow(() -> new IllegalArgumentException("Unknown exporter " + name)),
                    values,
                    context
            );
        }

        return function;
    }

    private final SpreadsheetExporterAliasSet aliases;

    private final SpreadsheetExporterProvider provider;

    @Override
    public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
        return this.infos;
    }

    private final SpreadsheetExporterInfoSet infos;

    @Override
    public String toString() {
        return this.spreadsheetExporterInfos().toString();
    }
}
