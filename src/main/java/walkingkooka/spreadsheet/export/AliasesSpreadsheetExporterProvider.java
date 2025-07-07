/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
        return this.provider.spreadsheetExporter(
            this.aliases.selector(selector),
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

        SpreadsheetExporter exporter;

        final SpreadsheetExporterAliasSet aliases = this.aliases;
        final SpreadsheetExporterProvider provider = this.provider;

        final Optional<SpreadsheetExporterSelector> selector = aliases.aliasSelector(name);
        if (selector.isPresent()) {
            if (false == values.isEmpty()) {
                throw new IllegalArgumentException("Alias " + name + " should have no values");
            }
            // assumes that $provider caches selectors to exporter
            exporter = provider.spreadsheetExporter(
                selector.get(),
                context
            );
        } else {
            exporter = provider.spreadsheetExporter(
                aliases.aliasOrName(name)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown exporter " + name)),
                values,
                context
            );
        }

        return exporter;
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
