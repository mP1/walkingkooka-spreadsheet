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
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.math.MathContext;
import java.util.Locale;

public final class BasicSpreadsheetParserContextTest implements ClassTesting2<BasicSpreadsheetParserContext>,
        SpreadsheetParserContextTesting<BasicSpreadsheetParserContext> {

    private final static String CURRENCY = "$$";
    private final static char DECIMAL = 'D';
    private final static String EXPONENT = "X";
    private final static char GROUPING = 'G';
    private final static char NEGATIVE = 'N';
    private final static char PERCENTAGE = 'R';
    private final static char POSITIVE = 'P';
    private final static Locale LOCALE = Locale.CANADA_FRENCH;
    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL32;

    @Test
    public void testLocale() {
        this.hasLocaleAndCheck(this.createContext(), LOCALE);
    }

    @Override
    public BasicSpreadsheetParserContext createContext() {
        return BasicSpreadsheetParserContext.with(
                DateTimeContexts.fake(),
                DecimalNumberContexts.basic(CURRENCY,
                        DECIMAL,
                        EXPONENT,
                        GROUPING,
                        NEGATIVE,
                        PERCENTAGE,
                        POSITIVE,
                        LOCALE,
                        MATH_CONTEXT));
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
