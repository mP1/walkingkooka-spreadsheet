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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.FakeSpreadsheetTextFormatContext;
import walkingkooka.spreadsheet.format.FakeSpreadsheetTextFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormattedText;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatContext;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeName;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetEngineContextTest implements SpreadsheetEngineContextTesting<BasicSpreadsheetEngineContext> {

    @Test
    public void testWithNullFunctionsFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(null,
                    this.engine(),
                    this.labelStore(),
                    this.converter(),
                    this.decimalNumberContext(),
                    this.dateTimeContext(),
                    this.numberToColor(),
                    this.nameToColor(),
                    WIDTH,
                    FRACTIONER,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    null,
                    this.labelStore(),
                    this.converter(),
                    this.decimalNumberContext(),
                    this.dateTimeContext(),
                    this.numberToColor(),
                    this.nameToColor(),
                    WIDTH,
                    FRACTIONER,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithNullLabelStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    this.engine(),
                    null,
                    this.converter(),
                    this.decimalNumberContext(),
                    this.dateTimeContext(),
                    this.numberToColor(),
                    this.nameToColor(),
                    WIDTH,
                    FRACTIONER,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithNullConverterFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    this.engine(),
                    this.labelStore(),
                    null,
                    this.decimalNumberContext(),
                    this.dateTimeContext(),
                    this.numberToColor(),
                    this.nameToColor(),
                    WIDTH,
                    FRACTIONER,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithNullDecimalNumberContextFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    this.engine(),
                    this.labelStore(),
                    this.converter(),
                    null,
                    this.dateTimeContext(),
                    this.numberToColor(),
                    this.nameToColor(),
                    WIDTH,
                    FRACTIONER,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithNullDateTimeContextFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    this.engine(),
                    this.labelStore(),
                    this.converter(),
                    this.decimalNumberContext(),
                    null,
                    this.numberToColor(),
                    this.nameToColor(),
                    WIDTH,
                    FRACTIONER,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithNullNumberToColorFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    this.engine(),
                    this.labelStore(),
                    this.converter(),
                    this.decimalNumberContext(),
                    this.dateTimeContext(),
                    null,
                    this.nameToColor(),
                    WIDTH,
                    FRACTIONER,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithNullNameToColorFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    this.engine(),
                    this.labelStore(),
                    this.converter(),
                    this.decimalNumberContext(),
                    this.dateTimeContext(),
                    this.numberToColor(),
                    null,
                    WIDTH,
                    FRACTIONER,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithInvalidWidthFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    this.engine(),
                    this.labelStore(),
                    this.converter(),
                    this.decimalNumberContext(),
                    this.dateTimeContext(),
                    this.numberToColor(),
                    this.nameToColor(),
                    0,
                    FRACTIONER,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithNullFractionFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    this.engine(),
                    this.labelStore(),
                    this.converter(),
                    this.decimalNumberContext(),
                    this.dateTimeContext(),
                    this.numberToColor(),
                    this.nameToColor(),
                    WIDTH,
                    null,
                    this.defaultSpreadsheetTextFormatter());
        });
    }

    @Test
    public void testWithNullDefaultSpreadsheetTextFormatterFails() {
        assertThrows(NullPointerException.class, () -> {
            BasicSpreadsheetEngineContext.with(this.functions(),
                    this.engine(),
                    this.labelStore(),
                    this.converter(),
                    this.decimalNumberContext(),
                    this.dateTimeContext(),
                    this.numberToColor(),
                    this.nameToColor(),
                    WIDTH,
                    FRACTIONER,
                    null);
        });
    }

    @Test
    public void testConvert() {
        this.convertAndCheck(BigInteger.valueOf(1), Boolean.class, Boolean.TRUE);
    }

    @Test
    public void testConvert2() {
        this.convertAndCheck(BigInteger.valueOf(0), Boolean.class, Boolean.FALSE);
    }

    @Test
    public void testDefaultSpreadsheetTextFormatter() {
        final SpreadsheetTextFormatter defaultSpreadsheetTextFormatter = this.defaultSpreadsheetTextFormatter();
        assertSame(defaultSpreadsheetTextFormatter, this.createContext(defaultSpreadsheetTextFormatter).defaultSpreadsheetTextFormatter());
    }

    @Test
    public void testParseFormula() {
        this.parseFormulaAndCheck("1+2",
                SpreadsheetParserToken.addition(Lists.of(SpreadsheetParserToken.bigDecimal(BigDecimal.valueOf(1), "1"),
                        SpreadsheetParserToken.plusSymbol("+", "+"),
                        SpreadsheetParserToken.bigDecimal(BigDecimal.valueOf(2), "2")),
                        "1+2"));
    }

    @Test
    public void testEvaluate() {
        this.evaluateAndCheck(ExpressionNode.addition(ExpressionNode.longNode(1), ExpressionNode.longNode(2)),
                Long.valueOf(1 + 2));
    }

    @Test
    public void testEvaluateWithFunction() {
        this.evaluateAndCheck(ExpressionNode.function(ExpressionNodeName.with("xyz"),
                Lists.of(ExpressionNode.longNode(1), ExpressionNode.longNode(2), ExpressionNode.longNode(3))),
                Long.valueOf(1 + 2 + 3));
    }

    @Test
    public void testParsePattern() {
        // DecimalNumberContext returns 'D' for the decimal point character and 'M' for minus sign

        this.parsePatternAndCheck("####.#",
                BigDecimal.valueOf(-123.456),
                this.spreadsheetTextFormatContext(),
                Optional.of(SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, "M123D5")));
    }

    private SpreadsheetTextFormatContext spreadsheetTextFormatContext() {
        final DecimalNumberContext decimalNumberContext = this.decimalNumberContext();

        return new FakeSpreadsheetTextFormatContext() {
            @Override
            public String currencySymbol() {
                return decimalNumberContext.currencySymbol();
            }

            @Override
            public char decimalPoint() {
                return decimalNumberContext.decimalPoint();
            }

            @Override
            public char exponentSymbol() {
                return decimalNumberContext.exponentSymbol();
            }

            @Override
            public char groupingSeparator() {
                return decimalNumberContext.groupingSeparator();
            }

            @Override
            public char minusSign() {
                return decimalNumberContext.minusSign();
            }

            @Override
            public char plusSign() {
                return decimalNumberContext.plusSign();
            }

            @Override
            public MathContext mathContext() {
                return decimalNumberContext.mathContext();
            }
        };
    }

    @Test
    public void testFormat() {
        // DecimalNumberContext returns 'D' for the decimal point character and 'M' for minus sign
        this.formatAndCheck(BigDecimal.valueOf(-123.45),
                this.createContext().parsePattern("#.#\"Abc123\""),
                Optional.of(SpreadsheetFormattedText.with(SpreadsheetFormattedText.WITHOUT_COLOR, "M123D5Abc123")));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createContext(), "decimalNumberContext=\"C\" 'D' 'E' 'G' 'M' 'P' 'L' fr_CA precision=7 roundingMode=HALF_EVEN converter=value instanceof target type. | Truthy BigDecimal|BigInteger|Byte|Short|Integer|Long|Float|Double->Boolean fractioner=Fractioner123 defaultSpreadsheetTextFormatter=SpreadsheetTextFormatter123");
    }

    @Override
    public BasicSpreadsheetEngineContext createContext() {
        return this.createContext(this.defaultSpreadsheetTextFormatter());
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetTextFormatter defaultSpreadsheetTextFormatter) {
        return BasicSpreadsheetEngineContext.with(this.functions(),
                this.engine(),
                this.labelStore(),
                this.converter(),
                this.decimalNumberContext(),
                this.dateTimeContext(),
                this.numberToColor(),
                this.nameToColor(),
                WIDTH,
                FRACTIONER,
                defaultSpreadsheetTextFormatter);
    }

    private BiFunction<ExpressionNodeName, List<Object>, Object> functions() {
        return this::functions;
    }

    private Object functions(final ExpressionNodeName name, final List<Object> parameters) {
        assertEquals(functionName(), name, "function name");
        return parameters.stream()
                .mapToLong(p -> this.converter().convert(p, Long.class, ConverterContexts.fake()))
                .sum();
    }

    private ExpressionNodeName functionName() {
        return ExpressionNodeName.with("xyz");
    }

    private SpreadsheetEngine engine() {
        return SpreadsheetEngines.fake();
    }

    private SpreadsheetLabelStore labelStore() {
        return SpreadsheetLabelStores.fake();
    }

    private Converter converter() {
        return Converters.collection(
                Lists.of(Converters.simple(),
                        Converters.truthyNumberBoolean()));
    }

    private DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic("C",
                'D',
                'E',
                'G',
                'M',
                'P',
                'L',
                Locale.CANADA_FRENCH,
                MathContext.DECIMAL32);
    }

    private DateTimeContext dateTimeContext() {
        return new FakeDateTimeContext() {
            @Override
            public String toString() {
                return "DateTimeContext123";
            }
        };
    }

    private SpreadsheetTextFormatter defaultSpreadsheetTextFormatter() {
        return new FakeSpreadsheetTextFormatter() {
            @Override
            public String toString() {
                return "SpreadsheetTextFormatter123";
            }
        };
    }

    private Function<Integer, Optional<Color>> numberToColor() {
        return this::numberToColor0;
    }

    private Optional<Color> numberToColor0(final Integer colorNumber) {
        assertEquals(12, colorNumber, "colorNumber");
        return this.color();
    }


    private Function<String, Optional<Color>> nameToColor() {
        return this::nameToColor0;
    }

    private Optional<Color> nameToColor0(final String colorName) {
        assertEquals("bingo", colorName, "colorName");
        return this.color();
    }

    private final Optional<Color> color() {
        return Optional.of(Color.fromRgb(0x123456));
    }

    private final static String GENERAL_DECIMAL_FORMAT_PATTERN = "##.#";
    private final static int WIDTH = 1;
    private final Function<BigDecimal, Fraction> FRACTIONER = new Function<>() {
        @Override
        public Fraction apply(final BigDecimal bigDecimal) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "Fractioner123";
        }
    };

    @Override
    public Class<BasicSpreadsheetEngineContext> type() {
        return BasicSpreadsheetEngineContext.class;
    }
}
