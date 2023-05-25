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
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.FakeDateTimeContext;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.math.Fraction;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.SpreadsheetColors;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
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
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.expression.function.FakeExpressionFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetEngineContextTest implements SpreadsheetEngineContextTesting<BasicSpreadsheetEngineContext> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;
    private final static Locale LOCALE = Locale.forLanguageTag("EN-AU");
    private final static char VALUE_SEPARATOR = ',';
    private final static int WIDTH = 1;
    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com/path123");

    private final static Supplier<LocalDateTime> NOW = LocalDateTime::now;

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
                        SERVER_URL,
                        NOW
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
                        this.metadata(),
                        this.functions(),
                        null,
                        FRACTIONER,
                        this.storeRepository(),
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
                        this.metadata(),
                        this.functions(),
                        this.engine(),
                        null,
                        this.storeRepository(),
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
                        this.metadata(),
                        this.functions(),
                        this.engine(),
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
                        this.metadata(),
                        this.functions(),
                        this.engine(),
                        FRACTIONER,
                        this.storeRepository(),
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
                        this.metadata(),
                        this.functions(),
                        this.engine(),
                        FRACTIONER,
                        this.storeRepository(),
                        SERVER_URL,
                        null
                )
        );
    }

    // resolveIfLabel..............................................................................................

    @Test
    public void testResolveIfLabelLabelUnknownFails() {
        final RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> this.createContext(
                        SpreadsheetLabelStores.treeMap()
                ).resolveIfLabel(SpreadsheetLabelName.labelName("UnknownLabel123"))
        );
        this.checkEquals(
                "Label not found: UnknownLabel123",
                thrown.getMessage(),
                "message"
        );
    }

    @Test
    public void testResolveIfLabelLabel() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("Label456");

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.mapping(cell));

        this.resolveIfLabelAndCheck(
                this.createContext(store),
                label,
                cell
        );
    }

    @Test
    public void testResolveIfLabelLabelToLabelToCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label222");

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label1.mapping(label2));
        store.save(label2.mapping(cell));

        this.resolveIfLabelAndCheck(
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
                this.metadata()
        );
    }

    @Test
    public void testFormat() {
        this.formatAndCheck(
                BigDecimal.valueOf(-123.45),
                SpreadsheetPattern.parseNumberFormatPattern("#.#\"Abc123\"").formatter(),
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
                        "  \"color-Black\": \"#000000\",\n" +
                        "  \"color-Blue\": \"#0000ff\",\n" +
                        "  \"color-Cyan\": \"#00ffff\",\n" +
                        "  \"color-Green\": \"#00ff00\",\n" +
                        "  \"color-Magenta\": \"#ff00ff\",\n" +
                        "  \"color-Red\": \"#ff0000\",\n" +
                        "  \"color-White\": \"#ffffff\",\n" +
                        "  \"color-Yellow\": \"#ffff00\",\n" +
                        "  \"currency-symbol\": \"CURR\",\n" +
                        "  \"date-format-pattern\": \"dddd, d mmmm yyyy\",\n" +
                        "  \"date-parse-pattern\": \"dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                        "  \"date-time-format-pattern\": \"dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                        "  \"date-time-offset\": \"-25569\",\n" +
                        "  \"date-time-parse-pattern\": \"dd/mm/yyyy hh:mm\",\n" +
                        "  \"decimal-separator\": \".\",\n" +
                        "  \"default-year\": 1900,\n" +
                        "  \"exponent-symbol\": \"e\",\n" +
                        "  \"expression-number-kind\": \"DOUBLE\",\n" +
                        "  \"grouping-separator\": \",\",\n" +
                        "  \"locale\": \"en-AU\",\n" +
                        "  \"negative-sign\": \"!\",\n" +
                        "  \"number-format-pattern\": \"#,##0.###\",\n" +
                        "  \"number-parse-pattern\": \"#,##0.###;#,##0\",\n" +
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
                        "  \"time-parse-pattern\": \"h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
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
                        "  \"color-Black\": \"#000000\",\n" +
                        "  \"color-Blue\": \"#0000ff\",\n" +
                        "  \"color-Cyan\": \"#00ffff\",\n" +
                        "  \"color-Green\": \"#00ff00\",\n" +
                        "  \"color-Magenta\": \"#ff00ff\",\n" +
                        "  \"color-Red\": \"#ff0000\",\n" +
                        "  \"color-White\": \"#ffffff\",\n" +
                        "  \"color-Yellow\": \"#ffff00\",\n" +
                        "  \"currency-symbol\": \"CURR\",\n" +
                        "  \"date-format-pattern\": \"dddd, d mmmm yyyy\",\n" +
                        "  \"date-parse-pattern\": \"dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m\",\n" +
                        "  \"date-time-format-pattern\": \"dddd, d mmmm yyyy \\\\a\\\\t h:mm:ss AM/PM\",\n" +
                        "  \"date-time-offset\": \"-25569\",\n" +
                        "  \"date-time-parse-pattern\": \"dd/mm/yyyy hh:mm\",\n" +
                        "  \"decimal-separator\": \".\",\n" +
                        "  \"default-year\": 1900,\n" +
                        "  \"exponent-symbol\": \"e\",\n" +
                        "  \"expression-number-kind\": \"DOUBLE\",\n" +
                        "  \"grouping-separator\": \",\",\n" +
                        "  \"locale\": \"en-AU\",\n" +
                        "  \"negative-sign\": \"!\",\n" +
                        "  \"number-format-pattern\": \"#,##0.###\",\n" +
                        "  \"number-parse-pattern\": \"#,##0.###;#,##0\",\n" +
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
                        "  \"time-parse-pattern\": \"h:mm:ss AM/PM;h:mm:ss;h:mm:ss.0;h:mm AM/PM;h:mm\",\n" +
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
                SERVER_URL,
                NOW
        );
    }

    private SpreadsheetMetadata metadata() {
        return SpreadsheetMetadata.NON_LOCALE_DEFAULTS
                .set(SpreadsheetMetadataPropertyName.LOCALE, LOCALE)
                .loadFromLocale()
                .set(SpreadsheetMetadataPropertyName.DATETIME_PARSE_PATTERN, SpreadsheetFormatPattern.parseDateTimeParsePattern("dd/mm/yyyy hh:mm"))
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
