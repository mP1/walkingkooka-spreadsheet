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
import walkingkooka.math.DecimalNumberContext;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A {@link SpreadsheetParserContext} without any functionality.
 */
final class BasicSpreadsheetParserContext implements SpreadsheetParserContext {

    /**
     * Creates a new {@link BasicSpreadsheetParserContext}.
     */
    static BasicSpreadsheetParserContext with(final DateTimeContext dateTimeContext,
                                              final DecimalNumberContext decimalNumberContext) {
        Objects.requireNonNull(dateTimeContext, "dateTimeContext");
        Objects.requireNonNull(decimalNumberContext, "decimalNumberContext");

        return new BasicSpreadsheetParserContext(dateTimeContext, decimalNumberContext);
    }

    /**
     * Private ctor use factory
     */
    private BasicSpreadsheetParserContext(final DateTimeContext dateTimeContext,
                                          final DecimalNumberContext decimalNumberContext) {
        super();
        this.dateTimeContext = dateTimeContext;
        this.decimalNumberContext = decimalNumberContext;
    }

    // DateTimeContext..................................................................................................

    @Override
    public List<String> ampms() {
        return this.dateTimeContext.ampms();
    }

    @Override
    public List<String> monthNames() {
        return this.dateTimeContext.monthNames();
    }

    @Override
    public List<String> monthNameAbbreviations() {
        return this.dateTimeContext.monthNameAbbreviations();
    }

    @Override
    public int twoDigitYear() {
        return this.dateTimeContext.twoDigitYear();
    }

    @Override
    public List<String> weekDayNames() {
        return this.dateTimeContext.weekDayNames();
    }

    @Override
    public List<String> weekDayNameAbbreviations() {
        return this.dateTimeContext.weekDayNameAbbreviations();
    }

    private final DateTimeContext dateTimeContext;

    // DecimalNumberContext.............................................................................................

    @Override
    public String currencySymbol() {
        return this.decimalNumberContext.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return this.decimalNumberContext.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return this.decimalNumberContext.exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return this.decimalNumberContext.groupingSeparator();
    }

    @Override
    public char negativeSign() {
        return this.decimalNumberContext.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return this.decimalNumberContext.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return this.decimalNumberContext.positiveSign();
    }

    @Override
    public Locale locale() {
        return this.decimalNumberContext.locale();
    }

    @Override
    public MathContext mathContext() {
        return this.decimalNumberContext.mathContext();
    }

    private final DecimalNumberContext decimalNumberContext;

    @Override
    public String toString() {
        return this.dateTimeContext + " " + this.decimalNumberContext;
    }
}
