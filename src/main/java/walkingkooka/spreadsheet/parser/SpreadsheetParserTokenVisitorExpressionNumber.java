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

import walkingkooka.ToStringBuilder;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A {@link SpreadsheetParserTokenVisitor} that accepts a {@link SpreadsheetNumberParserToken} and creates a {@link ExpressionNumber}
 */
final class SpreadsheetParserTokenVisitorExpressionNumber extends SpreadsheetParserTokenVisitor {

    /**
     * Creates a {@link SpreadsheetParserTokenVisitorExpressionNumber}, that collects and translates symbols into a {@link String}
     * which is then parsed by {@link ExpressionNumberKind#parse(String)}.
     */
    static ExpressionNumber toExpressionNumber(final SpreadsheetParentParserToken token,
                                               final ExpressionEvaluationContext context) {
        Objects.requireNonNull(context, "context");

        final SpreadsheetParserTokenVisitorExpressionNumber visitor = new SpreadsheetParserTokenVisitorExpressionNumber();
        visitor.accept(token);
        return context.expressionNumberKind().parse(visitor.number.toString());
    }

    // @VisibleForTesting
    SpreadsheetParserTokenVisitorExpressionNumber() {
        super();
    }

    // visit....................................................................................................
    // ignore all SymbolParserTokens, dont bother to collect them.

    @Override
    protected void visit(final SpreadsheetDecimalSeparatorSymbolParserToken token) {
        this.number.append('.');
    }

    @Override
    protected void visit(final SpreadsheetDigitsParserToken token) {
        this.number.append(token.text());
    }

    @Override
    protected void visit(final SpreadsheetExponentSymbolParserToken token) {
        this.number.append('E');
    }

    @Override
    protected void visit(final SpreadsheetMinusSymbolParserToken token) {
        this.number.append('-');
    }

    @Override
    protected void visit(final SpreadsheetPlusSymbolParserToken token) {
        this.number.append('+');
    }

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
