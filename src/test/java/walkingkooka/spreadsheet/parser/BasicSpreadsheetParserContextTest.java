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
import walkingkooka.InvalidCharacterException;
import walkingkooka.ToStringTesting;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.expression.ExpressionNumberContext;
import walkingkooka.tree.expression.ExpressionNumberContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.Locale;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetParserContextTest implements ClassTesting2<BasicSpreadsheetParserContext>,
        SpreadsheetParserContextTesting<BasicSpreadsheetParserContext>,
        DecimalNumberContextDelegator,
        ToStringTesting<BasicSpreadsheetParserContext> {

    private final static BiFunction<Parser<?>, TextCursor, InvalidCharacterException> INVALID_CHARACTER_EXCEPTION_FACTORY = InvalidCharacterExceptionFactory.POSITION;

    private final static DateTimeContext DATE_TIME_CONTEXT = DateTimeContexts.fake();

    private final static String CURRENCY = "$$";
    private final static char DECIMAL = ':';
    private final static String EXPONENT = "^";
    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    private final static char GROUP_SEPARATOR = '/';
    private final static String INFINITY = "Infinity!";
    private final static char MONETARY_DECIMAL = ';';
    private final static String NAN = "Nan!";
    private final static char NEGATIVE = '!';
    private final static char PERCENTAGE = '#';
    private final static char PERMILL = '?';
    private final static char POSITIVE = '@';
    private final static char ZERO_DIGIT = '0';
    private final static Locale LOCALE = Locale.CANADA_FRENCH;
    private final static MathContext MATH_CONTEXT = MathContext.DECIMAL32;

    private final static ExpressionNumberContext EXPRESSION_NUMBER_CONTEXT = ExpressionNumberContexts.basic(
            EXPRESSION_NUMBER_KIND,
            DecimalNumberContexts.basic(
                    DecimalNumberSymbols.with(
                            NEGATIVE,
                            POSITIVE,
                            ZERO_DIGIT,
                            CURRENCY,
                            DECIMAL,
                            EXPONENT,
                            GROUP_SEPARATOR,
                            INFINITY,
                            MONETARY_DECIMAL,
                            NAN,
                            PERCENTAGE,
                            PERMILL
                    ),
                    LOCALE,
                    MATH_CONTEXT
            )
    );
    private final static char VALUE_SEPARATOR = ',';

    @Test
    public void testWithNullInvalidCharacterExceptionFactoryFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetParserContext.with(
                        null,
                        DATE_TIME_CONTEXT,
                        EXPRESSION_NUMBER_CONTEXT,
                        VALUE_SEPARATOR)
        );
    }

    @Test
    public void testWithNullDateTimeContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetParserContext.with(
                INVALID_CHARACTER_EXCEPTION_FACTORY,
                null,
                EXPRESSION_NUMBER_CONTEXT,
                VALUE_SEPARATOR)
        );
    }

    @Test
    public void testWithNullExpressionNumberContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetParserContext.with(
                INVALID_CHARACTER_EXCEPTION_FACTORY,
                DATE_TIME_CONTEXT,
                null,
                VALUE_SEPARATOR
                )
        );
    }

    @Test
    public void testLocale() {
        this.localeAndCheck(
                this.createContext(),
                LOCALE
        );
    }

    @Test
    public void testValueSeparator() {
        final BasicSpreadsheetParserContext context = this.createContext();
        this.checkEquals(',', context.valueSeparator(), "valueSeparator");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createContext(),
                INVALID_CHARACTER_EXCEPTION_FACTORY + " " + DATE_TIME_CONTEXT + " " + EXPRESSION_NUMBER_CONTEXT + " ','"
        );
    }

    @Override
    public BasicSpreadsheetParserContext createContext() {
        return BasicSpreadsheetParserContext.with(
                INVALID_CHARACTER_EXCEPTION_FACTORY,
                DATE_TIME_CONTEXT,
                EXPRESSION_NUMBER_CONTEXT,
                VALUE_SEPARATOR
        );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return EXPRESSION_NUMBER_CONTEXT;
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetParserContext> type() {
        return BasicSpreadsheetParserContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
