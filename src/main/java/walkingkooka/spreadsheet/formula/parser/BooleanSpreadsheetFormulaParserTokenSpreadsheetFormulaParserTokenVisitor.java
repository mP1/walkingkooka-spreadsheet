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

import java.util.Objects;

/**
 * A {@link SpreadsheetFormulaParserTokenVisitor} that accepts a {@link BooleanSpreadsheetFormulaParserTokenSpreadsheetFormulaParserTokenVisitor} finds the embedded {@link BooleanLiteralSpreadsheetFormulaParserToken}
 */
final class BooleanSpreadsheetFormulaParserTokenSpreadsheetFormulaParserTokenVisitor extends SpreadsheetFormulaParserTokenVisitor {

    /**
     * Creates a {@link BooleanSpreadsheetFormulaParserTokenSpreadsheetFormulaParserTokenVisitor}, that finds the {@link BooleanLiteralSpreadsheetFormulaParserToken}.
     */
    static boolean toBoolean(final BooleanSpreadsheetFormulaParserToken token) {
        Objects.requireNonNull(token, "token");

        final BooleanSpreadsheetFormulaParserTokenSpreadsheetFormulaParserTokenVisitor visitor = new BooleanSpreadsheetFormulaParserTokenSpreadsheetFormulaParserTokenVisitor();
        visitor.accept(token);
        return visitor.booleanValue;
    }

    // @VisibleForTesting
    BooleanSpreadsheetFormulaParserTokenSpreadsheetFormulaParserTokenVisitor() {
        super();
    }

    @Override
    protected void visit(final BooleanLiteralSpreadsheetFormulaParserToken token) {
        this.booleanValue = token.value();
    }

    private Boolean booleanValue = null;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return "booleanValue: " + this.booleanValue;
    }
}
