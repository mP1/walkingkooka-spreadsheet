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

package walkingkooka.spreadsheet.importer.provider;

import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;

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
