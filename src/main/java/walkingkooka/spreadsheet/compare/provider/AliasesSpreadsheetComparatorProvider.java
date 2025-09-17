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

package walkingkooka.spreadsheet.compare.provider;

import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetComparatorProvider} that uses the given aliases definition and {@link SpreadsheetComparatorProvider}
 * supporting aliases.
 */
final class AliasesSpreadsheetComparatorProvider implements SpreadsheetComparatorProvider {

    static AliasesSpreadsheetComparatorProvider with(final SpreadsheetComparatorAliasSet aliases,
                                                     final SpreadsheetComparatorProvider provider) {
        return new AliasesSpreadsheetComparatorProvider(
            Objects.requireNonNull(aliases, "aliases"),
            Objects.requireNonNull(provider, "provider")
        );
    }

    private AliasesSpreadsheetComparatorProvider(final SpreadsheetComparatorAliasSet aliases,
                                                 final SpreadsheetComparatorProvider provider) {
        this.aliases = aliases;
        this.provider = provider;

        this.infos = aliases.merge(provider.spreadsheetComparatorInfos());
    }

    @Override
    public SpreadsheetComparator<?> spreadsheetComparator(final SpreadsheetComparatorSelector selector,
                                                          final ProviderContext context) {
        return this.provider.spreadsheetComparator(
            this.aliases.selector(selector),
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

        SpreadsheetComparator<?> comparator;

        final SpreadsheetComparatorAliasSet aliases = this.aliases;
        final SpreadsheetComparatorProvider provider = this.provider;

        final Optional<SpreadsheetComparatorSelector> selector = aliases.aliasSelector(name);
        if (selector.isPresent()) {
            if (false == values.isEmpty()) {
                throw new IllegalArgumentException("Alias " + name + " should have no values");
            }
            // assumes that $provider caches selectors to comparator
            comparator = provider.spreadsheetComparator(
                selector.get(),
                context
            );
        } else {
            comparator = provider.spreadsheetComparator(
                aliases.aliasOrName(name)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown comparator " + name)),
                values,
                context
            );
        }

        return comparator;
    }

    private final SpreadsheetComparatorAliasSet aliases;

    private final SpreadsheetComparatorProvider provider;

    @Override
    public SpreadsheetComparatorInfoSet spreadsheetComparatorInfos() {
        return this.infos;
    }

    private final SpreadsheetComparatorInfoSet infos;

    @Override
    public String toString() {
        return this.spreadsheetComparatorInfos().toString();
    }
}
