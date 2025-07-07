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

import walkingkooka.spreadsheet.SpreadsheetStrings;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.cursor.TextCursor;

/**
 * A {@link SpreadsheetNumberParsePatternComponent} that matches an exponent and switches parsing to exponent parsing mode.
 */
final class SpreadsheetNumberParsePatternComponentExponent extends SpreadsheetNumberParsePatternComponentNonDigit {

    /**
     * Singleton
     */
    final static SpreadsheetNumberParsePatternComponentExponent INSTANCE = new SpreadsheetNumberParsePatternComponentExponent();

    /**
     * Private ctor use singleton.
     */
    private SpreadsheetNumberParsePatternComponentExponent() {
        super();
    }

    @Override
    boolean isExpressionCompatible() {
        return true;
    }

    @Override
    boolean parse(final TextCursor cursor,
                  final SpreadsheetNumberParsePatternRequest request) {
        return this.parseToken(
            cursor,
            request.context.exponentSymbol(),
            SpreadsheetStrings.CASE_SENSITIVITY,
            SpreadsheetFormulaParserToken::exponentSymbol,
            SpreadsheetNumberParsePatternComponentDigitMode.EXPONENT_START,
            request
        );
    }

    @Override
    public String toString() {
        return "E";
    }
}
