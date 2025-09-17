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

package walkingkooka.spreadsheet.export.provider;

import walkingkooka.plugin.ProviderCollection;
import walkingkooka.plugin.ProviderCollectionProviderGetter;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.export.SpreadsheetExporter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link SpreadsheetExporterProvider} view of a collection of {@link SpreadsheetExporterProvider providers}.
 */
final class SpreadsheetExporterProviderCollection implements SpreadsheetExporterProvider {

    static SpreadsheetExporterProviderCollection with(final Set<SpreadsheetExporterProvider> providers) {
        return new SpreadsheetExporterProviderCollection(
            Objects.requireNonNull(providers, "providers")
        );
    }

    private SpreadsheetExporterProviderCollection(final Set<SpreadsheetExporterProvider> providers) {
        this.providers = ProviderCollection.with(
            new ProviderCollectionProviderGetter<>() {
                @Override
                public SpreadsheetExporter get(final SpreadsheetExporterProvider provider,
                                               final SpreadsheetExporterName name,
                                               final List<?> values,
                                               final ProviderContext context) {
                    return provider.spreadsheetExporter(
                        name,
                        values,
                        context
                    );
                }

                @Override
                public SpreadsheetExporter get(final SpreadsheetExporterProvider provider,
                                               final SpreadsheetExporterSelector selector,
                                               final ProviderContext context) {
                    return provider.spreadsheetExporter(
                        selector,
                        context
                    );
                }
            },
            SpreadsheetExporterProvider::spreadsheetExporterInfos,
            SpreadsheetExporter.class.getSimpleName(),
            providers
        );
    }

    @Override
    public SpreadsheetExporter spreadsheetExporter(final SpreadsheetExporterSelector selector,
                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.providers.get(
            selector,
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

        return this.providers.get(
            name,
            values,
            context
        );
    }

    @Override
    public SpreadsheetExporterInfoSet spreadsheetExporterInfos() {
        return SpreadsheetExporterInfoSet.with(
            this.providers.infos()
        );
    }

    private final ProviderCollection<SpreadsheetExporterProvider, SpreadsheetExporterName, SpreadsheetExporterInfo, SpreadsheetExporterSelector, SpreadsheetExporter> providers;

    @Override
    public String toString() {
        return this.providers.toString();
    }
}
