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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.text.TextNode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetFormatterContextTest implements SpreadsheetFormatterContextTesting<BasicSpreadsheetFormatterContext> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    private final static Locale LOCALE = Locale.FRANCE;

    private final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = new FakeSpreadsheetLabelNameResolver() {

        // variable length #toString and ToStringBuilder max length causes random test fails
        @Override
        public String toString() {
            return FakeSpreadsheetLabelNameResolver.class.getSimpleName();
        }
    };

    private final int GENERAL_NUMBER_FORMAT_DIGIT_COUNT = 8;

    private final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT =
            (cell) -> {
                Objects.requireNonNull(cell, "cell");
                throw new UnsupportedOperationException();
            };

    @Test
    public void testWithNullNumberToColorFails() {
        this.withFails(
                null,
                this.nameToColor(),
                CELL_CHARACTER_WIDTH,
                GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                this.formatter(),
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
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
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
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
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
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
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
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
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
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
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
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
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
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
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                null
        );
    }

    private void withFails(final Function<Integer, Optional<Color>> numberToColor,
                           final Function<SpreadsheetColorName, Optional<Color>> nameToColor,
                           final int width,
                           final int generalNumberFormatDigitCount,
                           final SpreadsheetFormatter formatter,
                           final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> spreadsheetExpressionEvaluationContext,
                           final SpreadsheetConverterContext converterContext) {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetFormatterContext.with(
                        numberToColor,
                        nameToColor,
                        width,
                        generalNumberFormatDigitCount,
                        formatter,
                        spreadsheetExpressionEvaluationContext,
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
        this.localeAndCheck(
                this.createContext(),
                LOCALE
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createContext(),
                "cellCharacterWidth=1 numberToColor=1=#123456 nameToColor=bingo=#123456 context=Character or CharSequence or HasText or String to Character or CharSequence or String | Number to Boolean | SpreadsheetError to Number FakeSpreadsheetLabelNameResolver symbols=ampms=\"AM\", \"PM\" monthNames=\"janvier\", \"février\", \"mars\", \"avril\", \"mai\", \"juin\", \"juillet\", \"août\", \"septembre\", \"octobre\", \"novembre\", \"décembre\" monthNameAbbreviations=\"janv.\", \"févr.\", \"mars\", \"avr.\", \"mai\", \"juin\", \"juil.\", \"août\", \"sept.\", \"oct.\", \"nov.\", \"déc.\" weekDayNames=\"dimanche\", \"lundi\", \"mardi\", \"mercredi\", \"jeudi\", \"vendredi\", \"samedi\" weekDayNameAbbreviations=\"dim.\", \"lun.\", \"mar.\", \"mer.\", \"jeu.\", \"ven.\", \"sam.\" locale=\"fr-FR\" twoDigitYear=50 negativeSign=';' positiveSign='^' zeroDigit='0' currencySymbol=\"$$\" decimalSeparator='!' exponentSymbol=\"EE\" groupSeparator='/' infinitySymbol=\"Infinity!\" monetaryDecimalSeparator='*' nanSymbol=\"Nan!\" percentSymbol=':' permillSymbol='>' fr_FR precisi"
        );
    }

    @Override
    public BasicSpreadsheetFormatterContext createContext() {
        return BasicSpreadsheetFormatterContext.with(
                this.numberToColor(),
                this.nameToColor(),
                CELL_CHARACTER_WIDTH,
                GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                this.formatter(),
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                this.converterContext()
        );
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
        return new FakeSpreadsheetFormatter() {

            @Override
            public Optional<TextNode> format(final Optional<Object> value,
                                             final SpreadsheetFormatterContext context) {
                return Optional.of(
                        SpreadsheetText.with(
                                new DecimalFormat("000.000")
                                        .format(
                                                value.orElse(null)
                                        )
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
                SpreadsheetConverterContexts.NO_METADATA,
                SpreadsheetConverterContexts.NO_VALIDATION_REFERENCE,
                Converters.collection(
                        Cast.to(
                                Lists.of(
                                        SpreadsheetConverters.textToText(),
                                        Converters.numberToBoolean(),
                                        SpreadsheetConverters.errorToNumber()
                                )
                        )
                ),
                LABEL_NAME_RESOLVER,
                JsonNodeConverterContexts.basic(
                        ExpressionNumberConverterContexts.basic(
                                Converters.fake(),
                                ConverterContexts.basic(
                                        Converters.JAVA_EPOCH_OFFSET, // dateOffset
                                        Converters.fake(),
                                        this.dateTimeContext(),
                                        this.decimalNumberContext()),
                                EXPRESSION_NUMBER_KIND
                        ),
                        JsonNodeMarshallUnmarshallContexts.fake()
                )
        );
    }

    private DateTimeContext dateTimeContext() {
        return DateTimeContexts.basic(
                DateTimeSymbols.fromDateFormatSymbols(
                        new DateFormatSymbols(LOCALE)
                ),
                LOCALE,
                1900,
                50,
                LocalDateTime::now
        );
    }

    private DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(
                DecimalNumberSymbols.with(
                        this.negativeSign(),
                        this.positiveSign(),
                        this.zeroDigit(),
                        this.currencySymbol(),
                        this.decimalSeparator(),
                        this.exponentSymbol(),
                        this.groupSeparator(),
                        this.infinitySymbol(),
                        this.monetaryDecimalSeparator(),
                        this.nanSymbol(),
                        this.percentSymbol(),
                        this.permillSymbol()
                ),
                LOCALE,
                this.mathContext()
        );
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
        return "EE";
    }

    @Override
    public char groupSeparator() {
        return '/';
    }

    @Override
    public String infinitySymbol() {
        return "Infinity!";
    }

    @Override
    public MathContext mathContext() {
        return MathContext.DECIMAL32;
    }

    @Override
    public char monetaryDecimalSeparator() {
        return '*';
    }

    @Override
    public String nanSymbol() {
        return "Nan!";
    }

    @Override
    public char negativeSign() {
        return ';';
    }

    @Override
    public char percentSymbol() {
        return ':';
    }

    @Override
    public char permillSymbol() {
        return '>';
    }

    @Override
    public char positiveSign() {
        return '^';
    }

    @Override
    public char zeroDigit() {
        return '0';
    }
    // ClassTesting.....................................................................................................

    @Override
    public Class<BasicSpreadsheetFormatterContext> type() {
        return BasicSpreadsheetFormatterContext.class;
    }
}
