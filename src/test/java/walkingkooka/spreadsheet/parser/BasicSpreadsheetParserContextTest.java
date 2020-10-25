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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetParserContextTest implements ClassTesting2<BasicSpreadsheetParserContext>,
        SpreadsheetParserContextTesting<BasicSpreadsheetParserContext>,
        ToStringTesting<BasicSpreadsheetParserContext> {

    private final static DateTimeContext DATE_TIME_CONTEXT = DateTimeContexts.fake();

    private final static String CURRENCY = "$$";
    private final static char DECIMAL = 'D';
    private final static String EXPONENT = "X";
    private final static char GROUPING = 'G';
    private final static char NEGATIVE = 'N';
    private final static char PERCENTAGE = 'R';
    private final static char POSITIVE = 'P';
    private final static Locale LOCALE = Locale.CANADA_FRENCH;
    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL32;

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.basic(CURRENCY,
            DECIMAL,
            EXPONENT,
            GROUPING,
            NEGATIVE,
            PERCENTAGE,
            POSITIVE,
            LOCALE,
            MATH_CONTEXT);

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    @Test
    public void testWithNullDateTimeContextFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetParserContext.with(
                null,
                DECIMAL_NUMBER_CONTEXT,
                EXPRESSION_NUMBER_KIND));
    }

    @Test
    public void testWithNullDecimalNumberContextFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetParserContext.with(
                DATE_TIME_CONTEXT,
                null,
                EXPRESSION_NUMBER_KIND));
    }

    @Test
    public void testWithNullExpressionNumberContextFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetParserContext.with(
                DATE_TIME_CONTEXT,
                DECIMAL_NUMBER_CONTEXT,
                null));
    }

    @Test
    public void testLocale() {
        this.hasLocaleAndCheck(this.createContext(), LOCALE);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createContext(), DATE_TIME_CONTEXT + " " + DECIMAL_NUMBER_CONTEXT + " " + EXPRESSION_NUMBER_KIND);
    }

    @Override
    public BasicSpreadsheetParserContext createContext() {
        return BasicSpreadsheetParserContext.with(
                DATE_TIME_CONTEXT,
                DECIMAL_NUMBER_CONTEXT,
                EXPRESSION_NUMBER_KIND);
    }

    @Override
    public String currencySymbol() {
        return CURRENCY;
    }

    @Override
    public char decimalSeparator() {
        return DECIMAL;
    }

    @Override
    public String exponentSymbol() {
        return EXPONENT;
    }

    @Override
    public char groupingSeparator() {
        return GROUPING;
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    @Override
    public char negativeSign() {
        return NEGATIVE;
    }

    @Override
    public char percentageSymbol() {
        return PERCENTAGE;
    }

    @Override
    public char positiveSign() {
        return POSITIVE;
    }

    @Override
    public Class<BasicSpreadsheetParserContext> type() {
        return BasicSpreadsheetParserContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
