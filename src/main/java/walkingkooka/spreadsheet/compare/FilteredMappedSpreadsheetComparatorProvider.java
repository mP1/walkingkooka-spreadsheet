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

import walkingkooka.plugin.FilteredProviderMapper;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetComparatorProvider} that wraps a view of new {@link SpreadsheetComparatorName} to a wrapped {@link SpreadsheetComparatorProvider}.
 */
final class FilteredMappedSpreadsheetComparatorProvider implements SpreadsheetComparatorProvider {

    static FilteredMappedSpreadsheetComparatorProvider with(final SpreadsheetComparatorInfoSet infos,
                                                            final SpreadsheetComparatorProvider provider) {
        Objects.requireNonNull(infos, "infos");
        Objects.requireNonNull(provider, "provider");

        return new FilteredMappedSpreadsheetComparatorProvider(
            infos,
            provider
        );
    }

    private FilteredMappedSpreadsheetComparatorProvider(final SpreadsheetComparatorInfoSet infos,
                                                        final SpreadsheetComparatorProvider provider) {
        this.mapper = FilteredProviderMapper.with(
            infos,
            provider.spreadsheetComparatorInfos(),
            SpreadsheetComparatorPluginHelper.INSTANCE
        );
        this.provider = provider;
    }

    @Override
    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorSelector selector,
                                                          final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        final SpreadsheetComparatorName name = selector.name();

        return this.provider.spreadsheetComparator(
            selector.setName(
                this.mapper.name(name)
            ),
            context
        );
    }

    @Override
    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorName name,
                                                          final List<?> values,
                                                          final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        return this.provider.spreadsheetComparator(
            this.mapper.name(name),
            values,
            context
        );
    }

    /**
     * The original wrapped {@link SpreadsheetComparatorProvider}.
     */
    private final SpreadsheetComparatorProvider provider;

    @Override
    public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
        return this.mapper.infos();
    }

    @Override
    public String toString() {
        return this.mapper.toString();
    }

    private final FilteredProviderMapper<SpreadsheetComparatorName, SpreadsheetComparatorInfo, SpreadsheetComparatorInfoSet, SpreadsheetComparatorSelector, SpreadsheetComparatorAlias, SpreadsheetComparatorAliasSet> mapper;
}
