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

import walkingkooka.plugin.Provider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.Parser;

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
     * Resolves the given {@link SpreadsheetParserSelector} to a {@link walkingkooka.text.cursor.parser.Parser}.
     */
    Optional<Parser<SpreadsheetParserContext>> spreadsheetParser(final SpreadsheetParserSelector selector);

    /**
     * Helper that invokes {@link #spreadsheetParser(SpreadsheetParserSelector)} and throws a {@link IllegalArgumentException}
     * if none was found.
     */
    default Parser<SpreadsheetParserContext> spreadsheetParserOrFail(final SpreadsheetParserSelector selector) {
        return this.spreadsheetParser(selector)
                .orElseThrow(() -> new IllegalArgumentException("Unknown parser " + selector.name()));
    }

    /**
     * Returns all available {@link SpreadsheetParserInfo}
     */
    Set<SpreadsheetParserInfo> spreadsheetParserInfos();
}
