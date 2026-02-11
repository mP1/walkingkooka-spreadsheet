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
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        return selector.evaluateValueText(
            this,
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

        final SpreadsheetComparatorSelector selector = aliases.aliasSelector(name)
            .orElse(null);
        if (null != selector) {
            if (false == values.isEmpty()) {
                throw new IllegalArgumentException("Alias " + name + " should have no values");
            }
            comparator = provider.spreadsheetComparator(
                selector,
                context
            );
        } else {
            final SpreadsheetComparatorSelector reversedSelector = aliases.aliasSelector(name.reversed())
                .orElse(null);
            if (null != reversedSelector) {
                if (false == values.isEmpty()) {
                    throw new IllegalArgumentException("Alias " + name + " should have no values");
                }
                // aliasSelector lookup was reversed so reversed the response and then lookup
                comparator = provider.spreadsheetComparator(
                    reversedSelector.setName(
                        reversedSelector.name()
                            .reversed()
                    ),
                    context
                );
            } else {
                SpreadsheetComparatorName aliasOrName = aliases.aliasOrName(name)
                    .orElse(null);
                if (null != aliasOrName) {
                    comparator = provider.spreadsheetComparator(
                        aliasOrName,
                        values,
                        context
                    );
                } else {
                    // aliasOrName with reversed, reverse the response then ask provider
                    comparator = provider.spreadsheetComparator(
                        aliases.aliasOrName(name.reversed())
                            .orElseThrow(() -> new IllegalArgumentException("Unknown comparator " + name))
                            .reversed(),
                        values,
                        context
                    );
                }
            }
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
