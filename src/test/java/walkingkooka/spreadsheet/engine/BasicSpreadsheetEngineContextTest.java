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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.Fraction;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatter;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePatterns;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberExpression;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionContext;
import walkingkooka.tree.expression.function.FakeExpressionFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetEngineContextTest implements SpreadsheetEngineContextTesting<BasicSpreadsheetEngineContext> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    private final static Locale LOCALE = Locale.forLanguageTag("EN-AU");
    private final static char VALUE_SEPARATOR = ',';
    private final static int WIDTH = 1;

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngineContext.with(
                null,
                this.valueParser(),
                VALUE_SEPARATOR,
                this.functions(),
                this.engine(),
                this.numberToColor(),
                this.nameToColor(),
                FRACTIONER,
                this.defaultSpreadsheetFormatter(),
                this.storeRepository()
                )
        );
    }

    @Test
    public void testWithNullParserFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngineContext.with(
                this.metadata(),
                null,
                VALUE_SEPARATOR,
                this.functions(),
                this.engine(),
                this.numberToColor(),
                this.nameToColor(),
                FRACTIONER,
                this.defaultSpreadsheetFormatter(),
                this.storeRepository()
                )
        );
    }

    @Test
    public void testWithNullFunctionsFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngineContext.with(
                this.metadata(),
                this.valueParser(),
                VALUE_SEPARATOR,
                null,
                this.engine(),
                this.numberToColor(),
                this.nameToColor(),
                FRACTIONER,
                this.defaultSpreadsheetFormatter(),
                this.storeRepository()
                )
        );
    }

    @Test
    public void testWithNullEngineFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngineContext.with(
                this.metadata(),
                this.valueParser(),
                VALUE_SEPARATOR,
                this.functions(),
                null,
                this.numberToColor(),
                this.nameToColor(),
                FRACTIONER,
                this.defaultSpreadsheetFormatter(),
                this.storeRepository()
                )
        );
    }

    @Test
    public void testWithNullNumberToColorFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngineContext.with(
                this.metadata(),
                this.valueParser(),
                VALUE_SEPARATOR,
                this.functions(),
                this.engine(),
                null,
                this.nameToColor(),
                FRACTIONER,
                this.defaultSpreadsheetFormatter(),
                this.storeRepository()
                )
        );
    }

    @Test
    public void testWithNullNameToColorFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngineContext.with(
                this.metadata(),
                this.valueParser(),
                VALUE_SEPARATOR,
                this.functions(),
                this.engine(),
                this.numberToColor(),
                null,
                FRACTIONER,
                this.defaultSpreadsheetFormatter(),
                this.storeRepository()
                )
        );
    }

    @Test
    public void testWithNullFractionFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngineContext.with(
                this.metadata(),
                this.valueParser(),
                VALUE_SEPARATOR,
                this.functions(),
                this.engine(),
                this.numberToColor(),
                this.nameToColor(),
                null,
                this.defaultSpreadsheetFormatter(),
                this.storeRepository()
                )
        );
    }

    @Test
    public void testWithNullDefaultSpreadsheetFormatterFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngineContext.with(
                this.metadata(),
                this.valueParser(),
                VALUE_SEPARATOR,
                this.functions(),
                this.engine(),
                this.numberToColor(),
                this.nameToColor(),
                FRACTIONER,
                null,
                this.storeRepository()
                )
        );
    }

    @Test
    public void testWithNullStoreRepositoryFails() {
        assertThrows(NullPointerException.class, () -> BasicSpreadsheetEngineContext.with(
                this.metadata(),
                this.valueParser(),
                VALUE_SEPARATOR,
                this.functions(),
                this.engine(),
                this.numberToColor(),
                this.nameToColor(),
                FRACTIONER,
                this.defaultSpreadsheetFormatter(),
                null
                )
        );
    }

    @Test
    public void testConvert() {
        this.convertAndCheck(BigDecimal.valueOf(123), Integer.class, 123);
    }

    @Test
    public void testDefaultSpreadsheetFormatter() {
        final SpreadsheetFormatter defaultSpreadsheetFormatter = this.defaultSpreadsheetFormatter();
        assertSame(defaultSpreadsheetFormatter, this.createContext(defaultSpreadsheetFormatter).defaultSpreadsheetFormatter());
    }

    // resolveCellReference..............................................................................................

    @Test
    public void testResolveCellReferenceCellReference() {
        final SpreadsheetCellReference cell = SpreadsheetExpressionReference.parseCellReference("A1");
        this.resolveCellReferenceAndCheck(
                cell,
                cell
        );
    }

    @Test
    public void testResolveCellReferenceLabelUnknownFails() {
        this.resolveCellReferenceAndFail(
                SpreadsheetLabelName.labelName("UnknownLabel")
        );
    }

    @Test
    public void testResolveCellReferenceLabel() {
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCellReference("A1");
        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("Label456");

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.mapping(cell));

        this.resolveCellReferenceAndCheck(
                this.createContext(store),
                label,
                cell
        );
    }

    @Test
    public void testResolveCellReferenceLabelToLabelToCell() {
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCellReference("A1");
        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label222");

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label1.mapping(label2));
        store.save(label2.mapping(cell));

        this.resolveCellReferenceAndCheck(
                this.createContext(store),
                label1,
                cell
        );
    }

    // parseFormula.....................................................................................................

    @Test
    public void testParseFormulaApostropheString() {
        final String text = "abc123";
        final String formula = "'" + text;
        this.parseFormulaAndCheck(
                formula,
                SpreadsheetParserToken.text(
                        Lists.of(
                                SpreadsheetParserToken.apostropheSymbol("\'", "\'"),
                                SpreadsheetParserToken.textLiteral(text, text)
                        ),
                        formula
                )
        );
    }

    @Test
    public void testParseFormulaDate() {
        final String text = "31/12/2000";
        this.parseFormulaAndCheck(
                text,
                SpreadsheetParserToken.date(
                        Lists.of(
                                SpreadsheetParserToken.dayNumber(31, "31"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.monthNumber(12, "12"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.year(2000, "2000")
                        ),
                        text
                )
        );
    }

    @Test
    public void testParseFormulaDateTime() {
        final String text = "31/12/2000 12:58";
        this.parseFormulaAndCheck(
                text,
                SpreadsheetParserToken.dateTime(
                        Lists.of(
                                SpreadsheetParserToken.dayNumber(31, "31"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.monthNumber(12, "12"),
                                SpreadsheetParserToken.textLiteral("/", "/"),
                                SpreadsheetParserToken.year(2000, "2000"),
                                SpreadsheetParserToken.textLiteral(" ", " "),
                                SpreadsheetParserToken.hour(12, "12"),
                                SpreadsheetParserToken.textLiteral(":", ":"),
                                SpreadsheetParserToken.minute(58, "58")
                        ),
                        text
                )
        );
    }

    @Test
    public void testParseFormulaNumber() {
        final String text = "123";
        this.parseFormulaAndCheck(
                text,
                SpreadsheetParserToken.number(
                        Lists.of(
                                SpreadsheetParserToken.digits(text, text)
                        ),
                        text
                )
        );
    }

    @Test
    public void testParseFormulaNumber2() {
        final String text = "1" + DECIMAL + "5";
        this.parseFormulaAndCheck(
                text,
                SpreadsheetParserToken.number(
                        Lists.of(
                                SpreadsheetParserToken.digits("1", "1"),
                                SpreadsheetParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL),
                                SpreadsheetParserToken.digits("5", "5")
                        ),
                        text
                )
        );
    }

    @Test
    public void testParseFormulaTime() {
        final String text = "12:58";
        this.parseFormulaAndCheck(
                text,
                SpreadsheetParserToken.time(
                        Lists.of(
                                SpreadsheetParserToken.hour(12, "12"),
                                SpreadsheetParserToken.textLiteral(":", ":"),
                                SpreadsheetParserToken.minute(58, "58")
                        ),
                        text
                )
        );
    }

    @Test
    public void testParseFormulaExpression() {
        final String text = "=1+2";
        this.parseFormulaAndCheck(
                text,
                SpreadsheetParserToken.expression(
                        Lists.of(
                                SpreadsheetParserToken.equalsSymbol("=", "="),
                                SpreadsheetParserToken.addition(
                                        Lists.of(
                                                SpreadsheetParserToken.number(
                                                        Lists.of(
                                                                SpreadsheetParserToken.digits("1", "1")
                                                        ),
                                                        "1"
                                                ),
                                                SpreadsheetParserToken.plusSymbol("+", "+"),
                                                SpreadsheetParserToken.number(
                                                        Lists.of(
                                                                SpreadsheetParserToken.digits("2", "2")
                                                        ),
                                                        "2"
                                                )
                                        ),
                                        "1+2"
                                )
                        ),
                        text
                )
        );
    }

    @Test
    public void testEvaluate() {
        this.evaluateAndCheck(Expression.add(this.expression(1), this.expression(2)),
                this.number(1 + 2));
    }

    @Test
    public void testEvaluateWithFunction() {
        this.evaluateAndCheck(Expression.function(FunctionExpressionName.with("xyz"),
                Lists.of(this.expression(1), this.expression(2), this.expression(3))),
                1L + 2 + 3);
    }

    @Test
    public void testParsePattern() {
        // DecimalNumberContext returns 'D' for the decimal point character and 'M' for minus sign

        this.parsePatternAndCheck("####.#",
                BigDecimal.valueOf(-123.456),
                this.spreadsheetFormatContext(),
                Optional.of(SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, MINUS + "123" + DECIMAL + "5")));
    }

    private SpreadsheetFormatterContext spreadsheetFormatContext() {
        final DecimalNumberContext decimalNumberContext = this.decimalNumberContext();

        return new FakeSpreadsheetFormatterContext() {

            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> target) {
                try {
                    this.convert(value, target);
                    return true;
                } catch (final Exception failed) {
                    return false;
                }
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return Converters.numberNumber()
                        .convert(value,
                                target,
                                ConverterContexts.fake());
            }

            @Override
            public String currencySymbol() {
                return decimalNumberContext.currencySymbol();
            }

            @Override
            public char decimalSeparator() {
                return decimalNumberContext.decimalSeparator();
            }

            @Override
            public String exponentSymbol() {
                return decimalNumberContext.exponentSymbol();
            }

            @Override
            public char groupingSeparator() {
                return decimalNumberContext.groupingSeparator();
            }

            @Override
            public char negativeSign() {
                return decimalNumberContext.negativeSign();
            }

            @Override
            public char positiveSign() {
                return decimalNumberContext.positiveSign();
            }

            @Override
            public MathContext mathContext() {
                return decimalNumberContext.mathContext();
            }
        };
    }

    @Test
    public void testFormat() {
        this.formatAndCheck(
                BigDecimal.valueOf(-123.45),
                this.createContext().parsePattern("#.#\"Abc123\""),
                Optional.of(
                        SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, MINUS + "123" + DECIMAL + "5Abc123")
                )
        );
    }

    // hasLocale........................................................................................................

    @Test
    public void testLocale() {
        assertEquals(LOCALE, this.createContext().locale());
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createContext(),
                "metadata={\n" +
                        "  \"cell-character-width\": 1,\n" +
                        "  \"currency-symbol\": \"CURR\",\n" +
                        "  \"date-format-pattern\": \"dddd, d mmmm yyyy\",\n" +
                        "  \"date-parse-patterns\": \"dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                        "  \"date-time-format-pattern\": \"dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                        "  \"date-time-offset\": \"-25569\",\n" +
                        "  \"date-time-parse-patterns\": \"dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM;dddd, d mmmm yy \\\\a\\\\t h:mm:ss AM/PM;dddd, d mmmm yy \\\\a\\\\t h:mm:ss;dddd, d mmmm yy \\\\a\\\\t h:mm AM/PM;dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss.0 AM/PM;dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss.0;dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss;dddd, d mmmm yyyy \\\\a\\\\t h:mm AM/PM;dddd, d mmmm yyyy \\\\a\\\\t h:mm;dddd, d mmmm yyyy, h:mm:ss AM/PM;dddd, d mmmm yy, h:mm:ss AM/PM;dddd, d mmmm yy, h:mm:ss;dddd, d mmmm yy, h:mm AM/PM;dddd, d mmmm yyyy, h:mm:ss.0 AM/PM;dddd, d mmmm yyyy, h:mm:ss.0;ddd fractioner=Fractioner123 defaultSpreadsheetFormatter=SpreadsheetFormatter123"
        );
    }

    @Override
    public BasicSpreadsheetEngineContext createContext() {
        return this.createContext(this.defaultSpreadsheetFormatter());
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetLabelStore labelStore) {
        return this.createContext(labelStore, this.defaultSpreadsheetFormatter());
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetFormatter defaultSpreadsheetFormatter) {
        return this.createContext(SpreadsheetLabelStores.fake(), defaultSpreadsheetFormatter);
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetLabelStore labelStore,
                                                        final SpreadsheetFormatter defaultSpreadsheetFormatter) {
        return BasicSpreadsheetEngineContext.with(
                this.metadata(),
                this.valueParser(),
                VALUE_SEPARATOR,
                this.functions(),
                this.engine(),
                this.numberToColor(),
                this.nameToColor(),
                FRACTIONER,
                defaultSpreadsheetFormatter,
                new FakeSpreadsheetStoreRepository() {
                    @Override
                    public SpreadsheetLabelStore labels() {
                        return labelStore;
                    }
                }
        );
    }

    private SpreadsheetMetadata metadata() {
        return SpreadsheetMetadata.NON_LOCALE_DEFAULTS
                .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, WIDTH)
                .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
                .loadFromLocale()
                .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetFormatPattern.parseTextFormatPattern("@"))
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT)
                .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, GROUPING)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, MINUS)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, PLUS)
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND);
    }

    private Parser<SpreadsheetParserContext> valueParser() {
        return Parsers.alternatives(
                Lists.of(
                        SpreadsheetParsePatterns.parseDateTimeParsePatterns("d/mm/yyyy hh:mm").parser(),
                        SpreadsheetParsePatterns.parseDateParsePatterns("d/mm/yyyy").parser(),
                        SpreadsheetParsePatterns.parseTimeParsePatterns("hh:mm").parser(),
                        SpreadsheetParsePatterns.parseNumberParsePatterns("#.#;#").parser()
                )
        );
    }

    private ExpressionNumber number(final Number value) {
        return EXPRESSION_NUMBER_KIND.create(value);
    }

    private ExpressionNumberExpression expression(final Number value) {
        return Expression.expressionNumber(this.number(value));
    }

    private Function<FunctionExpressionName, ExpressionFunction<?, ExpressionFunctionContext>> functions() {
        return (n) -> {
            assertEquals(functionName(), n, "function name");
            return new FakeExpressionFunction<>() {
                @Override
                public Object apply(final List<Object> parameters,
                                    final ExpressionFunctionContext context) {
                    return parameters.stream()
                            .mapToLong(p -> context.convertOrFail(p, Long.class))
                            .sum();
                }

                @Override
                public boolean resolveReferences() {
                    return true;
                }
            };
        };
    }

    private FunctionExpressionName functionName() {
        return FunctionExpressionName.with("xyz");
    }

    private SpreadsheetEngine engine() {
        return SpreadsheetEngines.fake();
    }

    @Override
    public DateTimeContext dateTimeContext() {
        return new FakeDateTimeContext() {

            @Override
            public String toString() {
                return "DateTimeContext123";
            }
        };
    }

    final static String CURRENCY = "CURR";
    final static char DECIMAL = '.';
    final static String EXPONENT = "e";
    final static char GROUPING = ',';
    final static char MINUS = '!';
    final static char PERCENT = '#';
    final static char PLUS = '@';

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(
                CURRENCY,
                DECIMAL,
                EXPONENT,
                GROUPING,
                MINUS,
                PERCENT,
                PLUS,
                LOCALE,
                new MathContext(MathContext.DECIMAL32.getPrecision(), RoundingMode.HALF_UP)
        );
    }

    private SpreadsheetFormatter defaultSpreadsheetFormatter() {
        return new FakeSpreadsheetFormatter() {
            @Override
            public String toString() {
                return "SpreadsheetFormatter123";
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


    private Function<SpreadsheetColorName, Optional<Color>> nameToColor() {
        return this::nameToColor0;
    }

    private Optional<Color> nameToColor0(final SpreadsheetColorName colorName) {
        assertEquals(SpreadsheetColorName.with("bingo"), colorName, "colorName");
        return this.color();
    }

    private Optional<Color> color() {
        return Optional.of(Color.fromRgb(0x123456));
    }

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

    final SpreadsheetStoreRepository storeRepository() {
        return SpreadsheetStoreRepositories.fake();
    }

    @Override
    public Class<BasicSpreadsheetEngineContext> type() {
        return BasicSpreadsheetEngineContext.class;
    }
}
