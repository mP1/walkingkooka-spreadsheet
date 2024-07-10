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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.text.cursor.parser.ParserContext;
import walkingkooka.text.cursor.parser.ParserContexts;

/**
 * Base {@link SpreadsheetNumberParsePatternComponent} for all non digit {@link SpreadsheetNumberParsePatternComponent}.
 */
abstract class SpreadsheetNumberParsePatternComponentNonDigit extends SpreadsheetNumberParsePatternComponent {

    /**
     * A {@link ParserContext} used by any {@link walkingkooka.text.cursor.parser.Parser}.
     */
    final static ParserContext PARSER_CONTEXT = ParserContexts.fake();

    SpreadsheetNumberParsePatternComponentNonDigit() {
        super();
    }

    /**
     * This method should never be invoked on non digit textComponents.
     */
    @Override
    final SpreadsheetNumberParsePatternComponent lastDigit(final SpreadsheetNumberParsePatternComponentDigitMode mode) {
        throw new UnsupportedOperationException();
    }
}
