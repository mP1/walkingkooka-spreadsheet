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

import walkingkooka.spreadsheet.format.parser.GeneralSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} for all {@link SpreadsheetFormatPattern} that is used to validate a pattern
 * includes only supported tokens. Each subclass will call {@link SpreadsheetPatternSpreadsheetFormatParserTokenVisitor#failInvalid()} for invalid tokens.
 */
abstract class SpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor extends SpreadsheetPatternSpreadsheetFormatParserTokenVisitor {

    SpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected final void visit(final GeneralSymbolSpreadsheetFormatParserToken token) {
        // OK!
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName();
    }
}
