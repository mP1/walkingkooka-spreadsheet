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
import walkingkooka.math.FakeDecimalNumberContext;
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

    private final static Color COLOR = Color.fromRgb(0x123456);

    private final static Function<Integer, Optional<Color>> NUMBER_TO_COLOR = new Function<>() {

        @Override
        public Optional<Color> apply(final Integer number) {
            return Optional.of(COLOR);
        }

        @Override
        public String toString() {
            return 1 + "=" + COLOR;
        }
    };

    private final static Function<SpreadsheetColorName, Optional<Color>> NAME_TO_COLOR = new Function<>() {

        @Override
        public Optional<Color> apply(final SpreadsheetColorName name) {
            return Optional.of(COLOR);
        }

        @Override
        public String toString() {
            return "bingo=" + COLOR;
        }
    };

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

    private final static SpreadsheetFormatter FORMATTER = new FakeSpreadsheetFormatter() {

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

    private final static DateTimeContext DATE_TIME_CONTEXT = DateTimeContexts.basic(
            DateTimeSymbols.fromDateFormatSymbols(
                    new DateFormatSymbols(LOCALE)
            ),
            LOCALE,
            1900,
            50,
            LocalDateTime::now
    );

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = new FakeDecimalNumberContext() {
        @Override
        public Locale locale() {
            return LOCALE;
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

        @Override
        public String toString() {
            return "TestDecimalNumberContext";
        }
    };

    private final static SpreadsheetConverterContext CONVERTER_CONTEXT = SpreadsheetConverterContexts.basic(
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
                                    DATE_TIME_CONTEXT,
                                    DECIMAL_NUMBER_CONTEXT
                            ),
                            EXPRESSION_NUMBER_KIND
                    ),
                    JsonNodeMarshallUnmarshallContexts.fake()
            )
    );

    private final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT =
            (cell) -> {
                Objects.requireNonNull(cell, "cell");
                throw new UnsupportedOperationException();
            };

    @Test
    public void testWithNullNumberToColorFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetFormatterContext.with(
                        null,
                        NAME_TO_COLOR,
                        CELL_CHARACTER_WIDTH,
                        GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                        FORMATTER,
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                        CONVERTER_CONTEXT
                )
        );
    }

    @Test
    public void testWithNullNameToColorFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetFormatterContext.with(
                        NUMBER_TO_COLOR,
                        null,
                        CELL_CHARACTER_WIDTH,
                        GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                        FORMATTER,
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                        CONVERTER_CONTEXT
                )
        );
    }

    @Test
    public void testWithInvalidCellCharacterWidthFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BasicSpreadsheetFormatterContext.with(NUMBER_TO_COLOR,
                        NAME_TO_COLOR,
                        -1,
                        GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                        FORMATTER,
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                        CONVERTER_CONTEXT
                )
        );
    }

    @Test
    public void testWithInvalidCellCharacterWidthFails2() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BasicSpreadsheetFormatterContext.with(NUMBER_TO_COLOR,
                        NAME_TO_COLOR,
                        0,
                        GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                        FORMATTER,
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                        CONVERTER_CONTEXT)
        );
    }

    @Test
    public void testWithInvalidGeneralFormatNumberDigitCountFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BasicSpreadsheetFormatterContext.with(NUMBER_TO_COLOR,
                        NAME_TO_COLOR,
                        CELL_CHARACTER_WIDTH,
                        -1,
                        FORMATTER,
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                        CONVERTER_CONTEXT
                )
        );
    }

    @Test
    public void testWithInvalidGeneralFormatNumberDigitCountFails2() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BasicSpreadsheetFormatterContext.with(NUMBER_TO_COLOR,
                        NAME_TO_COLOR,
                        CELL_CHARACTER_WIDTH,
                        0,
                        FORMATTER,
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                        CONVERTER_CONTEXT)
        );
    }

    @Test
    public void testWithNullFormatterFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetFormatterContext.with(
                        NUMBER_TO_COLOR,
                        NAME_TO_COLOR,
                        CELL_CHARACTER_WIDTH,
                        GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                        null,
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                        CONVERTER_CONTEXT
                )
        );
    }

    @Test
    public void testWIthNullConverterContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetFormatterContext.with(
                        NUMBER_TO_COLOR,
                        NAME_TO_COLOR,
                        CELL_CHARACTER_WIDTH,
                        GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                        FORMATTER,
                        SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                        null
                )
        );
    }

    @Test
    public void testColorNumber() {
        this.colorNumberAndCheck(
                this.createContext(),
                1,
                Optional.of(COLOR)
        );
    }

    @Test
    public void testColorName() {
        this.colorNameAndCheck(
                this.createContext(),
                SpreadsheetColorName.with("bingo"),
                Optional.of(COLOR)
        );
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

    @Override
    public BasicSpreadsheetFormatterContext createContext() {
        return BasicSpreadsheetFormatterContext.with(
                NUMBER_TO_COLOR,
                NAME_TO_COLOR,
                CELL_CHARACTER_WIDTH,
                GENERAL_NUMBER_FORMAT_DIGIT_COUNT,
                FORMATTER,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                CONVERTER_CONTEXT
        );
    }

    private final static int CELL_CHARACTER_WIDTH = 1;

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
        return DECIMAL_NUMBER_CONTEXT.currencySymbol();
    }

    @Override
    public char decimalSeparator() {
        return DECIMAL_NUMBER_CONTEXT.decimalSeparator();
    }

    @Override
    public String exponentSymbol() {
        return DECIMAL_NUMBER_CONTEXT.exponentSymbol();
    }

    @Override
    public char groupSeparator() {
        return DECIMAL_NUMBER_CONTEXT.groupSeparator();
    }

    @Override
    public String infinitySymbol() {
        return DECIMAL_NUMBER_CONTEXT.infinitySymbol();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    @Override
    public char monetaryDecimalSeparator() {
        return DECIMAL_NUMBER_CONTEXT.monetaryDecimalSeparator();
    }

    @Override
    public String nanSymbol() {
        return DECIMAL_NUMBER_CONTEXT.nanSymbol();
    }

    @Override
    public char negativeSign() {
        return DECIMAL_NUMBER_CONTEXT.negativeSign();
    }

    @Override
    public char percentSymbol() {
        return DECIMAL_NUMBER_CONTEXT.percentSymbol();
    }

    @Override
    public char permillSymbol() {
        return DECIMAL_NUMBER_CONTEXT.permillSymbol();
    }

    @Override
    public char positiveSign() {
        return DECIMAL_NUMBER_CONTEXT.positiveSign();
    }

    @Override
    public char zeroDigit() {
        return DECIMAL_NUMBER_CONTEXT.zeroDigit();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<BasicSpreadsheetFormatterContext> type() {
        return BasicSpreadsheetFormatterContext.class;
    }
}
