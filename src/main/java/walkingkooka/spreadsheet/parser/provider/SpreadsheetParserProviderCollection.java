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

import walkingkooka.plugin.ProviderCollection;
import walkingkooka.plugin.ProviderCollectionProviderGetter;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetParserProvider} view of a collection of {@link SpreadsheetParserProvider providers}.
 */
final class SpreadsheetParserProviderCollection implements SpreadsheetParserProvider {

    static SpreadsheetParserProviderCollection with(final Set<SpreadsheetParserProvider> providers) {
        return new SpreadsheetParserProviderCollection(
            Objects.requireNonNull(providers, "providers")
        );
    }

    private SpreadsheetParserProviderCollection(final Set<SpreadsheetParserProvider> providers) {
        this.providers = ProviderCollection.with(
            new ProviderCollectionProviderGetter<>() {
                @Override
                public SpreadsheetParser get(final SpreadsheetParserProvider provider,
                                             final SpreadsheetParserName name,
                                             final List<?> values,
                                             final ProviderContext context) {
                    return provider.spreadsheetParser(
                        name,
                        values,
                        context
                    );
                }

                @Override
                public SpreadsheetParser get(final SpreadsheetParserProvider provider,
                                             final SpreadsheetParserSelector selector,
                                             final ProviderContext context) {
                    return provider.spreadsheetParser(
                        selector,
                        context
                    );
                }
            },
            SpreadsheetParserProvider::spreadsheetParserInfos,
            SpreadsheetParser.class.getSimpleName(),
            providers
        );
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                               final ProviderContext context) {
        final SpreadsheetParser parser = this.providers.get(
            selector,
            context
        );
        if (null == parser) {
            throw new IllegalArgumentException("Unknown parser " + selector.name());
        }
        return parser;
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values,
                                               final ProviderContext context) {
        final SpreadsheetParser parser = this.providers.get(
            name,
            values,
            context
        );
        if (null == parser) {
            throw new IllegalArgumentException("Unknown parser " + name);
        }
        return parser;
    }

    @Override
    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        return NO_NEXT_TOKEN;
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        return NO_SPREADSHEET_FORMATTER_SELECTOR;
    }

    @Override
    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
        return SpreadsheetParserInfoSet.with(
            this.providers.infos()
        );
    }

    private final ProviderCollection<SpreadsheetParserProvider, SpreadsheetParserName, SpreadsheetParserInfo, SpreadsheetParserSelector, SpreadsheetParser> providers;

    @Override
    public String toString() {
        return this.providers.toString();
    }
}
