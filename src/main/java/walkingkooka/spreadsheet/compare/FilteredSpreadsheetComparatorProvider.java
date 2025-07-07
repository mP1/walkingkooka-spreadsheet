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

import walkingkooka.plugin.FilteredProviderGuard;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;

/**
 * A {@link SpreadsheetComparatorProvider} that provides {@link SpreadsheetComparator} from one provider but lists more {@link SpreadsheetComparatorInfo}.
 */
final class FilteredSpreadsheetComparatorProvider implements SpreadsheetComparatorProvider {

    static FilteredSpreadsheetComparatorProvider with(final SpreadsheetComparatorProvider provider,
                                                      final SpreadsheetComparatorInfoSet infos) {
        return new FilteredSpreadsheetComparatorProvider(
            Objects.requireNonNull(provider, "provider"),
            Objects.requireNonNull(infos, "infos")
        );
    }

    private FilteredSpreadsheetComparatorProvider(final SpreadsheetComparatorProvider provider,
                                                  final SpreadsheetComparatorInfoSet infos) {
        this.guard = FilteredProviderGuard.with(
            infos.names(),
            SpreadsheetComparatorPluginHelper.INSTANCE
        );

        this.provider = provider;
        this.infos = infos;
    }

    @Override
    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorSelector selector,
                                                          final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        final SpreadsheetComparatorName name = selector.name();

        return this.provider.spreadsheetComparator(
            selector.setName(
                this.guard.name(name)
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
            this.guard.name(name),
            values,
            context
        );
    }

    private final FilteredProviderGuard<SpreadsheetComparatorName, ?> guard;

    private final SpreadsheetComparatorProvider provider;

    @Override
    public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
        return this.infos;
    }

    private final SpreadsheetComparatorInfoSet infos;

    @Override
    public String toString() {
        return this.provider.toString();
    }
}
