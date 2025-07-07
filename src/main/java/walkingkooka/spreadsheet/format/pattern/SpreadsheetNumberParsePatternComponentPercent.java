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

import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.cursor.TextCursor;

/**
 * A {@link SpreadsheetNumberParsePatternComponent} that matches any percent symbol.
 */
final class SpreadsheetNumberParsePatternComponentPercent extends SpreadsheetNumberParsePatternComponentNonDigit {

    /**
     * Singleton
     */
    final static SpreadsheetNumberParsePatternComponentPercent INSTANCE = new SpreadsheetNumberParsePatternComponentPercent();

    private SpreadsheetNumberParsePatternComponentPercent() {
        super();
    }

    @Override
    boolean isExpressionCompatible() {
        return true;
    }

    @Override
    boolean parse(final TextCursor cursor,
                  final SpreadsheetNumberParsePatternRequest request) {
        final char percentSymbol = request.context.percentSymbol();

        return this.parseToken(
            cursor,
            Character.toString(percentSymbol),
            CaseSensitivity.SENSITIVE,
            SpreadsheetFormulaParserToken::percentSymbol,
            null, // dont update mode
            request
        );
    }

    @Override
    public String toString() {
        return ".";
    }
}
