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

import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;

/**
 * A token that holds a column reference.
 */
public final class ColumnReferenceSpreadsheetParserToken extends NonSymbolSpreadsheetParserToken<SpreadsheetColumnReference>
        implements HasSpreadsheetReference<SpreadsheetColumnReference> {

    static ColumnReferenceSpreadsheetParserToken with(final SpreadsheetColumnReference value,
                                                      final String text) {
        return new ColumnReferenceSpreadsheetParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private ColumnReferenceSpreadsheetParserToken(final SpreadsheetColumnReference value, final String text) {
        super(value, text);
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // HasSpreadsheetReference..........................................................................................

    @Override
    public SpreadsheetColumnReference reference() {
        return this.value();
    }
}
