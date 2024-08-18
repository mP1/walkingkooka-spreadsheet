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

import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContextDelegator;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.Locale;
import java.util.Objects;

/**
 * A {@link SpreadsheetParserContext} without any functionality.
 */
final class BasicSpreadsheetParserContext implements SpreadsheetParserContext,
        DateTimeContextDelegator {

    /**
     * Creates a new {@link BasicSpreadsheetParserContext}.
     */
    static BasicSpreadsheetParserContext with(final DateTimeContext dateTimeContext,
                                              final ExpressionNumberContext expressionNumberContext,
                                              final char valueSeparator) {
        Objects.requireNonNull(dateTimeContext, "dateTimeContext");
        Objects.requireNonNull(expressionNumberContext, "expressionNumberContext");

        return new BasicSpreadsheetParserContext(
                dateTimeContext,
                expressionNumberContext,
                valueSeparator
        );
    }

    /**
     * Private ctor use factory
     */
    private BasicSpreadsheetParserContext(final DateTimeContext dateTimeContext,
                                          final ExpressionNumberContext expressionNumberContext,
                                          final char valueSeparator) {
        super();
        this.dateTimeContext = dateTimeContext;
        this.expressionNumberContext = expressionNumberContext;
        this.valueSeparator = valueSeparator;
    }

    // DateTimeContextDelegator.........................................................................................

    @Override
    public DateTimeContext dateTimeContext() {
        return this.dateTimeContext;
    }

    private final DateTimeContext dateTimeContext;

    // ExpressionNumberContext.............................................................................................

    @Override
    public String currencySymbol() {
        return this.expressionNumberContext.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.expressionNumberContext.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.expressionNumberContext.exponentSymbol();
    }

    @Override
    public ExpressionNumberKind expressionNumberKind() {
        return this.expressionNumberContext.expressionNumberKind();
    }

    @Override
    public char groupSeparator() {
        return this.expressionNumberContext.groupSeparator();
    }

    @Override
    public char negativeSign() {
        return this.expressionNumberContext.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return this.expressionNumberContext.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return this.expressionNumberContext.positiveSign();
    }

    @Override
    public Locale locale() {
        return this.expressionNumberContext.locale();
    }

    @Override
    public MathContext mathContext() {
        return this.expressionNumberContext.mathContext();
    }

    private final ExpressionNumberContext expressionNumberContext;

    @Override
    public char valueSeparator() {
        return this.valueSeparator;
    }

    private final char valueSeparator;

    @Override
    public String toString() {
        return this.dateTimeContext + " " + this.expressionNumberContext + " " + CharSequences.quoteIfChars(this.valueSeparator);
    }
}
