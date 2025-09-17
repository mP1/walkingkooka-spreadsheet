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

import walkingkooka.plugin.ProviderCollection;
import walkingkooka.plugin.ProviderCollectionProviderGetter;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.importer.SpreadsheetImporter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link SpreadsheetImporterProvider} view of a collection of {@link SpreadsheetImporterProvider providers}.
 */
final class SpreadsheetImporterProviderCollection implements SpreadsheetImporterProvider {

    static SpreadsheetImporterProviderCollection with(final Set<SpreadsheetImporterProvider> providers) {
        return new SpreadsheetImporterProviderCollection(
            Objects.requireNonNull(providers, "providers")
        );
    }

    private SpreadsheetImporterProviderCollection(final Set<SpreadsheetImporterProvider> providers) {
        this.providers = ProviderCollection.with(
            new ProviderCollectionProviderGetter<>() {
                @Override
                public SpreadsheetImporter get(final SpreadsheetImporterProvider provider,
                                               final SpreadsheetImporterName name,
                                               final List<?> values,
                                               final ProviderContext context) {
                    return provider.spreadsheetImporter(
                        name,
                        values,
                        context
                    );
                }

                @Override
                public SpreadsheetImporter get(final SpreadsheetImporterProvider provider,
                                               final SpreadsheetImporterSelector selector,
                                               final ProviderContext context) {
                    return provider.spreadsheetImporter(
                        selector,
                        context
                    );
                }
            },
            SpreadsheetImporterProvider::spreadsheetImporterInfos,
            SpreadsheetImporter.class.getSimpleName(),
            providers
        );
    }

    @Override
    public SpreadsheetImporter spreadsheetImporter(final SpreadsheetImporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.providers.get(
            selector,
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

        return this.providers.get(
            name,
            values,
            context
        );
    }

    @Override
    public SpreadsheetImporterInfoSet spreadsheetImporterInfos() {
        return SpreadsheetImporterInfoSet.with(
            this.providers.infos()
        );
    }

    private final ProviderCollection<SpreadsheetImporterProvider, SpreadsheetImporterName, SpreadsheetImporterInfo, SpreadsheetImporterSelector, SpreadsheetImporter> providers;

    @Override
    public String toString() {
        return this.providers.toString();
    }
}
