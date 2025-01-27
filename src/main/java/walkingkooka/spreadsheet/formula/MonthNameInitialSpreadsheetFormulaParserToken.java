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
package walkingkooka.spreadsheet.formula;

import java.util.Objects;

/**
 * Represents the month name initial within a date or date/time.
 */
public final class MonthNameInitialSpreadsheetFormulaParserToken extends NonSymbolSpreadsheetFormulaParserToken<Integer> {

    static MonthNameInitialSpreadsheetFormulaParserToken with(final int value, final String text) {
        Objects.requireNonNull(text, "text");

        return new MonthNameInitialSpreadsheetFormulaParserToken(value, text);
    }

    private MonthNameInitialSpreadsheetFormulaParserToken(final int value, final String text) {
        super(value, text);
    }

    // SpreadsheetFormulaParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetFormulaParserTokenVisitor visitor) {
        visitor.visit(this);
    }
}
