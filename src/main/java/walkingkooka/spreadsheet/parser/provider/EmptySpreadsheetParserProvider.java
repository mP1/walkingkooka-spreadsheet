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
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetParserProvider} that is empty and always returns nothing when queried.
 */
final class EmptySpreadsheetParserProvider implements SpreadsheetParserProvider {

    /**
     * Singleton.
     */
    final static EmptySpreadsheetParserProvider INSTANCE = new EmptySpreadsheetParserProvider();

    private EmptySpreadsheetParserProvider() {
        super();
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                               final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");
        Objects.requireNonNull(context, "context");

        throw new IllegalArgumentException("Unknown parser " + selector.name());
    }

    @Override
    public SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                               final List<?> values,
                                               final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        throw new IllegalArgumentException("Unknown parser " + name);
    }

    @Override
    public Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        throw new IllegalArgumentException("Unknown parser " + selector.name());
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector) {
        Objects.requireNonNull(selector, "selector");

        throw new IllegalArgumentException("Unknown parser " + selector.name());
    }

    @Override
    public SpreadsheetParserInfoSet spreadsheetParserInfos() {
        return SpreadsheetParserInfoSet.EMPTY;
    }
}
