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

package walkingkooka.spreadsheet.parser;

import walkingkooka.plugin.ProviderCollection;
import walkingkooka.plugin.ProviderCollectionProviderGetter;

import java.util.List;
import java.util.Objects;
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
                                                 final List<?> values) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public SpreadsheetParser get(final SpreadsheetParserProvider provider,
                                                 final SpreadsheetParserSelector selector) {
                        return provider.spreadsheetParser(
                                selector
                        );
                    }
                },
                SpreadsheetParserProvider::spreadsheetParserInfos,
                SpreadsheetParser.class.getSimpleName(),
                providers
        );
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        final SpreadsheetParser parser = this.providers.get(selector);
        if(null == parser) {
            throw new IllegalArgumentException("Unknown parser " + selector.name());
        }
        return parser;
    }

    @Override
    public Set<SpreadsheetParserInfo> spreadsheetParserInfos() {
        return this.providers.infos();
    }

    private final ProviderCollection<SpreadsheetParserProvider, SpreadsheetParserName, SpreadsheetParserInfo, SpreadsheetParserSelector, SpreadsheetParser> providers;

    @Override
    public String toString() {
        return this.providers.toString();
    }
}
