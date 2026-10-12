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
import walkingkooka.color.CanIndexedNamedColor;
import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.spreadsheet.color.SpreadsheetColorContexts;
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

public final class SpreadsheetParserContextBasicTest implements ClassTesting2<SpreadsheetParserContextBasic>,
    SpreadsheetParserContextTesting2<SpreadsheetParserContextBasic>,
    DateTimeContextTesting,
    DecimalNumberContextDelegator,
    ToStringTesting<SpreadsheetParserContextBasic> {

    private final static CanIndexedNamedColor CAN_INDEXED_NAMED_COLOR = SpreadsheetColorContexts.spreadsheetMetadataDefaults();

    private final static BiFunction<Parser<?>, TextCursor, InvalidCharacterException> INVALID_CHARACTER_EXCEPTION_FACTORY = InvalidCharacterExceptionFactory.POSITION;

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

    private final static ExpressionNumberContext EXPRESSION_NUMBER_CONTEXT = ExpressionNumberContexts.basic(
        EXPRESSION_NUMBER_KIND,
        DecimalNumberContexts.basic(
            DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT,
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
    public void testWithNullCanIndexedNamedColorFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetParserContextBasic.with(
                null,
                INVALID_CHARACTER_EXCEPTION_FACTORY,
                DATE_TIME_CONTEXT,
                EXPRESSION_NUMBER_CONTEXT,
                VALUE_SEPARATOR)
        );
    }

    @Test
    public void testWithNullInvalidCharacterExceptionFactoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetParserContextBasic.with(
                CAN_INDEXED_NAMED_COLOR,
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
            () -> SpreadsheetParserContextBasic.with(
                CAN_INDEXED_NAMED_COLOR,
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
            () -> SpreadsheetParserContextBasic.with(
                CAN_INDEXED_NAMED_COLOR,
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
        final SpreadsheetParserContextBasic context = this.createContext();
        this.checkEquals(',', context.valueSeparator(), "valueSeparator");
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "canIndexedNamedColor=color1: black\n" +
                "color2: white\n" +
                "color3: red\n" +
                "color4: lime\n" +
                "color5: blue\n" +
                "color6: yellow\n" +
                "color7: magenta\n" +
                "color8: cyan\n" +
                "color9: maroon\n" +
                "color10: green\n" +
                "color11: navy\n" +
                "color12: olive\n" +
                "color13: purple\n" +
                "color14: teal\n" +
                "color15: silver\n" +
                "color16: grey\n" +
                "color17: #99f\n" +
                "color18: #936\n" +
                "color19: #ffc\n" +
                "color20: #cff\n" +
                "color21: #606\n" +
                "color22: #ff8080\n" +
                "color23: #06c\n" +
                "color24: #ccf\n" +
                "color25: navy\n" +
                "color26: magenta\n" +
                "color27: yellow\n" +
                "color28: cyan\n" +
                "color29: purple\n" +
                "color30: maroon\n" +
                "color31: teal\n" +
                "color32: blue\n" +
                "color33: #0cf\n" +
                "color34: #cff\n" +
                "color35: #cfc\n" +
                "color36: #ff9\n" +
                "color37: #9cf\n" +
                "color38: #f9c\n" +
                "color39: #c9f\n" +
                "color40: #fc9\n" +
                "color41: #36f\n" +
                "color42: #3cc\n" +
                "color43: #9c0\n" +
                "color44: #fc0\n" +
                "color45: #f90\n" +
                "color46: #f60\n" +
                "color47: #669\n" +
                "color48: #969696\n" +
                "color49: #036\n" +
                "color50: #396\n" +
                "color51: #030\n" +
                "color52: #330\n" +
                "color53: #930\n" +
                "color54: #936\n" +
                "color55: #339\n" +
                "color56: #333\n" +
                "colorBlack: 1\n" +
                "colorBlue: 5\n" +
                "colorCyan: 8\n" +
                "colorGreen: 4\n" +
                "colorMagenta: 7\n" +
                "color invalidCharacterExceptionFactory=POSITION dateTimeContext=symbols=ampms=\"am\", \"pm\" monthNames=\"Janu"
        );
    }

    @Override
    public SpreadsheetParserContextBasic createContext() {
        return SpreadsheetParserContextBasic.with(
            CAN_INDEXED_NAMED_COLOR,
            INVALID_CHARACTER_EXCEPTION_FACTORY,
            DATE_TIME_CONTEXT,
            EXPRESSION_NUMBER_CONTEXT,
            VALUE_SEPARATOR
        );
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return DecimalNumberContext.DEFAULT_NUMBER_DIGIT_COUNT;
    }

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
    public Class<SpreadsheetParserContextBasic> type() {
        return SpreadsheetParserContextBasic.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
