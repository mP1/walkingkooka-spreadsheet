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

import walkingkooka.ToStringBuilder;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A {@link SpreadsheetFormulaParserTokenVisitor} that accepts a {@link NumberSpreadsheetFormulaParserToken} and creates a {@link ExpressionNumber}
 */
final class NumberSpreadsheetFormulaParserTokenToNumberSpreadsheetFormulaParserTokenVisitor extends SpreadsheetFormulaParserTokenVisitor {

    /**
     * Creates a {@link NumberSpreadsheetFormulaParserTokenToNumberSpreadsheetFormulaParserTokenVisitor}, that collects and translates symbols into a {@link String}
     * which is then parsed by {@link ExpressionNumberKind#parse(String)}.
     * If the token includes a {@link PercentSymbolSpreadsheetFormulaParserToken} then the value will be divided by 100.
     */
    static ExpressionNumber toExpressionNumber(final NumberSpreadsheetFormulaParserToken token,
                                               final ExpressionNumberContext context) {
        Objects.requireNonNull(context, "context");

        final NumberSpreadsheetFormulaParserTokenToNumberSpreadsheetFormulaParserTokenVisitor visitor = new NumberSpreadsheetFormulaParserTokenToNumberSpreadsheetFormulaParserTokenVisitor();
        visitor.accept(token);

        final ExpressionNumberKind kind = context.expressionNumberKind();

        ExpressionNumber number = kind.parse(visitor.number.toString());

        final int percentage = visitor.percentageSymbolCounter;
        if (0 != percentage) {
            // percentage = 1 = 100%
            // percentage = 2 = 100% * 100%
            number = number.divide(
                kind.create(10)
                    .power(
                        kind.create(percentage * 2),
                        context
                    ),
                context
            );
        }

        return number;
    }

    // @VisibleForTesting
    NumberSpreadsheetFormulaParserTokenToNumberSpreadsheetFormulaParserTokenVisitor() {
        super();
        this.percentageSymbolCounter = 0;
    }

    // visit....................................................................................................
    // ignore all SymbolParserTokens, dont bother to collect them.

    @Override
    protected void visit(final DecimalSeparatorSymbolSpreadsheetFormulaParserToken token) {
        this.number.append('.');
    }

    @Override
    protected void visit(final DigitsSpreadsheetFormulaParserToken token) {
        this.number.append(token.text());
    }

    @Override
    protected void visit(final ExponentSymbolSpreadsheetFormulaParserToken token) {
        this.number.append('E');
    }

    @Override
    protected void visit(final MinusSymbolSpreadsheetFormulaParserToken token) {
        this.number.append('-');
    }

    @Override
    protected void visit(final PlusSymbolSpreadsheetFormulaParserToken token) {
        this.number.append('+');
    }

    @Override
    protected void visit(final PercentSymbolSpreadsheetFormulaParserToken token) {
        // count all the percent symbols in the number
        this.percentageSymbolCounter += token.text()
            .length();
    }

    private int percentageSymbolCounter;

    /**
     * Aggregates all the number important characters digits, signs, exponent etc, this will be parsed by {@link BigDecimal}.
     */
    private final StringBuilder number = new StringBuilder();

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .build();
    }
}
