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

package walkingkooka.spreadsheet.format;

import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.ProviderContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetFormatterProvider} that uses the given aliases definition and {@link SpreadsheetFormatterProvider}
 * supporting aliases.
 */
final class AliasesSpreadsheetFormatterProvider implements SpreadsheetFormatterProvider {

    static AliasesSpreadsheetFormatterProvider with(final SpreadsheetFormatterAliasSet aliases,
                                                    final SpreadsheetFormatterProvider provider) {
        return new AliasesSpreadsheetFormatterProvider(
                Objects.requireNonNull(aliases, "aliases"),
                Objects.requireNonNull(provider, "provider")
        );
    }

    private AliasesSpreadsheetFormatterProvider(final SpreadsheetFormatterAliasSet aliases,
                                                final SpreadsheetFormatterProvider provider) {
        this.aliases = aliases;
        this.provider = provider;

        this.infos = aliases.merge(provider.spreadsheetFormatterInfos());
    }

    @Override
    public SpreadsheetFormatter spreadsheetFormatter(final SpreadsheetFormatterSelector selector,
                                                     final ProviderContext context) {
        return this.provider.spreadsheetFormatter(
                this.aliases.selector(selector),
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

        SpreadsheetFormatter formatter;

        final SpreadsheetFormatterAliasSet aliases = this.aliases;
        final SpreadsheetFormatterProvider provider = this.provider;

        final Optional<SpreadsheetFormatterSelector> selector = aliases.aliasSelector(name);
        if (selector.isPresent()) {
            if (false == values.isEmpty()) {
                throw new IllegalArgumentException("Alias " + name + " should have no values");
            }
            // assumes that $provider caches selectors to function
            formatter = provider.spreadsheetFormatter(
                    selector.get(),
                    context
            );
        } else {
            formatter = provider.spreadsheetFormatter(
                    aliases.aliasOrName(name)
                            .orElseThrow(() -> new IllegalArgumentException("Unknown formatter " + name)),
                    values,
                    context
            );
        }

        return formatter;
    }

    @Override
    public Optional<SpreadsheetFormatterSelectorToken> spreadsheetFormatterNextToken(final SpreadsheetFormatterSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final SpreadsheetFormatterName name = selector.name();

        return this.aliases.aliasOrName(name)
                .flatMap(n -> this.provider.spreadsheetFormatterNextToken(selector.setName(n)));
    }

    @Override
    public List<SpreadsheetFormatterSample> spreadsheetFormatterSamples(final SpreadsheetFormatterName name,
                                                                        final SpreadsheetFormatterProviderSamplesContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(context, "context");

        // return empty list if unknown alias/name
        return this.aliases.aliasOrName(name)
                .map(n -> this.provider.spreadsheetFormatterSamples(n, context))
                .orElse(Lists.empty())
                .stream()
                .map(s -> s.setSelector(s.selector().setName(name)))
                .collect(Collectors.toList());
    }

    private final SpreadsheetFormatterAliasSet aliases;

    private final SpreadsheetFormatterProvider provider;

    @Override
    public SpreadsheetFormatterInfoSet spreadsheetFormatterInfos() {
        return this.infos;
    }

    private final SpreadsheetFormatterInfoSet infos;

    @Override
    public String toString() {
        return this.spreadsheetFormatterInfos().toString();
    }
}
