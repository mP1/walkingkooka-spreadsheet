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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberConverterContexts;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.text.TextNode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetFormatterContextTest implements SpreadsheetFormatterContextTesting<BasicSpreadsheetFormatterContext> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    private final static Locale LOCALE = Locale.CANADA_FRENCH;

    private final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    private final int GENERAL_NUMBER_FORMAT_DIGIT_COUNT = 8;

    @Test
    public void testWithNullNumberToColorFails() {
        this.withFails(
                null,
                this.nameToColor(),
                CELL_CHARACTER_WIDTH,
                GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                this.formatter(),
                this.converterContext()
        );
    }

    @Test
    public void testWithNullNameToColorFails() {
        this.withFails(
                this.numberToColor(),
                null,
                CELL_CHARACTER_WIDTH,
                GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                this.formatter(),
                this.converterContext()
        );
    }

    @Test
    public void testWithInvalidCellCharacterWidthFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BasicSpreadsheetFormatterContext.with(this.numberToColor(),
                        this.nameToColor(),
                        -1,
                        GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                        this.formatter(),
                        this.converterContext()
                )
        );
    }

    @Test
    public void testWithInvalidCellCharacterWidthFails2() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BasicSpreadsheetFormatterContext.with(this.numberToColor(),
                        this.nameToColor(),
                        0,
                        GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                        this.formatter(),
                        this.converterContext())
        );
    }

    @Test
    public void testWithInvalidGeneralFormatNumberDigitCountFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BasicSpreadsheetFormatterContext.with(this.numberToColor(),
                        this.nameToColor(),
                        CELL_CHARACTER_WIDTH,
                        -1,
                        this.formatter(),
                        this.converterContext()
                )
        );
    }

    @Test
    public void testWithInvalidGeneralFormatNumberDigitCountFails2() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BasicSpreadsheetFormatterContext.with(this.numberToColor(),
                        this.nameToColor(),
                        CELL_CHARACTER_WIDTH,
                        0,
                        this.formatter(),
                        this.converterContext())
        );
    }

    @Test
    public void testWithNullFormatterFails() {
        this.withFails(
                this.numberToColor(),
                this.nameToColor(),
                CELL_CHARACTER_WIDTH,
                GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                null,
                this.converterContext()
        );
    }

    @Test
    public void testWIthNullConverterContextFails() {
        this.withFails(
                this.numberToColor(),
                this.nameToColor(),
                CELL_CHARACTER_WIDTH,
                GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                this.formatter(),
                null
        );
    }

    private void withFails(final Function<Integer, Optional<Color>> numberToColor,
                           final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                           final int width,
                           final int generalNumberFormatDigitCount,
                           final SpreadsheetFormatter formatter,
                           final SpreadsheetConverterContext converterContext) {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetFormatterContext.with(
                        numberToColor,
                        nameToColor,
                        width,
                        generalNumberFormatDigitCount,
                        formatter,
                        converterContext
                )
        );
    }

    @Test
    public void testColorNumber() {
        this.colorNumberAndCheck(this.createContext(), 1, Optional.of(this.color()));
    }

    @Test
    public void testColorName() {
        this.colorNameAndCheck(this.createContext(),
                SpreadsheetColorName.with("bingo"),
                Optional.of(this.color()));
    }

    @Test
    public void testConvertNumberOneToBoolean() {
        this.convertAndCheck(
                1,
                Boolean.class,
                Boolean.TRUE
        );
    }

    @Test
    public void testConvertNumberZeroToBoolean() {
        this.convertAndCheck(
                0,
                Boolean.class,
                Boolean.FALSE
        );
    }

    @Test
    public void testConvertSpreadsheetErrorMissingCellToNumber() {
        this.convertAndCheck(
                SpreadsheetError.selectionNotFound(
                        SpreadsheetSelection.parseCell("Z99")
                ),
                ExpressionNumber.class,
                EXPRESSION_NUMBER_KIND.zero()
        );
    }

    @Test
    public void testConvertSpreadsheetErrorToString() {
        final SpreadsheetErrorKind kind = SpreadsheetErrorKind.DIV0;

        this.convertAndCheck(
                kind.setMessage("Message is ignored!"),
                String.class,
                kind.text()
        );
    }

    @Test
    public void testFormat() {
        this.formatAndCheck(
                BigDecimal.valueOf(12.5),
                SpreadsheetText.with("012.500")
        );
    }

    @Test
    public void testLocale() {
        this.hasLocaleAndCheck(this.createContext(), LOCALE);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createContext(),
                "cellCharacterWidth=1 numberToColor=1=#123456 nameToColor=bingo=#123456 context=Truthy BigDecimal|BigInteger|Byte|Short|Integer|Long|Float|Double->Boolean | SpreadsheetError->Number " + LABEL_NAME_RESOLVER + " locale=\"fr-CA\" twoDigitYear=50 \"$$\" '!' \"E\" 'G' 'N' 'P' 'L' fr_CA precision=7 roundingMode=HALF_EVEN DOUBLE"
        );
    }

    @Override
    public BasicSpreadsheetFormatterContext createContext() {
        return BasicSpreadsheetFormatterContext.with(this.numberToColor(),
                this.nameToColor(),
                CELL_CHARACTER_WIDTH,
                GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                this.formatter(),
                this.converterContext());
    }

    private Function<Integer, Optional<Color>> numberToColor() {
        return new Function<>() {

            @Override
            public Optional<Color> apply(final Integer number) {
                checkEquals(number, 1, "color number");
                return Optional.of(BasicSpreadsheetFormatterContextTest.this.color());
            }

            @Override
            public String toString() {
                return 1 + "=" + BasicSpreadsheetFormatterContextTest.this.color();
            }
        };
    }

    private Function<SpreadsheetColorName, Optional<Color>> nameToColor() {
        return new Function<>() {

            @Override
            public Optional<Color> apply(final SpreadsheetColorName name) {
                checkEquals(name, SpreadsheetColorName.with("bingo"), "color name");
                return Optional.of(BasicSpreadsheetFormatterContextTest.this.color());
            }

            @Override
            public String toString() {
                return "bingo=" + BasicSpreadsheetFormatterContextTest.this.color();
            }
        };
    }

    private Color color() {
        return Color.fromRgb(0x123456);
    }

    private final static int CELL_CHARACTER_WIDTH = 1;

    private SpreadsheetFormatter formatter() {
        return new SpreadsheetFormatter() {
            @Override
            public boolean canFormat(final Object value,
                                     final SpreadsheetFormatterContext context) {
                return value instanceof BigDecimal;
            }

            @Override
            public Optional<TextNode> format(final Object value,
                                             final SpreadsheetFormatterContext context) {
                return Optional.of(
                        SpreadsheetText.with(
                                new DecimalFormat("000.000")
                                        .format(value)
                        ).toTextNode()
                );
            }

            @Override
            public String toString() {
                return BasicSpreadsheetFormatterContextTest.class.getSimpleName() + ".formatter()";
            }
        };
    }

    private SpreadsheetConverterContext converterContext() {
        return SpreadsheetConverterContexts.basic(
                Converters.collection(
                        Lists.of(
                                Converters.numberToBoolean(),
                                SpreadsheetConverters.errorToNumber()
                        )
                ),
                LABEL_NAME_RESOLVER,
                ExpressionNumberConverterContexts.basic(
                        Converters.fake(),
                        ConverterContexts.basic(Converters.fake(),
                                this.dateTimeContext(),
                                this.decimalNumberContext()),
                        EXPRESSION_NUMBER_KIND
                )
        );
    }

    private DateTimeContext dateTimeContext() {
        return DateTimeContexts.locale(
                LOCALE,
                1900,
                50,
                LocalDateTime::now
        );
    }

    private DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(this.currencySymbol(),
                this.decimalSeparator(),
                this.exponentSymbol(),
                this.groupSeparator(),
                this.negativeSign(),
                this.percentageSymbol(),
                this.positiveSign(),
                LOCALE,
                this.mathContext());
    }

    @Override
    public String currencySymbol() {
        return "$$";
    }

    @Override
    public char decimalSeparator() {
        return '!';
    }

    @Override
    public String exponentSymbol() {
        return "E";
    }

    @Override
    public char groupSeparator() {
        return 'G';
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    @Override
    public char negativeSign() {
        return 'N';
    }

    @Override
    public char percentageSymbol() {
        return 'P';
    }

    @Override
    public char positiveSign() {
        return 'L';
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<BasicSpreadsheetFormatterContext> type() {
        return BasicSpreadsheetFormatterContext.class;
    }
}
