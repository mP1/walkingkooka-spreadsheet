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

package walkingkooka.spreadsheet.compare;

import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.PluginSelectorLike;
import walkingkooka.plugin.ProviderCollection;
import walkingkooka.plugin.ProviderCollectionProviderGetter;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link SpreadsheetComparatorProvider} view of a collection of {@link SpreadsheetComparatorProvider providers}.
 */
final class SpreadsheetComparatorProviderCollection implements SpreadsheetComparatorProvider {

    static SpreadsheetComparatorProviderCollection with(final Set<SpreadsheetComparatorProvider> providers) {
        return new SpreadsheetComparatorProviderCollection(
                Objects.requireNonNull(providers, "providers")
        );
    }

    private SpreadsheetComparatorProviderCollection(final Set<SpreadsheetComparatorProvider> providers) {
        this.providers = ProviderCollection.with(
                new ProviderCollectionProviderGetter<SpreadsheetComparatorProvider, SpreadsheetComparatorName, PluginSelectorLike<SpreadsheetComparatorName>, SpreadsheetComparator<?>>() {
                    @Override
                    public SpreadsheetComparator<?> get(final SpreadsheetComparatorProvider provider,
                                                        final SpreadsheetComparatorName name,
                                                        final List<?> values,
                                                        final ProviderContext context) {
                        return provider.spreadsheetComparator(
                                name,
                                context
                        );
                    }

                    @Override
                    public SpreadsheetComparator<?> get(final SpreadsheetComparatorProvider provider,
                                                        final PluginSelectorLike<SpreadsheetComparatorName> selector,
                                                        final ProviderContext context) {
                        throw new UnsupportedOperationException();
                    }
                },
                SpreadsheetComparatorProvider::spreadsheetComparatorInfos,
                SpreadsheetComparator.class.getSimpleName(),
                providers
        );
    }

    @Override
    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorName name,
                                                          final ProviderContext context) {
        Objects.requireNonNull(name, "name");

        return this.providers.get(
                name,
                Lists.empty(),
                context
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
        return SpreadsheetComparatorInfoSet.with(
                this.providers.infos()
        );
    }

    private final ProviderCollection<SpreadsheetComparatorProvider, SpreadsheetComparatorName, SpreadsheetComparatorInfo, PluginSelectorLike<SpreadsheetComparatorName>, SpreadsheetComparator<?>> providers;

    @Override
    public String toString() {
        return this.providers.toString();
    }
}
