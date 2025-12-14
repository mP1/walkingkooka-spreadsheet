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

package walkingkooka.spreadsheet.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContexts;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.convert.FakeSpreadsheetConverterContext;
import walkingkooka.spreadsheet.engine.SpreadsheetMetadataMode;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.terminal.TerminalContext;
import walkingkooka.terminal.TerminalContexts;
import walkingkooka.terminal.server.TerminalServerContexts;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionFunctionName;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.function.ExpressionFunction;
import walkingkooka.tree.expression.function.ExpressionFunctionParameter;
import walkingkooka.tree.expression.function.ExpressionFunctionParameterKind;
import walkingkooka.tree.expression.function.FakeExpressionFunction;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionInfoSet;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;
import walkingkooka.tree.expression.function.provider.FakeExpressionFunctionProvider;

import java.math.MathContext;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextSpreadsheetContextTest implements SpreadsheetExpressionEvaluationContextTesting<SpreadsheetExpressionEvaluationContextSpreadsheetContext>,
    SpreadsheetMetadataTesting,
    DecimalNumberContextDelegator {

    private final static SpreadsheetCellReference CELL_REFERENCE = SpreadsheetSelection.parseCell("Z9");

    private final static Optional<SpreadsheetCell> CELL = Optional.of(
        CELL_REFERENCE.setFormula(SpreadsheetFormula.EMPTY.setText("'CurrentCell"))
    );

    private final static SpreadsheetExpressionReferenceLoader SPREADSHEET_EXPRESSION_REFERENCE_LOADER = SpreadsheetExpressionReferenceLoaders.fake();

    private final static SpreadsheetMetadataMode MODE = SpreadsheetMetadataMode.FORMULA;

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadataTesting.METADATA_EN_AU.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    ).set(
        SpreadsheetMetadataPropertyName.FUNCTIONS,
        SpreadsheetExpressionFunctions.parseAliasSet("HelloFunction")
    ).set(
        SpreadsheetMetadataPropertyName.FORMULA_FUNCTIONS,
        SpreadsheetExpressionFunctions.parseAliasSet("HelloFunction")
    );

    static {
        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
        store.save(METADATA);

        SPREADSHEET_STORE_REPOSITORY = SpreadsheetStoreRepositories.treeMap(store);
    }

    private final static SpreadsheetStoreRepository SPREADSHEET_STORE_REPOSITORY;

    private final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT = SpreadsheetMetadataTesting.SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
            .setSpreadsheetId(SPREADSHEET_ID);

    private final static SpreadsheetContext SPREADSHEET_CONTEXT = spreadsheetContext(
        SPREADSHEET_ENVIRONMENT_CONTEXT,
        EXPRESSION_FUNCTION_PROVIDER,
        PROVIDER_CONTEXT
    );

    private final static TerminalContext TERMINAL_CONTEXT = TerminalContexts.fake();

    // with.............................................................................................................

    @Test
    public void testWithNullModeFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSpreadsheetContext.with(
                null,
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullCellFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSpreadsheetContext.with(
                MODE,
                null,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetExpressionReferenceLoaderFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSpreadsheetContext.with(
                MODE,
                CELL,
                null,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetLabelNameResolverFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSpreadsheetContext.with(
                MODE,
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                null,
                SPREADSHEET_CONTEXT,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSpreadsheetContext.with(
                MODE,
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                null,
                TERMINAL_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullTerminalContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetExpressionEvaluationContextSpreadsheetContext.with(
                MODE,
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
                SPREADSHEET_LABEL_NAME_RESOLVER,
                SPREADSHEET_CONTEXT,
                null
            )
        );
    }

    // setCell..........................................................................................................

    @Test
    public void testSetCellDifferentCell() {
        final SpreadsheetExpressionEvaluationContextSpreadsheetContext context = this.createContext();

        final Optional<SpreadsheetCell> differentCell = Optional.of(
            SpreadsheetSelection.parseCell("B2")
                .setFormula(
                    SpreadsheetFormula.EMPTY.setText("Different")
                )
        );

        final SpreadsheetExpressionEvaluationContext different = context.setCell(differentCell);
        assertNotSame(
            context,
            different
        );
        this.checkEquals(
            differentCell,
            different.cell(),
            "serverUrl"
        );
    }

    // loadCell.........................................................................................................

    @Test
    public void testLoadCell() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1+22+333")
        );
        cellStore.save(spreadsheetCell);

        this.loadCellAndCheck(
            this.createContext(
                new FakeSpreadsheetExpressionReferenceLoader() {
                    @Override
                    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return cellStore.load(cell);
                    }
                }
            ),
            cell,
            spreadsheetCell
        );
    }

    // nextEmptyColumn..................................................................................................

    @Test
    public void testNextEmptyColumn() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );
        cellStore.save(spreadsheetCell);

        this.nextEmptyColumnAndCheck(
            SpreadsheetExpressionEvaluationContextSpreadsheetContext.with(
                MODE,
                CELL,
                SpreadsheetExpressionReferenceLoaders.fake(),
                SPREADSHEET_LABEL_NAME_RESOLVER,
                spreadsheetContext(cellStore),
                TERMINAL_CONTEXT
            ),
            SpreadsheetSelection.parseRow("1"),
            SpreadsheetSelection.parseColumn("B")
        );
    }

    // nextEmptyRow.....................................................................................................

    @Test
    public void testNextEmptyRow() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final SpreadsheetCell spreadsheetCell = cell.setFormula(
            SpreadsheetFormula.EMPTY.setText("=1")
        );
        cellStore.save(spreadsheetCell);

        this.nextEmptyRowAndCheck(
            SpreadsheetExpressionEvaluationContextSpreadsheetContext.with(
                MODE,
                CELL,
                SpreadsheetExpressionReferenceLoaders.fake(),
                SPREADSHEET_LABEL_NAME_RESOLVER,
                spreadsheetContext(cellStore),
                TERMINAL_CONTEXT
            ),
            SpreadsheetSelection.parseColumn("A"),
            SpreadsheetSelection.parseRow("2")
        );
    }

    // parseFormula.....................................................................................................

    @Test
    public void testParseExpressionQuotedString() {
        final String text = "abc123";
        final String expression = '"' + text + '"';

        this.parseExpressionAndCheck(
            expression,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    SpreadsheetFormulaParserToken.doubleQuoteSymbol("\"", "\""),
                    SpreadsheetFormulaParserToken.textLiteral(text, text),
                    SpreadsheetFormulaParserToken.doubleQuoteSymbol("\"", "\"")
                ),
                expression
            )
        );
    }

    @Test
    public void testParseExpressionNumber() {
        final String text = "123";

        this.parseExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits(text, text)
                ),
                text
            )
        );
    }

    private final static char DECIMAL = '.';

    @Test
    public void testParseExpressionNumber2() {
        final String text = "1" + DECIMAL + "5";

        this.parseExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits("1", "1"),
                    SpreadsheetFormulaParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL),
                    SpreadsheetFormulaParserToken.digits("5", "5")
                ),
                text
            )
        );
    }

    @Test
    public void testParseExpressionAdditionExpression() {
        final String text = "1+2";

        this.parseExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.addition(
                Lists.of(
                    SpreadsheetFormulaParserToken.number(
                        Lists.of(
                            SpreadsheetFormulaParserToken.digits("1", "1")
                        ),
                        "1"
                    ),
                    SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                    SpreadsheetFormulaParserToken.number(
                        Lists.of(
                            SpreadsheetFormulaParserToken.digits("2", "2")
                        ),
                        "2"
                    )
                ),
                text
            )
        );
    }

    @Test
    public void testParseExpressionEqualsAdditionExpressionFails() {
        final String text = "=1+2";

        this.parseExpressionAndFail(
            text,
            "Invalid character '=' at (1,1) expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"TRUE\" | \"FALSE\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    // evaluateExpression...............................................................................................

    @Test
    public void testEvaluateExpressionFunctionParameterValueConvertFails() {
        final ExpressionFunctionName functionName = ExpressionFunctionName.with("HelloFunction");

        this.evaluateExpressionAndCheck(
            this.createContext(
                new FakeExpressionFunctionProvider<>() {
                    @Override
                    public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                            final List<?> values,
                                                                                                            final ProviderContext context) {
                        return new FakeExpressionFunction<>() {
                            @Override
                            public Optional<ExpressionFunctionName> name() {
                                return Optional.of(name);
                            }

                            @Override
                            public List<ExpressionFunctionParameter<?>> parameters(final int count) {
                                return Lists.of(
                                    ExpressionFunctionParameter.NUMBER.setType(Void.class)
                                        .setKinds(
                                            ExpressionFunctionParameterKind.CONVERT_EVALUATE
                                        )
                                );
                            }

                            @Override
                            public Object apply(final List<Object> values,
                                                final SpreadsheetExpressionEvaluationContext context) {
                                ExpressionFunctionParameter.NUMBER.getOrFail(values, 0);
                                throw new UnsupportedOperationException();
                            }
                        };
                    }

                    @Override
                    public ExpressionFunctionInfoSet expressionFunctionInfos() {
                        return SpreadsheetExpressionFunctions.parseInfoSet("https://example.com/HelloFunction HelloFunction");
                    }

                    @Override
                    public CaseSensitivity expressionFunctionNameCaseSensitivity() {
                        return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
                    }
                }
            ),
            Expression.call(
                Expression.namedFunction(functionName),
                Lists.of(
                    Expression.value("String1")
                )
            ),
            SpreadsheetErrorKind.VALUE.setMessage(
                "HelloFunction: number: Cannot convert \"String1\" to Void"
            )
        );
    }

    // parseValueOrExpression...........................................................................................

    @Test
    public void testParseValueOrExpressionDoubleQuotedStringFails() {
        this.parseValueOrExpressionAndFail(
            "\"abc123\"",
            "Invalid character '\\\"' at (1,1) expected \"\\'\", [STRING] | EQUALS_EXPRESSION | VALUE"
        );
    }

    @Test
    public void testParseValueOrExpressionDate() {
        final String text = "1999/12/31";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.date(
                Lists.of(
                    SpreadsheetFormulaParserToken.year(1999, "1999"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.monthNumber(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.dayNumber(31, "31")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionDateTime() {
        final String text = "1999/12/31 12:58";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.dateTime(
                Lists.of(
                    SpreadsheetFormulaParserToken.year(1999, "1999"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.monthNumber(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral("/", "/"),
                    SpreadsheetFormulaParserToken.dayNumber(31, "31"),
                    SpreadsheetFormulaParserToken.whitespace(" ", " "),
                    SpreadsheetFormulaParserToken.hour(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.minute(58, "58")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionNumber() {
        final String text = "123";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits(text, text)
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionNumber2() {
        final String text = "1" + DECIMAL + "5";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.number(
                Lists.of(
                    SpreadsheetFormulaParserToken.digits("1", "1"),
                    SpreadsheetFormulaParserToken.decimalSeparatorSymbol("" + DECIMAL, "" + DECIMAL),
                    SpreadsheetFormulaParserToken.digits("5", "5")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionApostropheString() {
        final String text = "'Hello";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.text(
                Lists.of(
                    SpreadsheetFormulaParserToken.apostropheSymbol("'", "'"),
                    SpreadsheetFormulaParserToken.textLiteral("Hello", "Hello")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionTime() {
        final String text = "12:58:59";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.time(
                Lists.of(
                    SpreadsheetFormulaParserToken.hour(12, "12"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.minute(58, "58"),
                    SpreadsheetFormulaParserToken.textLiteral(":", ":"),
                    SpreadsheetFormulaParserToken.seconds(59, "59")
                ),
                text
            )
        );
    }

    @Test
    public void testParseValueOrExpressionEqualsAdditionExpression() {
        final String text = "=1+2";

        this.parseValueOrExpressionAndCheck(
            text,
            SpreadsheetFormulaParserToken.expression(
                Lists.of(
                    SpreadsheetFormulaParserToken.equalsSymbol("=", "="),
                    SpreadsheetFormulaParserToken.addition(
                        Lists.of(
                            SpreadsheetFormulaParserToken.number(
                                Lists.of(
                                    SpreadsheetFormulaParserToken.digits("1", "1")
                                ),
                                "1"
                            ),
                            SpreadsheetFormulaParserToken.plusSymbol("+", "+"),
                            SpreadsheetFormulaParserToken.number(
                                Lists.of(
                                    SpreadsheetFormulaParserToken.digits("2", "2")
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
    public void testParseValueOrExpressionAdditionExpressionFails() {
        final String text = "1+2";

        this.parseValueOrExpressionAndFail(
            text,
            "Invalid character '1' at (1,1) expected \"\\'\", [STRING] | EQUALS_EXPRESSION | VALUE"
        );
    }

    // evaluateFunction.................................................................................................

    @Test
    public void testEvaluateFunctionMissingParameters() {
        final ExpressionFunctionName functionName = ExpressionFunctionName.with("HelloFunction");

        final SpreadsheetExpressionEvaluationContextSpreadsheetContext context = this.createContext(
            new FakeExpressionFunctionProvider<>() {

                @Override
                public ExpressionFunction<?, SpreadsheetExpressionEvaluationContext> expressionFunction(final ExpressionFunctionName name,
                                                                                                        final List<?> values,
                                                                                                        final ProviderContext context) {
                    return new FakeExpressionFunction<>() {
                        @Override
                        public Optional<ExpressionFunctionName> name() {
                            return Optional.of(functionName);
                        }

                        @Override
                        public List<ExpressionFunctionParameter<?>> parameters(int count) {
                            return Lists.of(
                                ExpressionFunctionParameter.DATE,
                                ExpressionFunctionParameter.DATETIME,
                                ExpressionFunctionParameter.TIME
                            );
                        }

                        @Override
                        public Object apply(final List<Object> parameters,
                                            final SpreadsheetExpressionEvaluationContext context) {
                            this.checkParameterCount(parameters);
                            throw new UnsupportedOperationException();
                        }
                    };
                }

                @Override
                public ExpressionFunctionInfoSet expressionFunctionInfos() {
                    return SpreadsheetExpressionFunctions.parseInfoSet("https://example.com/HelloFunction HelloFunction");
                }

                @Override
                public CaseSensitivity expressionFunctionNameCaseSensitivity() {
                    return SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY;
                }
            }
        );

        final ExpressionFunction<?, ExpressionEvaluationContext> function = context.expressionFunction(
            functionName
        );

        this.evaluateFunctionAndCheck(
            context,
            Cast.to(
                function
            ),
            Lists.of(1),
            Cast.to(
                SpreadsheetErrorKind.VALUE.setMessage("HelloFunction: Missing parameter(s): date-time, time")
            )
        );
    }

    // environmentContext...............................................................................................

    @Test
    public void testSetEnvironmentContextWithDifferent() {
        final SpreadsheetExpressionEvaluationContextSpreadsheetContext context = this.createContext();

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        final SpreadsheetEnvironmentContext differentEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final SpreadsheetExpressionEvaluationContext afterSet = context.setEnvironmentContext(differentEnvironmentContext);
        this.checkNotEquals(
            context,
            afterSet
        );
    }

    // HasLineEndings...................................................................................................

    @Test
    public void testLineEnding() {
        final SpreadsheetEnvironmentContext context = SPREADSHEET_ENVIRONMENT_CONTEXT;

        this.lineEndingAndCheck(
            this.createContext(context),
            context.lineEnding()
        );
    }

    @Test
    public void testSetLineEnding() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT;

        final SpreadsheetExpressionEvaluationContextSpreadsheetContext context = this.createContext(spreadsheetEnvironmentContext);

        final LineEnding lineEnding = LineEnding.CRNL;

        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        context.setLineEnding(lineEnding);

        this.lineEndingAndCheck(
            context,
            lineEnding
        );
    }

    @Test
    public void testLocale() {
        final SpreadsheetEnvironmentContext context = SPREADSHEET_ENVIRONMENT_CONTEXT;

        this.localeAndCheck(
            this.createContext(context),
            context.locale()
        );
    }

    @Test
    public void testSetLocale() {
        final SpreadsheetExpressionEvaluationContextSpreadsheetContext context = this.createContext(SPREADSHEET_ENVIRONMENT_CONTEXT);

        final Locale locale = Locale.GERMAN;
        context.setLocale(locale);

        this.localeAndCheck(
            context,
            locale
        );
    }

    @Test
    public void testEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            name,
            value
        );
    }

    @Test
    public void testSetEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        final SpreadsheetExpressionEvaluationContextSpreadsheetContext context = this.createContext(spreadsheetEnvironmentContext);
        context.setEnvironmentValue(
            name,
            value
        );

        this.environmentValueAndCheck(
            context,
            name,
            value
        );
    }

    @Test
    public void testRemoveEnvironmentValue() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        final SpreadsheetExpressionEvaluationContextSpreadsheetContext context = this.createContext(spreadsheetEnvironmentContext);
        context.removeEnvironmentValue(name);

        this.environmentValueAndCheck(
            context,
            name
        );
    }

    @Test
    public void testUser() {
        final EmailAddress user = EmailAddress.parse("user123@example.com");

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment()
            .setUser(
                Optional.of(user)
            );

        this.userAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            user
        );
    }

    @Test
    public void testReferenceWithEnvironmentValueName() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<String> name = EnvironmentValueName.with("Hello");
        final String value = "Hello World123";

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            value
        );

        this.referenceAndCheck(
            this.createContext(spreadsheetEnvironmentContext),
            name,
            value
        );
    }

    @Test
    public void testReferenceWithEnvironmentValueNameCycle() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<SpreadsheetLabelName> name = EnvironmentValueName.with("Hello");
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            label
        );

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final String value = "Hello World123";

        this.referenceFails(
            this.createContext(
                spreadsheetEnvironmentContext,
                new FakeSpreadsheetExpressionReferenceLoader() {

                    @Override
                    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference c,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return Optional.ofNullable(
                            cell.equalsIgnoreReferenceKind(c) ?
                                cell.setFormula(
                                    SpreadsheetFormula.EMPTY.setValue(
                                        Optional.of(name)
                                    )
                                ) :
                                null
                        );
                    }
                },
                new FakeSpreadsheetLabelNameResolver() {
                    @Override
                    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName l) {
                        return Optional.ofNullable(
                            label.equals(l) ?
                                cell :
                                null
                        );
                    }
                }
            ),
            name,
            new IllegalArgumentException("Cycle detected from Hello with Hello")
        );
    }

    @Test
    public void testReferenceWithEnvironmentValueNameEqualsLabel() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment();

        final EnvironmentValueName<SpreadsheetLabelName> name = EnvironmentValueName.with("Hello");
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        spreadsheetEnvironmentContext.setEnvironmentValue(
            name,
            label
        );

        final SpreadsheetCellReference cell = SpreadsheetSelection.A1;
        final String value = "Hello World123";

        this.referenceAndCheck(
            this.createContext(
                spreadsheetEnvironmentContext,
                new FakeSpreadsheetExpressionReferenceLoader() {

                    @Override
                    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference c,
                                                              final SpreadsheetExpressionEvaluationContext context) {
                        return Optional.ofNullable(
                            cell.equalsIgnoreReferenceKind(c) ?
                                cell.setFormula(
                                    SpreadsheetFormula.EMPTY.setValue(
                                        Optional.of(value)
                                    )
                                ) :
                                null
                        );
                    }
                },
                new FakeSpreadsheetConverterContext() {
                    @Override
                    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName l) {
                        return Optional.ofNullable(
                            label.equals(l) ?
                                cell :
                                null
                        );
                    }
                }
            ),
            name,
            value
        );
    }

    // ExpressionEvaluationContextTesting................................................................................

    @Override
    public SpreadsheetExpressionEvaluationContextSpreadsheetContext createContext() {
        return this.createContext(SPREADSHEET_EXPRESSION_REFERENCE_LOADER);
    }

    private SpreadsheetExpressionEvaluationContextSpreadsheetContext createContext(final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader) {
        return this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
            spreadsheetExpressionReferenceLoader,
            SPREADSHEET_LABEL_NAME_RESOLVER
        );
    }

    private SpreadsheetExpressionEvaluationContextSpreadsheetContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext) {
        return this.createContext(
            spreadsheetEnvironmentContext,
            SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
            SPREADSHEET_LABEL_NAME_RESOLVER
        );
    }

    private SpreadsheetExpressionEvaluationContextSpreadsheetContext createContext(final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider) {
        return this.createContext(
            SPREADSHEET_EXPRESSION_REFERENCE_LOADER,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            expressionFunctionProvider
        );
    }

    private SpreadsheetExpressionEvaluationContextSpreadsheetContext createContext(final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                                                   final SpreadsheetLabelNameResolver labelNameResolver,
                                                                                   final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider) {
        return this.createContext(
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
            spreadsheetExpressionReferenceLoader,
            labelNameResolver,
            expressionFunctionProvider,
            PROVIDER_CONTEXT
        );
    }

    private SpreadsheetExpressionEvaluationContextSpreadsheetContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                   final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                                                   final SpreadsheetLabelNameResolver labelNameResolver) {
        return createContext(
            spreadsheetEnvironmentContext,
            spreadsheetExpressionReferenceLoader,
            labelNameResolver,
            EXPRESSION_FUNCTION_PROVIDER,
            ProviderContexts.fake()
        );
    }

    private SpreadsheetExpressionEvaluationContextSpreadsheetContext createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                                                   final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                                                                                   final SpreadsheetLabelNameResolver labelNameResolver,
                                                                                   final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                                                   final ProviderContext providerContext) {
        return SpreadsheetExpressionEvaluationContextSpreadsheetContext.with(
            MODE,
            CELL,
            spreadsheetExpressionReferenceLoader,
            labelNameResolver,
            spreadsheetContext(
                spreadsheetEnvironmentContext,
                expressionFunctionProvider,
                providerContext
            ),
            TERMINAL_CONTEXT
        );
    }

    private static SpreadsheetContext spreadsheetContext(final SpreadsheetCellStore cellStore) {
        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
        store.save(METADATA);

        return spreadsheetContext(
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetCellStore cells() {
                    return cellStore;
                }

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return store;
                }
            },
            SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment(),
            EXPRESSION_FUNCTION_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    private static SpreadsheetContext spreadsheetContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                         final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                         final ProviderContext providerContext) {
        return spreadsheetContext(
            SPREADSHEET_STORE_REPOSITORY,
            spreadsheetEnvironmentContext,
            expressionFunctionProvider,
            providerContext
        );
    }

    private static SpreadsheetContext spreadsheetContext(final SpreadsheetStoreRepository storeRepository,
                                                         final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                         final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider,
                                                         final ProviderContext providerContext) {
        return SpreadsheetContexts.basic(
            (id) -> {
                if(SPREADSHEET_ID.equals(id)) {
                    return storeRepository;
                }
                throw new IllegalArgumentException("Unknown SpreadsheetId: " + id);
            },
            spreadsheetProvider(expressionFunctionProvider),
            (c) -> {
                throw new UnsupportedOperationException();
            }, // Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory
            (c) -> {
                throw new UnsupportedOperationException();
            }, // Function<SpreadsheetEngineContext, Router<HttpRequestAttribute<?>, HttpHandler>> httpRouterFactory
            spreadsheetEnvironmentContext,
            LOCALE_CONTEXT,
            providerContext,
            TerminalServerContexts.fake()
        );
    }

    private static SpreadsheetProvider spreadsheetProvider(final ExpressionFunctionProvider<SpreadsheetExpressionEvaluationContext> expressionFunctionProvider) {
        return SpreadsheetProviders.basic(
            CONVERTER_PROVIDER,
            expressionFunctionProvider,
            SPREADSHEET_COMPARATOR_PROVIDER,
            SPREADSHEET_EXPORTER_PROVIDER,
            SPREADSHEET_FORMATTER_PROVIDER,
            FORM_HANDLER_PROVIDER,
            SPREADSHEET_IMPORTER_PROVIDER,
            SPREADSHEET_PARSER_PROVIDER,
            VALIDATOR_PROVIDER
        );
    }

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    // HasConverter.....................................................................................................

    @Test
    public void testConverter() {
        final SpreadsheetExpressionEvaluationContextSpreadsheetContext context = this.createContext();

        final ExpressionNumber from = context.expressionNumberKind().create(123);
        final String to = context.convertOrFail(from, String.class);

        this.checkEquals(
            to,
            context.converter().convertOrFail(from, String.class, context),
            () -> "converter with context and context convertOrFail should return the same"
        );
    }

    // DecimalNumberContext.............................................................................................

    @Override
    public int decimalNumberDigitCount() {
        return DECIMAL_NUMBER_CONTEXT.decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DECIMAL_NUMBER_CONTEXT;
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = METADATA.decimalNumberContext(
        SpreadsheetMetadata.NO_CELL,
        LOCALE_CONTEXT
    );

    @Override
    public void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    // Class............................................................................................................

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<SpreadsheetExpressionEvaluationContextSpreadsheetContext> type() {
        return SpreadsheetExpressionEvaluationContextSpreadsheetContext.class;
    }
}
