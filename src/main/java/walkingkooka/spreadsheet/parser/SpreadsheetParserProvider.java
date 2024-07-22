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

import walkingkooka.plugin.Provider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    SpreadsheetParser spreadsheetParser(final SpreadsheetParserSelector selector);

    /**
     * Resolves the given {@link SpreadsheetParserName} and values to a {@link SpreadsheetParser}.
     */
    SpreadsheetParser spreadsheetParser(final SpreadsheetParserName name,
                                        final List<?> values);

    /**
     * Constant for {@link #spreadsheetParserNextTextComponent(SpreadsheetParserSelector)} when there is no next.
     */
    Optional<SpreadsheetParserSelectorTextComponent> NO_NEXT_TEXT_COMPONENT = Optional.empty();
    
    /**
     * Returns the next {@link SpreadsheetParserSelectorTextComponent} for the given {@link SpreadsheetParserSelector}.
     */
    Optional<SpreadsheetParserSelectorTextComponent> spreadsheetParserNextTextComponent(final SpreadsheetParserSelector selector);

    /**
     * Returns the equivalent {@link SpreadsheetFormatterSelector} if one is present for the given {@link SpreadsheetParserSelector}.
     */
    Optional<SpreadsheetFormatterSelector> spreadsheetParserSpreadsheetFormatter(final SpreadsheetParserSelector selector);

    /**
     * Returns all available {@link SpreadsheetParserInfo}
     */
    Set<SpreadsheetParserInfo> spreadsheetParserInfos();
}
