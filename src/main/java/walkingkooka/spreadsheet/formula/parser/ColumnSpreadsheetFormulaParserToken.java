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
package walkingkooka.spreadsheet.formula.parser;

import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;

/**
 * A token that holds a column reference.
 */
public final class ColumnSpreadsheetFormulaParserToken extends NonSymbolSpreadsheetFormulaParserToken<SpreadsheetColumnReference>
    implements HasSpreadsheetReference<SpreadsheetColumnReference> {

    static ColumnSpreadsheetFormulaParserToken with(final SpreadsheetColumnReference value,
                                                    final String text) {
        return new ColumnSpreadsheetFormulaParserToken(
            checkValue(value),
            checkText(text)
        );
    }

    private ColumnSpreadsheetFormulaParserToken(final SpreadsheetColumnReference value, final String text) {
        super(value, text);
    }

    // SpreadsheetFormulaParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetFormulaParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // HasSpreadsheetReference..........................................................................................

    @Override
    public SpreadsheetColumnReference reference() {
        return this.value();
    }
}
