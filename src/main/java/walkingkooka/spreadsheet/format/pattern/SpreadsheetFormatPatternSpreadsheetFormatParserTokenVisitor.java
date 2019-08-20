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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGeneralSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;

/**
 * A base {@link SpreadsheetFormatParserTokenVisitor} for all {@link SpreadsheetFormatPattern} basically calling {@link #failInvalid(SpreadsheetFormatParserToken)}
 * when an invalid token is included such as HOURS within a DATE.
 */
abstract class SpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor extends SpreadsheetPatternSpreadsheetFormatParserTokenVisitor {

    SpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected final void visit(final SpreadsheetFormatGeneralSymbolParserToken token) {
        this.failInvalid(token);
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName();
    }
}
