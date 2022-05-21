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
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.FakeSpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.ValueExpression;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetEngineContextTest implements SpreadsheetEngineContextTesting<BasicSpreadsheetEngineContext> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    private final static Locale LOCALE = Locale.forLanguageTag("EN-AU");
    private final static char VALUE_SEPARATOR = ',';
    private final static int WIDTH = 1;
    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("http://example.com/path123");

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        null,
                        this.functions(),
                        this.engine(),
                        FRACTIONER,
                        this.storeRepository(),
                        SERVER_URL
                )
        );
    }

    @Test
    public void testWithNullFunctionsFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        this.metadata(),
                        null,
                        this.engine(),
                        FRACTIONER,
                        this.storeRepository(),
                        SERVER_URL
                )
        );
    }

    @Test
    public void testWithNullEngineFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        this.metadata(),
                        this.functions(),
                        null,
                        FRACTIONER,
                        this.storeRepository(),
                        SERVER_URL
                )
        );
    }

    @Test
    public void testWithNullFractionFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        this.metadata(),
                        this.functions(),
                        this.engine(),
                        null,
                        this.storeRepository(),
                        SERVER_URL
                )
        );
    }

    @Test
    public void testWithNullStoreRepositoryFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        this.metadata(),
                        this.functions(),
                        this.engine(),
                        FRACTIONER,
                        null,
                        SERVER_URL
                )
        );
    }

    @Test
    public void testWithNullServerUrlFails() {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetEngineContext.with(
                        this.metadata(),
                        this.functions(),
                        this.engine(),
                        FRACTIONER,
                        this.storeRepository(),
                        null
                )
        );
    }

    // resolveCellReference..............................................................................................

    @Test
    public void testResolveCellReferenceCellReference() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("A1");
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
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCell("A1");
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
        final SpreadsheetCellReference cell = SpreadsheetCellReference.parseCell("A1");
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

    private final static SpreadsheetCellReference LOAD_CELL_REFERENCE = SpreadsheetSelection.parseCell("Z99");
    private final static Object LOAD_CELL_VALUE = "LoadCellTextValue";

    @Test
    public void testEvaluateWithFunctionContextLoadCell() {
        this.evaluateAndCheck(
                Expression.function(
                        FunctionExpressionName.with(TEST_CONTEXT_LOADCELL),
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
                Expression.function(
                        FunctionExpressionName.with(TEST_CONTEXT_SERVER_URL),
                        Lists.empty()
                ),
                SERVER_URL
        );
    }

    @Test
    public void testEvaluateWithFunctionContextSpreadsheetMetadata() {
        this.evaluateAndCheck(
                Expression.function(
                        FunctionExpressionName.with(TEST_CONTEXT_SPREADSHEET_METADATA),
                        Lists.empty()
                ),
                this.metadata()
        );
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
                        "  \"date-time-parse-patterns\": \"dd/mm/yyyy hh:mm\",\n" +
                        "  \"decimal-separator\": \".\",\n" +
                        "  \"default-year\": 1900,\n" +
                        "  \"exponent-symbol\": \"e\",\n" +
                        "  \"expression-number-kind\": \"DOUBLE\",\n" +
                        "  \"grouping-separator\": \",\",\n" +
                        "  \"locale\": \"en-AU\",\n" +
                        "  \"negative-sign\": \"!\",\n" +
                        "  \"number-format-pattern\": \"#,##0.###\",\n" +
                        "  \"number-parse-patterns\": \"#,##0.###;#,##0\",\n" +
                        "  \"percentage-symbol\": \"%\",\n" +
                        "  \"positive-sign\": \"@\",\n" +
                        "  \"precision\": 10,\n" +
                        "  \"rounding-mode\": \"HALF_UP\",\n" +
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
                        "  \"text-format-pattern\": \"@\",\n" +
                        "  \"time-format-pattern\": \"h:mm:ss AM/PM\",\n" +
                        "  \"time-parse-patterns\": \"h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
                        "  \"two-digit-year\": 20,\n" +
                        "  \"value-separator\": \",\",\n" +
                        "  \"viewport-cell\": \"A1\"\n" +
                        "}\n" +
                        "fractioner=Fractioner123\n" +
                        "serverUrl=" + SERVER_URL
        );
    }

    @Test
    public void testToStringMetadataLotsProperties() {
        SpreadsheetMetadata metadata = this.metadata();

        for (int i = 0; i < 60; i++) {
            metadata = metadata.set(SpreadsheetMetadataPropertyName.numberedColor(i), Color.fromRgb(i));
        }

        this.toStringAndCheck(
                this.createContext(metadata, SpreadsheetLabelStores.treeMap()),
                "metadata={\n" +
                        "  \"cell-character-width\": 1,\n" +
                        "  \"color-0\": \"#000000\",\n" +
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
                        "  \"color-57\": \"#000039\",\n" +
                        "  \"color-58\": \"#00003a\",\n" +
                        "  \"color-59\": \"#00003b\",\n" +
                        "  \"currency-symbol\": \"CURR\",\n" +
                        "  \"date-format-pattern\": \"dddd, d mmmm yyyy\",\n" +
                        "  \"date-parse-patterns\": \"dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                        "  \"date-time-format-pattern\": \"dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                        "  \"date-time-offset\": \"-25569\",\n" +
                        "  \"date-time-parse-patterns\": \"dd/mm/yyyy hh:mm\",\n" +
                        "  \"decimal-separator\": \".\",\n" +
                        "  \"default-year\": 1900,\n" +
                        "  \"exponent-symbol\": \"e\",\n" +
                        "  \"expression-number-kind\": \"DOUBLE\",\n" +
                        "  \"grouping-separator\": \",\",\n" +
                        "  \"locale\": \"en-AU\",\n" +
                        "  \"negative-sign\": \"!\",\n" +
                        "  \"number-format-pattern\": \"#,##0.###\",\n" +
                        "  \"number-parse-patterns\": \"#,##0.###;#,##0\",\n" +
                        "  \"percentage-symbol\": \"%\",\n" +
                        "  \"positive-sign\": \"@\",\n" +
                        "  \"precision\": 10,\n" +
                        "  \"rounding-mode\": \"HALF_UP\",\n" +
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
                        "  \"text-format-pattern\": \"@\",\n" +
                        "  \"time-format-pattern\": \"h:mm:ss AM/PM\",\n" +
                        "  \"time-parse-patterns\": \"h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
                        "  \"two-digit-year\": 20,\n" +
                        "  \"value-separator\": \",\",\n" +
                        "  \"viewport-cell\": \"A1\"\n" +
                        "}\n" +
                        "fractioner=Fractioner123\n" +
                        "serverUrl=" + SERVER_URL
        );
    }

    @Override
    public BasicSpreadsheetEngineContext createContext() {
        return this.createContext(SpreadsheetLabelStores.treeMap());
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetLabelStore labelStore) {
        return this.createContext(this.metadata(), labelStore);
    }

    private BasicSpreadsheetEngineContext createContext(final SpreadsheetMetadata metadata,
                                                        final SpreadsheetLabelStore labelStore) {
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
                this.functions(),
                this.engine(),
                FRACTIONER,
                new FakeSpreadsheetStoreRepository() {

                    @Override
                    public SpreadsheetCellStore cells() {
                        return cells;
                    }

                    @Override
                    public SpreadsheetLabelStore labels() {
                        return labelStore;
                    }
                },
                SERVER_URL
        );
    }

    private SpreadsheetMetadata metadata() {
        return SpreadsheetMetadata.NON_LOCALE_DEFAULTS
                .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
                .loadFromLocale()
                .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERNS, SpreadsheetFormatPattern.parseDateTimeParsePatterns("dd/mm/yyyy hh:mm"))
                .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetFormatPattern.parseTextFormatPattern("@"))
                .set(SpreadsheetMetadataPropertyName.CURRENCY_SYMBOL, CURRENCY)
                .set(SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR, DECIMAL)
                .set(SpreadsheetMetadataPropertyName.EXPONENT_SYMBOL, EXPONENT)
                .set(SpreadsheetMetadataPropertyName.GROUPING_SEPARATOR, GROUPING)
                .set(SpreadsheetMetadataPropertyName.NEGATIVE_SIGN, MINUS)
                .set(SpreadsheetMetadataPropertyName.POSITIVE_SIGN, PLUS)
                .set(SpreadsheetMetadataPropertyName.CELL_CHARACTER_WIDTH, WIDTH)
                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, EXPRESSION_NUMBER_KIND)
                .set(SpreadsheetMetadataPropertyName.VALUE_SEPARATOR, VALUE_SEPARATOR);
    }

    private ExpressionNumber number(final Number value) {
        return EXPRESSION_NUMBER_KIND.create(value);
    }

    private ValueExpression<?> expression(final Number value) {
        return Expression.value(
                this.number(value)
        );
    }

    private final static String TEST_CONTEXT_LOADCELL = "test-context-loadCell";

    private final static String TEST_CONTEXT_SERVER_URL = "test-context-serverUrl";

    private final static String TEST_CONTEXT_SPREADSHEET_METADATA = "test-context-spreadsheet-metadata";

    private Function<FunctionExpressionName, ExpressionFunction<?, ExpressionEvaluationContext>> functions() {
        return (n) -> {
            switch (n.value()) {
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
                                public List<ExpressionFunctionParameter<?>> parameters() {
                                    return Lists.of(
                                            ExpressionFunctionParameterName.with("parameters").variable(Object.class)
                                    );
                                }

                                @Override
                                public Set<ExpressionFunctionKind> kinds() {
                                    return Sets.of(
                                            ExpressionFunctionKind.EVALUATE_PARAMETERS
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
                                public List<ExpressionFunctionParameter<?>> parameters() {
                                    return Lists.of(
                                            ExpressionFunctionParameterName.with("parameters").variable(Object.class)
                                    );
                                }

                                @Override
                                public Set<ExpressionFunctionKind> kinds() {
                                    return Sets.of(
                                            ExpressionFunctionKind.EVALUATE_PARAMETERS
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
                                public List<ExpressionFunctionParameter<?>> parameters() {
                                    return Lists.of(
                                            ExpressionFunctionParameterName.with("parameters").variable(Object.class)
                                    );
                                }

                                @Override
                                public Set<ExpressionFunctionKind> kinds() {
                                    return Sets.empty();
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
                                public List<ExpressionFunctionParameter<?>> parameters() {
                                    return Lists.of(
                                            ExpressionFunctionParameterName.with("parameters").variable(Object.class)
                                    );
                                }

                                @Override
                                public Set<ExpressionFunctionKind> kinds() {
                                    return Sets.empty();
                                }

                                @Override
                                public String toString() {
                                    return TEST_CONTEXT_SPREADSHEET_METADATA;
                                }
                            }
                    );
                default:
                    throw new UnsupportedOperationException("Unknown expression: " + n);
            }
        };
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

    SpreadsheetStoreRepository storeRepository() {
        return SpreadsheetStoreRepositories.fake();
    }

    @Override
    public Class<BasicSpreadsheetEngineContext> type() {
        return BasicSpreadsheetEngineContext.class;
    }
}
