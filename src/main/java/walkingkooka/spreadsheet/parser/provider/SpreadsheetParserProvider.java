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

import walkingkooka.plugin.Provider;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParser;

import java.util.List;
import java.util.Optional;

/**
 * A provider supports listing available {@link SpreadsheetParserInfo} and fetching implementations using a
 * {@link SpreadsheetParserSelector}, which is a simple combination of a {@link SpreadsheetParserName} and a pattern or string parameter.
 * <pre>
 * dd/mmm/yyyy
 * </pre>.
 */
public interface SpreadsheetParserProvider extends Provider {

    /**
     * Resolves the given {@link SpreadsheetParserSelector} to a {@link SpreadsheetParser}.
     */
    SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector,
                                        final ProviderContext context);

    /**
     * Resolves the given {@link SpreadsheetParserName} and values to a {@link SpreadsheetParser}.
     */
    SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                        final List<?> values,
                                        final ProviderContext context);

    /**
     * Constant for {@link #spreadsheetParserNextToken(SpreadsheetParserSelector)} when there is no next.
     */
    Optional<SpreadsheetParserSelectorToken> NO_NEXT_TOKEN = Optional.empty();

    /**
     * Returns the next {@link SpreadsheetParserSelectorToken} for the given {@link SpreadsheetParserSelector}.
     */
    Optional<SpreadsheetParserSelectorToken> spreadsheetParserNextToken(final SpreadsheetParserSelector selector);

    /**
     * A constant when no equivalent {@link SpreadsheetFormatterSelector} is available for a {@link SpreadsheetFormatterSelector}.
     */
    Optional<SpreadsheetFormatterSelector> NO_SPREADSHEET_FORMATTER_SELECTOR = Optional.empty();

    /**
     * Returns the equivalent {@link SpreadsheetFormatterSelector} if one is present for the given {@link SpreadsheetParserSelector}.
     * <br>
     * This will be useful for a UI where a user is building or selecting a {@link SpreadsheetParser}.
     * A table of formatted values would show the text that would be supported by a {@link SpreadsheetParser}.
     */
    Optional<SpreadsheetFormatterSelector> spreadsheetFormatterSelector(final SpreadsheetParserSelector selector);

    /**
     * Returns all available {@link SpreadsheetParserInfo}
     */
    SpreadsheetParserInfoSet spreadsheetParserInfos();
}
