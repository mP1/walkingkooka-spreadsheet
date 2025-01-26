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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;

/**
 * Represents a {@link SpreadsheetLabelName} within an expression.
 */
public final class SpreadsheetLabelNameParserToken extends SpreadsheetNonSymbolParserToken<SpreadsheetLabelName> implements SpreadsheetReferenceParserToken,
        HasSpreadsheetReference<SpreadsheetLabelName> {

    static SpreadsheetLabelNameParserToken with(final SpreadsheetLabelName value,
                                                final String text) {
        return new SpreadsheetLabelNameParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetLabelNameParserToken(final SpreadsheetLabelName value, final String text) {
        super(value, text);
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // HasSpreadsheetReference..........................................................................................

    @Override
    public SpreadsheetLabelName reference() {
        return this.value();
    }
}
