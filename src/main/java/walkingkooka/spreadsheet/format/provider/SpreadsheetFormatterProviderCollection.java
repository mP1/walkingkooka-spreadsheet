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

package walkingkooka.spreadsheet.format.provider;

import walkingkooka.plugin.ProviderCollection;
import walkingkooka.plugin.ProviderCollectionProviderGetter;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetFormatterProvider} view of a collection of {@link SpreadsheetFormatterProvider providers}.
 */
final class SpreadsheetFormatterProviderCollection implements SpreadsheetFormatterProvider {

    static SpreadsheetFormatterProviderCollection with(final Set<SpreadsheetFormatterProvider> providers) {
        return new SpreadsheetFormatterProviderCollection(
            Objects.requireNonNull(providers, "providers")
        );
    }

    private SpreadsheetFormatterProviderCollection(final Set<SpreadsheetFormatterProvider> providers) {
        this.providers = ProviderCollection.with(
            new ProviderCollectionProviderGetter<>() {
                @Override
                public SpreadsheetFormatter get(final SpreadsheetFormatterProvider provider,
                                                final SpreadsheetFormatterName name,
                                                final List<?> values,
                                                final ProviderContext context) {
                    return provider.spreadsheetFormatter(
                        name,
                        values,
                        context
                    );
                }

                @Override
                public SpreadsheetFormatter get(final SpreadsheetFormatterProvider provider,
                                                final SpreadsheetFormatterSelector selector,
                                                final ProviderContext context) {
                    return provider.spreadsheetFormatter(
                        selector,
                        context
                    );
                }
            },
            SpreadsheetFormatterProvider::spreadsheetFormatterInfos,
            SpreadsheetFormatter.class.getSimpleName(),
            providers
        );
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                                     final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return this.providers.get(
            selector,
            context
        );
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterName name,
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
    public Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        return NO_NEXT_TOKEN;
    }

    @Override
    public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterSelector selector,
                                                                        final boolean includeSamples,
                                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return NO_SPREADSHEET_FORMATTER_SAMPLES;
    }

    @Override
    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
        return SpreadsheetFormatterInfoSet.with(
            this.providers.infos()
        );
    }

    private final ProviderCollection<SpreadsheetFormatterProvider, SpreadsheetFormatterName, SpreadsheetFormatterInfo, SpreadsheetFormatterSelector, SpreadsheetFormatter> providers;

    @Override
    public String toString() {
        return this.providers.toString();
    }
}
