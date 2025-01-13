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
import walkingkooka.tree.expression.ExpressionNumberContextDelegator;

import java.util.Locale;
import java.util.Objects;

/**
 * A {@link SpreadsheetParserContext} without any functionality.
 */
final class BasicSpreadsheetParserContext implements SpreadsheetParserContext,
        DateTimeContextDelegator,
        ExpressionNumberContextDelegator {

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

    @Override
    public char valueSeparator() {
        return this.valueSeparator;
    }

    private final char valueSeparator;

    @Override
    public Locale locale() {
        return this.expressionNumberContext.locale();
    }

    // DateTimeContextDelegator.........................................................................................

    @Override
    public DateTimeContext dateTimeContext() {
        return this.dateTimeContext;
    }

    private final DateTimeContext dateTimeContext;

    // ExpressionNumberContext.............................................................................................

    @Override
    public ExpressionNumberContext expressionNumberContext() {
        return this.expressionNumberContext;
    }

    private final ExpressionNumberContext expressionNumberContext;

    @Override
    public String toString() {
        return this.dateTimeContext + " " + this.expressionNumberContext + " " + CharSequences.quoteIfChars(this.valueSeparator);
    }
}
