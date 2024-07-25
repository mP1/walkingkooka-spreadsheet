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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.ConversionException;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.SpreadsheetDescription;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.compare.SpreadsheetComparator;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProvider;
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.compare.SpreadsheetComparators;
import walkingkooka.spreadsheet.conditionalformat.SpreadsheetConditionalFormattingRule;
import walkingkooka.spreadsheet.convert.SpreadsheetConvertersConverterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterName;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSample;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.parser.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.ValueExpression;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfo;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetEngineContextTest implements SpreadsheetEngineContextTesting<BasicSpreadsheetEngineContext> {

    private final static String CURRENCY = "CURR";
    private final static char DECIMAL = '.';
    private final static String EXPONENT = "e";
    private final static char GROUP_SEPARATOR = ',';
    private final static char MINUS = '!';
    private final static char PERCENT = '#';
    private final static char PLUS = '@';

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    private final static Locale LOCALE = Locale.forLanguageTag("EN-AU");
    private final static char VALUE_SEPARATOR = ',';
    private final static int WIDTH = 1;

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadata.NON_LOCALE_DEFAULTS
            .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
            .loadFromLocale()
            .set(SpreadsheetMetadataPropertyName.DATE_TIME_PARSER, SpreadsheetPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm").spreadsheetParserSelector())
            .set(SpreadsheetMetadataPropertyName.TEXT_FORMATTER, SpreadsheetPattern.parseTextFormatPattern("@").spreadsheetFormatterSelector())
            .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
            .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL)
            .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT)
            .set(SpreadsheetMetadataPropertyName.GROUP_SEPARATOR, GROUP_SEPARATOR)
            .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, MINUS)
            .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, PLUS)
            .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, WIDTH)
            .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
            .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR);

    private final static SpreadsheetComparatorProvider SPREADSHEET_COMPARATOR_PROVIDER = SpreadsheetComparatorProviders.spreadsheetComparators();

    private final static SpreadsheetFormatterProvider SPREADSHEET_FORMATTER_PROVIDER = SpreadsheetFormatterProviders.spreadsheetFormatPattern(
            Locale.forLanguageTag("EN-AU"),
            () -> {
                throw new UnsupportedOperationException();
            }
    );

    private final static SpreadsheetParserProvider SPREADSHEET_PARSER_PROVIDER = SpreadsheetParserProviders.spreadsheetParsePattern(SPREADSHEET_FORMATTER_PROVIDER);

    private final static ConverterProvider CONVERTER_PROVIDER = SpreadsheetConvertersConverterProviders.spreadsheetConverters(
            METADATA,
            SPREADSHEET_FORMATTER_PROVIDER,
            SPREADSHEET_PARSER_PROVIDER
    );

    private final static String TEST_CONTEXT_LOADCELL = "test-context-loadCell";

    private final static String TEST_CONTEXT_SERVER_URL = "test-context-serverUrl";

    private final static String TEST_CONTEXT_SPREADSHEET_METADATA = "test-context-spreadsheet-metadata";

    private final static ExpressionFunctionProvider EXPRESSION_FUNCTION_PROVIDER = new ExpressionFunctionProvider() {

        @Override
        public ExpressionFunction<?, ExpressionEvaluationContext> expressionFunction(final FunctionExpressionName functionExpressionName) {
            switch (functionExpressionName.value()) {
                case "xyz":
                    return Cast.to(
                            new FakeExpressionFunction<Object, SpreadsheetExpressionEvaluationContext>() {

                                @Override
                                public Object apply(final List<Object> parameters,
                                                    final SpreadsheetExpressionEvaluationContext context) {
                                    return parameters.stream()
                                            .mapToLong(p -> context.convertOrFail(p, Long.class))
                                            .sum();
                                }

                                @Override
                                public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                    return Lists.of(
                                            ExpressionFunctionParameterName.with("parameters")
                                                    .variable(Object.class)
                                                    .setKinds(ExpressionFunctionParameterKind.CONVERT_EVALUATE_RESOLVE_REFERENCES)
                                    );
                                }

                                @Override
                                public String toString() {
                                    return "xyz";
                                }
                            }
                    );
                case TEST_CONTEXT_LOADCELL:
                    return Cast.to(
                            new FakeExpressionFunction<Object, SpreadsheetExpressionEvaluationContext>() {
                                @Override
                                public Object apply(final List<Object> parameters,
                                                    final SpreadsheetExpressionEvaluationContext context) {
                                    return context.loadCell(
                                                    (SpreadsheetCellReference) parameters.get(0)
                                            ).get()
                                            .formula()
                                            .value()
                                            .get();
                                }

                                @Override
                                public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                    return Lists.of(
                                            ExpressionFunctionParameterName.with("parameters")
                                                    .variable(Object.class)
                                                    .setKinds(
                                                            Sets.of(ExpressionFunctionParameterKind.EVALUATE)
                                                    )
                                    );
                                }

                                @Override
                                public String toString() {
                                    return TEST_CONTEXT_LOADCELL;
                                }
                            }
                    );
                case TEST_CONTEXT_SERVER_URL:
                    return Cast.to(
                            new FakeExpressionFunction<Object, SpreadsheetExpressionEvaluationContext>() {
                                @Override
                                public Object apply(final List<Object> parameters,
                                                    final SpreadsheetExpressionEvaluationContext context) {
                                    return context.serverUrl();
                                }

                                @Override
                                public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                    return Lists.of(
                                            ExpressionFunctionParameterName.with("parameters")
                                                    .variable(Object.class)
                                    );
                                }

                                @Override
                                public String toString() {
                                    return TEST_CONTEXT_SERVER_URL;
                                }
                            }
                    );
                case TEST_CONTEXT_SPREADSHEET_METADATA:
                    return Cast.to(
                            new FakeExpressionFunction<Object, SpreadsheetExpressionEvaluationContext>() {
                                @Override
                                public Object apply(final List<Object> parameters,
                                                    final SpreadsheetExpressionEvaluationContext context) {
                                    return context.spreadsheetMetadata();
                                }

                                @Override
                                public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                    return Lists.of(
                                            ExpressionFunctionParameterName.with("parameters")
                                                    .variable(Object.class)
                                    );
                                }

                                @Override
                                public String toString() {
                                    return TEST_CONTEXT_SPREADSHEET_METADATA;
                                }
                            }
                    );
                default:
                    throw new UnsupportedOperationException("Unknown function: " + functionExpressionName);
            }
        }

        @Override
        public Set<ExpressionFunctionInfo> expressionFunctionInfos() {
            return Sets.of(
                    ExpressionFunctionInfo.with(
                            Url.parseAbsolute("https://example.com/test/xyz"),
                            FunctionExpressionName.with("xyz")
                    ),
                    ExpressionFunctionInfo.with(
                            Url.parseAbsolute("https://example.com/test/" + TEST_CONTEXT_LOADCELL),
                            FunctionExpressionName.with(TEST_CONTEXT_LOADCELL)
                    ),
                    ExpressionFunctionInfo.with(
                            Url.parseAbsolute("https://example.com/test/" + TEST_CONTEXT_SERVER_URL),
                            FunctionExpressionName.with(TEST_CONTEXT_SERVER_URL)
                    )
                    , ExpressionFunctionInfo.with(
                            Url.parseAbsolute("https://example.com/test/" + TEST_CONTEXT_SPREADSHEET_METADATA),
                            FunctionExpressionName.with(TEST_CONTEXT_SPREADSHEET_METADATA)
                    )
            );
        }
    };

    private final static SpreadsheetEngine ENGINE = SpreadsheetEngines.fake();

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

    private final static SpreadsheetStoreRepository STORE_REPOSITORY = SpreadsheetStoreRepositories.fake();

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com/path123");

    private final static Supplier<LocalDateTime> NOW = LocalDateTime::now;

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        null,
                        CONVERTER_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        ENGINE,
                        FRACTIONER,
                        STORE_REPOSITORY,
                        SERVER_URL,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullConverterFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        null,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        ENGINE,
                        FRACTIONER,
                        STORE_REPOSITORY,
                        SERVER_URL,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetComparatorProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        CONVERTER_PROVIDER,
                        null,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        ENGINE,
                        FRACTIONER,
                        STORE_REPOSITORY,
                        SERVER_URL,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetFormatterProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        CONVERTER_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        null,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        ENGINE,
                        FRACTIONER,
                        STORE_REPOSITORY,
                        SERVER_URL,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullExpressionFunctionProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        CONVERTER_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        null,
                        SPREADSHEET_PARSER_PROVIDER,
                        ENGINE,
                        FRACTIONER,
                        STORE_REPOSITORY,
                        SERVER_URL,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullSpreadsheetParserProviderFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        CONVERTER_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        null,
                        ENGINE,
                        FRACTIONER,
                        STORE_REPOSITORY,
                        SERVER_URL,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullEngineFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        CONVERTER_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        null,
                        FRACTIONER,
                        STORE_REPOSITORY,
                        SERVER_URL,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullFractionFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        CONVERTER_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        ENGINE,
                        null,
                        STORE_REPOSITORY,
                        SERVER_URL,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullStoreRepositoryFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        CONVERTER_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        ENGINE,
                        FRACTIONER,
                        null,
                        SERVER_URL,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullServerUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        CONVERTER_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        ENGINE,
                        FRACTIONER,
                        STORE_REPOSITORY,
                        null,
                        NOW
                )
        );
    }

    @Test
    public void testWithNullNowFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        METADATA,
                        CONVERTER_PROVIDER,
                        SPREADSHEET_COMPARATOR_PROVIDER,
                        SPREADSHEET_FORMATTER_PROVIDER,
                        EXPRESSION_FUNCTION_PROVIDER,
                        SPREADSHEET_PARSER_PROVIDER,
                        ENGINE,
                        FRACTIONER,
                        STORE_REPOSITORY,
                        SERVER_URL,
                        null
                )
        );
    }

    // resolveLabel.....................................................................................................

    @Test
    public void testResolveLabelWithLabel() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("Label456");

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.mapping(cell));

        this.resolveLabelAndCheck(
                this.createContext(store),
                label,
                cell
        );
    }

    @Test
    public void testResolveLabelWithLabelToLabelToCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label222");

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label1.mapping(label2));
        store.save(label2.mapping(cell));

        this.resolveLabelAndCheck(
                this.createContext(store),
                label1,
                cell
        );
    }

    // spreadsheetComparator............................................................................................

    @Test
    public void testSpreadsheetComparator() {
        final SpreadsheetComparator<?> comparator = SpreadsheetComparators.text();

        this.spreadsheetComparatorAndCheck(
                this.createContext(),
                comparator.name(),
                comparator
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
                                SpreadsheetParserToken.apostropheSymbol("'", "'"),
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
                                SpreadsheetParserToken.whitespace(" ", " "),
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

    // toExpression.....................................................................................................

    @Test
    public void testToExpression() {
        final BasicSpreadsheetEngineContext context = this.createContext();

        this.toExpressionAndCheck(
                context,
                context.parseFormula(
                        TextCursors.charSequence("=1+2")
                ),
                Expression.add(
                        Expression.value(
                                EXPRESSION_NUMBER_KIND.one()
                        ),
                        Expression.value(
                                EXPRESSION_NUMBER_KIND.create(2)
                        )
                )
        );
    }

    // evaluate.........................................................................................................

    @Test
    public void testEvaluate() {
        this.evaluateAndCheck(Expression.add(this.expression(1), this.expression(2)),
                this.number(1 + 2));
    }

    @Test
    public void testEvaluateWithFunction() {
        this.evaluateAndCheck(
                Expression.call(
                        Expression.namedFunction(
                                FunctionExpressionName.with("xyz")
                        ),
                        Lists.of(
                                this.expression(1),
                                this.expression(2),
                                this.expression(3)
                        )
                ),
                1L + 2 + 3
        );
    }

    private final static SpreadsheetCellReference LOAD_CELL_REFERENCE = SpreadsheetSelection.parseCell("Z99");
    private final static Object LOAD_CELL_VALUE = "LoadCellTextValue";

    @Test
    public void testEvaluateWithFunctionContextLoadCell() {
        this.evaluateAndCheck(
                Expression.call(
                        Expression.namedFunction(
                                FunctionExpressionName.with(TEST_CONTEXT_LOADCELL)
                        ),
                        Lists.of(
                                Expression.reference(
                                        LOAD_CELL_REFERENCE
                                )
                        )
                ),
                LOAD_CELL_VALUE
        );
    }

    @Test
    public void testEvaluateWithFunctionContextServerUrl() {
        this.evaluateAndCheck(
                Expression.call(
                        Expression.namedFunction(
                                FunctionExpressionName.with(TEST_CONTEXT_SERVER_URL)
                        ),
                        Lists.empty()
                ),
                SERVER_URL
        );
    }

    @Test
    public void testEvaluateWithFunctionContextSpreadsheetMetadata() {
        this.evaluateAndCheck(
                Expression.call(
                        Expression.namedFunction(
                                FunctionExpressionName.with(TEST_CONTEXT_SPREADSHEET_METADATA)
                        ),
                        Lists.empty()
                ),
                METADATA
        );
    }

    // formatValue......................................................................................................

    @Test
    public void testFormatValue() {
        this.formatValueAndCheck(
                BigDecimal.valueOf(-125.25),
                SpreadsheetPattern.parseNumberFormatPattern("#.#\"Abc123\"").formatter(),
                SpreadsheetText.with(
                        MINUS + "125" + DECIMAL + "3Abc123"
                )
        );
    }

    // formatValueAndStyle...................................................................................................

    @Test
    public void testFormatValueAndStyle() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("1")
                        .setValue(
                                Optional.of(1)
                        )
        );

        this.formatAndStyleAndCheck(
                this.createContext(
                        METADATA,
                        SpreadsheetLabelStores.fake(),
                        SpreadsheetCellRangeStores.treeMap()
                ),
                cell,
                SpreadsheetPattern.parseNumberFormatPattern("$#.00")
                        .formatter(),
                cell.setFormattedValue(
                        Optional.of(
                                TextNode.text("CURR1.00")
                        )
                )
        );
    }

    @Test
    public void testFormatValueAndStyleWithConditionalFormattingRule() {
        final SpreadsheetCell cell = SpreadsheetSelection.A1.setFormula(
                SpreadsheetFormula.EMPTY.setText("1")
                        .setValue(
                                Optional.of(1)
                        )
        );
        final TextStyle style = TextStyle.EMPTY.set(
                TextStylePropertyName.BACKGROUND_COLOR,
                Color.parse("#123456")
        );

        final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules = SpreadsheetCellRangeStores.treeMap();
        rangeToConditionalFormattingRules.addValue(
                SpreadsheetSelection.A1.toCellRange(),
                SpreadsheetConditionalFormattingRule.with(
                        SpreadsheetDescription.with("Test Description"),
                        1,
                        SpreadsheetFormula.EMPTY.setText("=true()")
                                .setExpression(
                                        Optional.of(
                                                Expression.value(true)
                                        )
                                ),
                        (c) -> style
                )
        );

        this.formatAndStyleAndCheck(
                this.createContext(
                        METADATA,
                        SpreadsheetLabelStores.fake(),
                        rangeToConditionalFormattingRules
                ),
                cell,
                SpreadsheetPattern.parseNumberFormatPattern("$#.00")
                        .formatter(),
                cell.setFormattedValue(
                        Optional.of(
                                TextNode.text("CURR1.00")
                                        .setAttributes(
                                                style.value()
                                        )
                        )
                )
        );
    }

    // evaluateAsBoolean................................................................................................

    @Test
    public void testEvaluateAsBooleanConvertFails() {
        assertThrows(
                ConversionException.class,
                () -> this.createContext()
                        .evaluateAsBoolean(
                                Expression.value(this),
                                Optional.empty()
                        )
        );
    }

    @Test
    public void testEvaluateAsBooleanTrue() {
        final boolean value = true;

        this.evaluateAsBooleanAndCheck(
                this.createContext(),
                Expression.value(value),
                value
        );
    }

    @Test
    public void testEvaluateAsBooleanFalse() {
        final boolean value = false;

        this.evaluateAsBooleanAndCheck(
                this.createContext(),
                Expression.value(value),
                value
        );
    }

    @Test
    public void testEvaluateAsBooleanAddition() {
        this.evaluateAsBooleanAndCheck(
                this.createContext(),
                Expression.add(
                        this.expression(1),
                        this.expression(2)
                ),
                true
        );
    }

    // SpreadsheetFormatterProvider.....................................................................................

    // Default
    //  text-format-pattern
    //    "@"
    //  Hello 123
    @Test
    public void testSpreadsheetFormatterSamples() {
        this.spreadsheetFormatterSamplesAndCheck(
                SpreadsheetFormatterName.TEXT_FORMAT_PATTERN,
                SpreadsheetFormatterSample.with(
                        "Default",
                        SpreadsheetFormatterName.TEXT_FORMAT_PATTERN.setText("@"),
                        "Hello 123"
                )
        );
    }

    // SpreadsheetParserProvider........................................................................................

    @Test
    public void testSpreadsheetFormatterSelector() {
        final SpreadsheetParsePattern pattern = SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy");

        this.spreadsheetFormatterSelectorAndCheck(
                pattern.spreadsheetParserSelector(),
                pattern.toFormat()
                        .spreadsheetFormatterSelector()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createContext(),
                "metadata={\n" +
                        "  \"cell-character-width\": 1,\n" +
                        "  \"color-1\": \"#000000\",\n" +
                        "  \"color-2\": \"#ffffff\",\n" +
                        "  \"color-3\": \"#ff0000\",\n" +
                        "  \"color-4\": \"#00ff00\",\n" +
                        "  \"color-5\": \"#0000ff\",\n" +
                        "  \"color-6\": \"#ffff00\",\n" +
                        "  \"color-7\": \"#ff00ff\",\n" +
                        "  \"color-8\": \"#00ffff\",\n" +
                        "  \"color-9\": \"#800000\",\n" +
                        "  \"color-10\": \"#008000\",\n" +
                        "  \"color-11\": \"#000080\",\n" +
                        "  \"color-12\": \"#808000\",\n" +
                        "  \"color-13\": \"#800080\",\n" +
                        "  \"color-14\": \"#008080\",\n" +
                        "  \"color-15\": \"#c0c0c0\",\n" +
                        "  \"color-16\": \"#808080\",\n" +
                        "  \"color-17\": \"#9999ff\",\n" +
                        "  \"color-18\": \"#993366\",\n" +
                        "  \"color-19\": \"#ffffcc\",\n" +
                        "  \"color-20\": \"#ccffff\",\n" +
                        "  \"color-21\": \"#660066\",\n" +
                        "  \"color-22\": \"#ff8080\",\n" +
                        "  \"color-23\": \"#0066cc\",\n" +
                        "  \"color-24\": \"#ccccff\",\n" +
                        "  \"color-25\": \"#000080\",\n" +
                        "  \"color-26\": \"#ff00ff\",\n" +
                        "  \"color-27\": \"#ffff00\",\n" +
                        "  \"color-28\": \"#00ffff\",\n" +
                        "  \"color-29\": \"#800080\",\n" +
                        "  \"color-30\": \"#800000\",\n" +
                        "  \"color-31\": \"#008080\",\n" +
                        "  \"color-32\": \"#0000ff\",\n" +
                        "  \"color-33\": \"#00ccff\",\n" +
                        "  \"color-34\": \"#ccffff\",\n" +
                        "  \"color-35\": \"#ccffcc\",\n" +
                        "  \"color-36\": \"#ffff99\",\n" +
                        "  \"color-37\": \"#99ccff\",\n" +
                        "  \"color-38\": \"#ff99cc\",\n" +
                        "  \"color-39\": \"#cc99ff\",\n" +
                        "  \"color-40\": \"#ffcc99\",\n" +
                        "  \"color-41\": \"#3366ff\",\n" +
                        "  \"color-42\": \"#33cccc\",\n" +
                        "  \"color-43\": \"#99cc00\",\n" +
                        "  \"color-44\": \"#ffcc00\",\n" +
                        "  \"color-45\": \"#ff9900\",\n" +
                        "  \"color-46\": \"#ff6600\",\n" +
                        "  \"color-47\": \"#666699\",\n" +
                        "  \"color-48\": \"#969696\",\n" +
                        "  \"color-49\": \"#003366\",\n" +
                        "  \"color-50\": \"#339966\",\n" +
                        "  \"color-51\": \"#003300\",\n" +
                        "  \"color-52\": \"#333300\",\n" +
                        "  \"color-53\": \"#993300\",\n" +
                        "  \"color-54\": \"#993366\",\n" +
                        "  \"color-55\": \"#333399\",\n" +
                        "  \"color-56\": \"#333333\",\n" +
                        "  \"color-Black\": 1,\n" +
                        "  \"color-Blue\": 5,\n" +
                        "  \"color-Cyan\": 8,\n" +
                        "  \"color-Green\": 4,\n" +
                        "  \"color-Magenta\": 7,\n" +
                        "  \"color-Red\": 3,\n" +
                        "  \"color-White\": 2,\n" +
                        "  \"color-Yellow\": 6,\n" +
                        "  \"converters\": [\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/basic-spreadsheet-converter\",\n" +
                        "      \"name\": \"basic-spreadsheet-converter\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-throwing\",\n" +
                        "      \"name\": \"error-throwing\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-to-number\",\n" +
                        "      \"name\": \"error-to-number\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-to-string\",\n" +
                        "      \"name\": \"error-to-string\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/general\",\n" +
                        "      \"name\": \"general\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/selection-to-selection\",\n" +
                        "      \"name\": \"selection-to-selection\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-cell-to\",\n" +
                        "      \"name\": \"spreadsheet-cell-to\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/string-to-selection\",\n" +
                        "      \"name\": \"string-to-selection\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"currency-symbol\": \"CURR\",\n" +
                        "  \"date-formatter\": \"date-format-pattern dddd, d mmmm yyyy\",\n" +
                        "  \"date-parser\": \"date-parse-pattern dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                        "  \"date-time-formatter\": \"date-time-format-pattern dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                        "  \"date-time-offset\": \"-25569\",\n" +
                        "  \"date-time-parser\": \"date-time-parse-pattern dd/mm/yyyy hh:mm\",\n" +
                        "  \"decimal-separator\": \".\",\n" +
                        "  \"default-year\": 1900,\n" +
                        "  \"exponent-symbol\": \"e\",\n" +
                        "  \"expression-converter\": \"general\",\n" +
                        "  \"expression-functions\": [],\n" +
                        "  \"expression-number-kind\": \"DOUBLE\",\n" +
                        "  \"general-number-format-digit-count\": 9,\n" +
                        "  \"group-separator\": \",\",\n" +
                        "  \"hide-zero-values\": false,\n" +
                        "  \"locale\": \"en-AU\",\n" +
                        "  \"negative-sign\": \"!\",\n" +
                        "  \"number-formatter\": \"number-format-pattern #,##0.###\",\n" +
                        "  \"number-parser\": \"number-parse-pattern #,##0.###;#,##0\",\n" +
                        "  \"percentage-symbol\": \"%\",\n" +
                        "  \"positive-sign\": \"@\",\n" +
                        "  \"precision\": 10,\n" +
                        "  \"rounding-mode\": \"HALF_UP\",\n" +
                        "  \"spreadsheet-comparators\": [\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date\",\n" +
                        "      \"name\": \"date\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date-time\",\n" +
                        "      \"name\": \"date-time\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-month\",\n" +
                        "      \"name\": \"day-of-month\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-week\",\n" +
                        "      \"name\": \"day-of-week\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-am-pm\",\n" +
                        "      \"name\": \"hour-of-am-pm\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-day\",\n" +
                        "      \"name\": \"hour-of-day\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/minute-of-hour\",\n" +
                        "      \"name\": \"minute-of-hour\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/month-of-year\",\n" +
                        "      \"name\": \"month-of-year\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/nano-of-second\",\n" +
                        "      \"name\": \"nano-of-second\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/number\",\n" +
                        "      \"name\": \"number\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/seconds-of-minute\",\n" +
                        "      \"name\": \"seconds-of-minute\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text\",\n" +
                        "      \"name\": \"text\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text-case-insensitive\",\n" +
                        "      \"name\": \"text-case-insensitive\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/time\",\n" +
                        "      \"name\": \"time\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/year\",\n" +
                        "      \"name\": \"year\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"spreadsheet-formatters\": [\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic\",\n" +
                        "      \"name\": \"automatic\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection\",\n" +
                        "      \"name\": \"collection\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern\",\n" +
                        "      \"name\": \"date-format-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time-format-pattern\",\n" +
                        "      \"name\": \"date-time-format-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/general\",\n" +
                        "      \"name\": \"general\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number-format-pattern\",\n" +
                        "      \"name\": \"number-format-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/spreadsheet-pattern-collection\",\n" +
                        "      \"name\": \"spreadsheet-pattern-collection\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text-format-pattern\",\n" +
                        "      \"name\": \"text-format-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/time-format-pattern\",\n" +
                        "      \"name\": \"time-format-pattern\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"spreadsheet-parsers\": [\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/date-parse-pattern\",\n" +
                        "      \"name\": \"date-parse-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/date-time-parse-pattern\",\n" +
                        "      \"name\": \"date-time-parse-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/number-parse-pattern\",\n" +
                        "      \"name\": \"number-parse-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/time-parse-pattern\",\n" +
                        "      \"name\": \"time-parse-pattern\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"style\": {\n" +
                        "    \"background-color\": \"#ffffff\",\n" +
                        "    \"border-bottom-color\": \"#000000\",\n" +
                        "    \"border-bottom-style\": \"SOLID\",\n" +
                        "    \"border-bottom-width\": \"1px\",\n" +
                        "    \"border-left-color\": \"#000000\",\n" +
                        "    \"border-left-style\": \"SOLID\",\n" +
                        "    \"border-left-width\": \"1px\",\n" +
                        "    \"border-right-color\": \"#000000\",\n" +
                        "    \"border-right-style\": \"SOLID\",\n" +
                        "    \"border-right-width\": \"1px\",\n" +
                        "    \"border-top-color\": \"#000000\",\n" +
                        "    \"border-top-style\": \"SOLID\",\n" +
                        "    \"border-top-width\": \"1px\",\n" +
                        "    \"color\": \"#000000\",\n" +
                        "    \"font-family\": \"MS Sans Serif\",\n" +
                        "    \"font-size\": 11,\n" +
                        "    \"font-style\": \"NORMAL\",\n" +
                        "    \"font-variant\": \"NORMAL\",\n" +
                        "    \"height\": \"30px\",\n" +
                        "    \"hyphens\": \"NONE\",\n" +
                        "    \"margin-bottom\": \"none\",\n" +
                        "    \"margin-left\": \"none\",\n" +
                        "    \"margin-right\": \"none\",\n" +
                        "    \"margin-top\": \"none\",\n" +
                        "    \"padding-bottom\": \"none\",\n" +
                        "    \"padding-left\": \"none\",\n" +
                        "    \"padding-right\": \"none\",\n" +
                        "    \"padding-top\": \"none\",\n" +
                        "    \"text-align\": \"LEFT\",\n" +
                        "    \"text-justify\": \"NONE\",\n" +
                        "    \"vertical-align\": \"TOP\",\n" +
                        "    \"width\": \"100px\",\n" +
                        "    \"word-break\": \"NORMAL\",\n" +
                        "    \"word-wrap\": \"NORMAL\"\n" +
                        "  },\n" +
                        "  \"text-formatter\": \"text-format-pattern @\",\n" +
                        "  \"time-formatter\": \"time-format-pattern h:mm:ss AM/PM\",\n" +
                        "  \"time-parser\": \"time-parse-pattern h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
                        "  \"two-digit-year\": 20,\n" +
                        "  \"value-separator\": \",\",\n" +
                        "  \"viewport\": {\n" +
                        "    \"rectangle\": \"A1:100.0:100.0\"\n" +
                        "  }\n" +
                        "}\n" +
                        "fractioner=Fractioner123\n" +
                        "serverUrl=https://example.com/path123"
        );
    }

    @Test
    public void testToStringMetadataLotsProperties() {
        SpreadsheetMetadata metadata = METADATA;

        for (int i = SpreadsheetColors.MIN; i <= SpreadsheetColors.MAX; i++) {
            metadata = metadata.set(SpreadsheetMetadataPropertyName.numberedColor(i), Color.fromRgb(i));
        }

        this.toStringAndCheck(
                this.createContext(
                        metadata,
                        SpreadsheetLabelStores.treeMap()
                ),
                "metadata={\n" +
                        "  \"cell-character-width\": 1,\n" +
                        "  \"color-1\": \"#000001\",\n" +
                        "  \"color-2\": \"#000002\",\n" +
                        "  \"color-3\": \"#000003\",\n" +
                        "  \"color-4\": \"#000004\",\n" +
                        "  \"color-5\": \"#000005\",\n" +
                        "  \"color-6\": \"#000006\",\n" +
                        "  \"color-7\": \"#000007\",\n" +
                        "  \"color-8\": \"#000008\",\n" +
                        "  \"color-9\": \"#000009\",\n" +
                        "  \"color-10\": \"#00000a\",\n" +
                        "  \"color-11\": \"#00000b\",\n" +
                        "  \"color-12\": \"#00000c\",\n" +
                        "  \"color-13\": \"#00000d\",\n" +
                        "  \"color-14\": \"#00000e\",\n" +
                        "  \"color-15\": \"#00000f\",\n" +
                        "  \"color-16\": \"#000010\",\n" +
                        "  \"color-17\": \"#000011\",\n" +
                        "  \"color-18\": \"#000012\",\n" +
                        "  \"color-19\": \"#000013\",\n" +
                        "  \"color-20\": \"#000014\",\n" +
                        "  \"color-21\": \"#000015\",\n" +
                        "  \"color-22\": \"#000016\",\n" +
                        "  \"color-23\": \"#000017\",\n" +
                        "  \"color-24\": \"#000018\",\n" +
                        "  \"color-25\": \"#000019\",\n" +
                        "  \"color-26\": \"#00001a\",\n" +
                        "  \"color-27\": \"#00001b\",\n" +
                        "  \"color-28\": \"#00001c\",\n" +
                        "  \"color-29\": \"#00001d\",\n" +
                        "  \"color-30\": \"#00001e\",\n" +
                        "  \"color-31\": \"#00001f\",\n" +
                        "  \"color-32\": \"#000020\",\n" +
                        "  \"color-33\": \"#000021\",\n" +
                        "  \"color-34\": \"#000022\",\n" +
                        "  \"color-35\": \"#000023\",\n" +
                        "  \"color-36\": \"#000024\",\n" +
                        "  \"color-37\": \"#000025\",\n" +
                        "  \"color-38\": \"#000026\",\n" +
                        "  \"color-39\": \"#000027\",\n" +
                        "  \"color-40\": \"#000028\",\n" +
                        "  \"color-41\": \"#000029\",\n" +
                        "  \"color-42\": \"#00002a\",\n" +
                        "  \"color-43\": \"#00002b\",\n" +
                        "  \"color-44\": \"#00002c\",\n" +
                        "  \"color-45\": \"#00002d\",\n" +
                        "  \"color-46\": \"#00002e\",\n" +
                        "  \"color-47\": \"#00002f\",\n" +
                        "  \"color-48\": \"#000030\",\n" +
                        "  \"color-49\": \"#000031\",\n" +
                        "  \"color-50\": \"#000032\",\n" +
                        "  \"color-51\": \"#000033\",\n" +
                        "  \"color-52\": \"#000034\",\n" +
                        "  \"color-53\": \"#000035\",\n" +
                        "  \"color-54\": \"#000036\",\n" +
                        "  \"color-55\": \"#000037\",\n" +
                        "  \"color-56\": \"#000038\",\n" +
                        "  \"color-Black\": 1,\n" +
                        "  \"color-Blue\": 5,\n" +
                        "  \"color-Cyan\": 8,\n" +
                        "  \"color-Green\": 4,\n" +
                        "  \"color-Magenta\": 7,\n" +
                        "  \"color-Red\": 3,\n" +
                        "  \"color-White\": 2,\n" +
                        "  \"color-Yellow\": 6,\n" +
                        "  \"converters\": [\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/basic-spreadsheet-converter\",\n" +
                        "      \"name\": \"basic-spreadsheet-converter\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-throwing\",\n" +
                        "      \"name\": \"error-throwing\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-to-number\",\n" +
                        "      \"name\": \"error-to-number\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/error-to-string\",\n" +
                        "      \"name\": \"error-to-string\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/general\",\n" +
                        "      \"name\": \"general\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/selection-to-selection\",\n" +
                        "      \"name\": \"selection-to-selection\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/spreadsheet-cell-to\",\n" +
                        "      \"name\": \"spreadsheet-cell-to\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Converter/string-to-selection\",\n" +
                        "      \"name\": \"string-to-selection\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"currency-symbol\": \"CURR\",\n" +
                        "  \"date-formatter\": \"date-format-pattern dddd, d mmmm yyyy\",\n" +
                        "  \"date-parser\": \"date-parse-pattern dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                        "  \"date-time-formatter\": \"date-time-format-pattern dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                        "  \"date-time-offset\": \"-25569\",\n" +
                        "  \"date-time-parser\": \"date-time-parse-pattern dd/mm/yyyy hh:mm\",\n" +
                        "  \"decimal-separator\": \".\",\n" +
                        "  \"default-year\": 1900,\n" +
                        "  \"exponent-symbol\": \"e\",\n" +
                        "  \"expression-converter\": \"general\",\n" +
                        "  \"expression-functions\": [],\n" +
                        "  \"expression-number-kind\": \"DOUBLE\",\n" +
                        "  \"general-number-format-digit-count\": 9,\n" +
                        "  \"group-separator\": \",\",\n" +
                        "  \"hide-zero-values\": false,\n" +
                        "  \"locale\": \"en-AU\",\n" +
                        "  \"negative-sign\": \"!\",\n" +
                        "  \"number-formatter\": \"number-format-pattern #,##0.###\",\n" +
                        "  \"number-parser\": \"number-parse-pattern #,##0.###;#,##0\",\n" +
                        "  \"percentage-symbol\": \"%\",\n" +
                        "  \"positive-sign\": \"@\",\n" +
                        "  \"precision\": 10,\n" +
                        "  \"rounding-mode\": \"HALF_UP\",\n" +
                        "  \"spreadsheet-comparators\": [\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date\",\n" +
                        "      \"name\": \"date\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date-time\",\n" +
                        "      \"name\": \"date-time\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-month\",\n" +
                        "      \"name\": \"day-of-month\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-week\",\n" +
                        "      \"name\": \"day-of-week\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-am-pm\",\n" +
                        "      \"name\": \"hour-of-am-pm\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-day\",\n" +
                        "      \"name\": \"hour-of-day\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/minute-of-hour\",\n" +
                        "      \"name\": \"minute-of-hour\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/month-of-year\",\n" +
                        "      \"name\": \"month-of-year\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/nano-of-second\",\n" +
                        "      \"name\": \"nano-of-second\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/number\",\n" +
                        "      \"name\": \"number\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/seconds-of-minute\",\n" +
                        "      \"name\": \"seconds-of-minute\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text\",\n" +
                        "      \"name\": \"text\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text-case-insensitive\",\n" +
                        "      \"name\": \"text-case-insensitive\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/time\",\n" +
                        "      \"name\": \"time\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/year\",\n" +
                        "      \"name\": \"year\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"spreadsheet-formatters\": [\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/automatic\",\n" +
                        "      \"name\": \"automatic\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/collection\",\n" +
                        "      \"name\": \"collection\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-format-pattern\",\n" +
                        "      \"name\": \"date-format-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/date-time-format-pattern\",\n" +
                        "      \"name\": \"date-time-format-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/general\",\n" +
                        "      \"name\": \"general\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/number-format-pattern\",\n" +
                        "      \"name\": \"number-format-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/spreadsheet-pattern-collection\",\n" +
                        "      \"name\": \"spreadsheet-pattern-collection\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/text-format-pattern\",\n" +
                        "      \"name\": \"text-format-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetFormatter/time-format-pattern\",\n" +
                        "      \"name\": \"time-format-pattern\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"spreadsheet-parsers\": [\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/date-parse-pattern\",\n" +
                        "      \"name\": \"date-parse-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/date-time-parse-pattern\",\n" +
                        "      \"name\": \"date-time-parse-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/number-parse-pattern\",\n" +
                        "      \"name\": \"number-parse-pattern\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/Parser/time-parse-pattern\",\n" +
                        "      \"name\": \"time-parse-pattern\"\n" +
                        "    }\n" +
                        "  ],\n" +
                        "  \"style\": {\n" +
                        "    \"background-color\": \"#ffffff\",\n" +
                        "    \"border-bottom-color\": \"#000000\",\n" +
                        "    \"border-bottom-style\": \"SOLID\",\n" +
                        "    \"border-bottom-width\": \"1px\",\n" +
                        "    \"border-left-color\": \"#000000\",\n" +
                        "    \"border-left-style\": \"SOLID\",\n" +
                        "    \"border-left-width\": \"1px\",\n" +
                        "    \"border-right-color\": \"#000000\",\n" +
                        "    \"border-right-style\": \"SOLID\",\n" +
                        "    \"border-right-width\": \"1px\",\n" +
                        "    \"border-top-color\": \"#000000\",\n" +
                        "    \"border-top-style\": \"SOLID\",\n" +
                        "    \"border-top-width\": \"1px\",\n" +
                        "    \"color\": \"#000000\",\n" +
                        "    \"font-family\": \"MS Sans Serif\",\n" +
                        "    \"font-size\": 11,\n" +
                        "    \"font-style\": \"NORMAL\",\n" +
                        "    \"font-variant\": \"NORMAL\",\n" +
                        "    \"height\": \"30px\",\n" +
                        "    \"hyphens\": \"NONE\",\n" +
                        "    \"margin-bottom\": \"none\",\n" +
                        "    \"margin-left\": \"none\",\n" +
                        "    \"margin-right\": \"none\",\n" +
                        "    \"margin-top\": \"none\",\n" +
                        "    \"padding-bottom\": \"none\",\n" +
                        "    \"padding-left\": \"none\",\n" +
                        "    \"padding-right\": \"none\",\n" +
                        "    \"padding-top\": \"none\",\n" +
                        "    \"text-align\": \"LEFT\",\n" +
                        "    \"text-justify\": \"NONE\",\n" +
                        "    \"vertical-align\": \"TOP\",\n" +
                        "    \"width\": \"100px\",\n" +
                        "    \"word-break\": \"NORMAL\",\n" +
                        "    \"word-wrap\": \"NORMAL\"\n" +
                        "  },\n" +
                        "  \"text-formatter\": \"text-format-pattern @\",\n" +
                        "  \"time-formatter\": \"time-format-pattern h:mm:ss AM/PM\",\n" +
                        "  \"time-parser\": \"time-parse-pattern h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
                        "  \"two-digit-year\": 20,\n" +
                        "  \"value-separator\": \",\",\n" +
                        "  \"viewport\": {\n" +
                        "    \"rectangle\": \"A1:100.0:100.0\"\n" +
                        "  }\n" +
                        "}\n" +
                        "fractioner=Fractioner123\n" +
                        "serverUrl=https://example.com/path123"
        );
    }

    @Override
    public BasicSpreadsheetEngineContext createContext() {
        return this.createContext(SpreadsheetLabelStores.treeMap());
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetLabelStore labelStore) {
        return this.createContext(
                METADATA,
                labelStore
        );
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata,
                                                        final SpreadsheetLabelStore labelStore) {
        return this.createContext(
                metadata,
                labelStore,
                SpreadsheetCellRangeStores.fake()
        );
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata,
                                                        final SpreadsheetLabelStore labelStore,
                                                        final SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules) {
        final SpreadsheetCellStore cells = SpreadsheetCellStores.treeMap();
        cells.save(
                LOAD_CELL_REFERENCE.setFormula(
                        SpreadsheetFormula.EMPTY
                                .setText("'" + LOAD_CELL_VALUE)
                                .setValue(
                                        Optional.of(LOAD_CELL_VALUE)
                                )
                )
        );

        return BasicSpreadsheetEngineContext.with(
                metadata,
                CONVERTER_PROVIDER,
                SPREADSHEET_COMPARATOR_PROVIDER,
                SPREADSHEET_FORMATTER_PROVIDER,
                EXPRESSION_FUNCTION_PROVIDER,
                SPREADSHEET_PARSER_PROVIDER,
                ENGINE,
                FRACTIONER,
                new FakeSpreadsheetStoreRepository() {

                    @Override
                    public SpreadsheetCellStore cells() {
                        return cells;
                    }

                    @Override
                    public SpreadsheetCellRangeStore<SpreadsheetConditionalFormattingRule> rangeToConditionalFormattingRules() {
                        return rangeToConditionalFormattingRules;
                    }

                    @Override
                    public SpreadsheetLabelStore labels() {
                        return labelStore;
                    }
                },
                SERVER_URL,
                NOW
        );
    }

    private ExpressionNumber number(final Number value) {
        return EXPRESSION_NUMBER_KIND.create(value);
    }

    private ValueExpression<?> expression(final Number value) {
        return Expression.value(
                this.number(value)
        );
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

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.basic(
                CURRENCY,
                DECIMAL,
                EXPONENT,
                GROUP_SEPARATOR,
                MINUS,
                PERCENT,
                PLUS,
                LOCALE,
                new MathContext(MathContext.DECIMAL32.getPrecision(), RoundingMode.HALF_UP)
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<BasicSpreadsheetEngineContext> type() {
        return BasicSpreadsheetEngineContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
