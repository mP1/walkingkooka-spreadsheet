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
import walkingkooka.convert.BinaryNumberConverterFunctions;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.FakeDecimalNumberContext;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContexts;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataLoader;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolverTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.storage.HasUserDirectorieses;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.convert.ExpressionNumberBinaryNumberConverterFunctions;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.text.TextNode;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterContextBasicTest implements SpreadsheetFormatterContextTesting2<SpreadsheetFormatterContextBasic>,
    SpreadsheetEnvironmentContextTesting,
    SpreadsheetLabelNameResolverTesting {

    private final static Color COLOR = Color.fromRgb(0x123456);

    private final static int COLOR_NUMBER = 2;

    private final static SpreadsheetColorName SPREADSHEET_COLOR_NAME = SpreadsheetColorName.with("Bingo");

    private final static SpreadsheetLabelNameResolver LABEL_NAME_RESOLVER = new FakeSpreadsheetLabelNameResolver() {

        // variable length #toString and ToStringBuilder max length causes random test fails
        @Override
        public String toString() {
            return FakeSpreadsheetLabelNameResolver.class.getSimpleName();
        }
    };

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
                ).textNode()
            );
        }

        @Override
        public String toString() {
            return SpreadsheetFormatterContextBasicTest.class.getSimpleName() + ".formatter()";
        }
    };

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
        public int decimalNumberDigitCount() {
            return DEFAULT_NUMBER_DIGIT_COUNT;
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
            return MATH_CONTEXT;
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

    private final static SpreadsheetMetadata SPREADSHEET_METADATA = SpreadsheetMetadata.EMPTY.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    ).set(
        SpreadsheetMetadataPropertyName.numberedColor(COLOR_NUMBER),
        COLOR
    ).set(
        SpreadsheetMetadataPropertyName.namedColor(SPREADSHEET_COLOR_NAME),
        COLOR_NUMBER
    );

    private final static SpreadsheetConverterContext CONVERTER_CONTEXT = SpreadsheetConverterContexts.basic(
        HasUserDirectorieses.fake(),
        Optional.of(SPREADSHEET_METADATA),
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
        MEDIA_TYPE_DETECTOR,
        BinaryNumberConverterFunctions.multiply(), // multiplier
        LABEL_NAME_RESOLVER,
        new SpreadsheetMetadataLoader() {
            @Override
            public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
                Objects.requireNonNull(id, "id");

                return Optional.ofNullable(
                    SPREADSHEET_ID.equals(id) ?
                        SPREADSHEET_METADATA :
                        null
                );
            }
        },
        JsonNodeConverterContexts.basic(
            ExpressionNumberConverterContexts.basic(
                Converters.fake(),
                ExpressionNumberBinaryNumberConverterFunctions.multiply(), // multiplier
                ConverterContexts.basic(
                    false, // canNumbersHaveGroupSeparator
                    Converters.JAVA_EPOCH_OFFSET, // dateOffset
                    ',', // valueSeparator
                    Converters.fake(),
                    BinaryNumberConverterFunctions.fake(), // multiplier
                    BINARY_TEXT_CONTEXT,
                    CURRENCY_LOCALE_CONTEXT,
                    DATE_TIME_CONTEXT,
                    DECIMAL_NUMBER_CONTEXT
                ),
                EXPRESSION_NUMBER_KIND
            ),
            ENVIRONMENT_CONTEXT,
            JSON_NODE_MARSHALL_UNMARSHALL_CONTEXT
        ),
        LOCALE_CONTEXT
    );

    private final Function<Optional<Object>, SpreadsheetExpressionEvaluationContext> SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT =
        (cell) -> {
            Objects.requireNonNull(cell, "cell");
            throw new UnsupportedOperationException();
        };

    private final static SpreadsheetFormatterProvider SPREADSHEET_FORMATTER_PROVIDER = SpreadsheetFormatterProviders.fake();

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullHasSpreadsheetCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterContextBasic.with(
                null,
                CELL_CHARACTER_WIDTH,
                FORMATTER,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                CONVERTER_CONTEXT,
                SPREADSHEET_FORMATTER_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithInvalidCellCharacterWidthFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetFormatterContextBasic.with(
                HAS_SPREADSHEET_CELL,
                -1,
                FORMATTER,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                CONVERTER_CONTEXT,
                SPREADSHEET_FORMATTER_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithInvalidCellCharacterWidthFails2() {
        assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetFormatterContextBasic.with(
                HAS_SPREADSHEET_CELL,
                0,
                FORMATTER,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                CONVERTER_CONTEXT,
                SPREADSHEET_FORMATTER_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullFormatterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterContextBasic.with(
                HAS_SPREADSHEET_CELL,
                CELL_CHARACTER_WIDTH,
                null,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                CONVERTER_CONTEXT,
                SPREADSHEET_FORMATTER_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullConverterContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterContextBasic.with(
                HAS_SPREADSHEET_CELL,
                CELL_CHARACTER_WIDTH,
                FORMATTER,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                null,
                SPREADSHEET_FORMATTER_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetFormatterProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterContextBasic.with(
                HAS_SPREADSHEET_CELL,
                CELL_CHARACTER_WIDTH,
                FORMATTER,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                CONVERTER_CONTEXT,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterContextBasic.with(
                HAS_SPREADSHEET_CELL,
                CELL_CHARACTER_WIDTH,
                FORMATTER,
                SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
                CONVERTER_CONTEXT,
                SPREADSHEET_FORMATTER_PROVIDER,
                null
            )
        );
    }

    @Test
    public void testSpreadsheetCell() {
        this.spreadsheetCellAndCheck(
            this.createContext(),
            OPTIONAL_SPREADSHEET_CELL
        );
    }

//    @Test
//    public void testColorNumber() {
//        this.colorNumberAndCheck(
//            this.createContext(),
//            COLOR_NUMBER,
//            Optional.of(COLOR)
//        );
//    }
//
//    @Test
//    public void testColorName() {
//        this.colorNameAndCheck(
//            this.createContext(),
//            SPREADSHEET_COLOR_NAME,
//            Optional.of(COLOR)
//        );
//    }

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
    public void testFormatValue() {
        this.formatValueAndCheck(
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
    public void testLoadMetadata() {
        this.loadMetadataAndCheck(
            this.createContext(),
            SPREADSHEET_ID,
            SPREADSHEET_METADATA
        );
    }

    @Override
    public SpreadsheetFormatterContextBasic createContext() {
        return SpreadsheetFormatterContextBasic.with(
            HAS_SPREADSHEET_CELL,
            CELL_CHARACTER_WIDTH,
            FORMATTER,
            SPREADSHEET_EXPRESSION_EVALUATION_CONTEXT,
            CONVERTER_CONTEXT,
            SPREADSHEET_FORMATTER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    private final static int CELL_CHARACTER_WIDTH = 1;

    @Override
    public String currencySymbol() {
        return DECIMAL_NUMBER_CONTEXT.currencySymbol();
    }

    @Override
    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_CONTEXT.decimalNumberDigitCount();
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
    public Class<SpreadsheetFormatterContextBasic> type() {
        return SpreadsheetFormatterContextBasic.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
