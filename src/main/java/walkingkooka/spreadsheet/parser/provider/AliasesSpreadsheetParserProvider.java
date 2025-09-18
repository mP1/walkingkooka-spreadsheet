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

package walkingkooka.spreadsheet.parser.provider;

import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParserProvider} that uses the given aliases definition and {@link SpreadsheetParserProvider}
 * supporting aliases.
 */
final class AliasesSpreadsheetParserProvider implements SpreadsheetParserProvider {

    static AliasesSpreadsheetParserProvider with(final SpreadsheetParserAliasSet aliases,
                                                 final SpreadsheetParserProvider provider) {
        return new AliasesSpreadsheetParserProvider(
            Objects.requireNonNull(aliases, "aliases"),
            Objects.requireNonNull(provider, "provider")
        );
    }

    private AliasesSpreadsheetParserProvider(final SpreadsheetParserAliasSet aliases,
                                             final SpreadsheetParserProvider provider) {
        this.aliases = aliases;
        this.provider = provider;

        this.infos = aliases.merge(provider.spreadsheetParserInfos());
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                               final ProviderContext context) {
        return this.provider.spreadsheetParser(
            this.aliases.selector(selector),
            context
        );
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values,
                                               final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        SpreadsheetParser parser;

        final SpreadsheetParserAliasSet aliases = this.aliases;
        final SpreadsheetParserProvider provider = this.provider;

        final Optional<SpreadsheetParserSelector> selector = aliases.aliasSelector(name);
        if (selector.isPresent()) {
            if (false == values.isEmpty()) {
                throw new IllegalArgumentException("Alias " + name + " should have no values");
            }
            // assumes that $provider caches selectors to function
            parser = provider.spreadsheetParser(
                selector.get(),
                context
            );
        } else {
            parser = provider.spreadsheetParser(
                aliases.aliasOrName(name)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown parser " + name)),
                values,
                context
            );
        }

        return parser;
    }

    @Override
    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final SpreadsheetParserName name = selector.name();

        return this.aliases.aliasOrName(name)
            .flatMap(n -> this.provider.spreadsheetParserNextToken(selector.setName(n)));
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        return this.aliases.aliasOrName(selector.name())
            .flatMap(
                n ->
                    this.provider.spreadsheetFormatterSelector(
                        selector.setName(n)
                    )
            );
    }

    private final SpreadsheetParserAliasSet aliases;

    private final SpreadsheetParserProvider provider;

    @Override
    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
        return this.infos;
    }

    private final SpreadsheetParserInfoSet infos;

    @Override
    public String toString() {
        return this.spreadsheetParserInfos().toString();
    }
}
