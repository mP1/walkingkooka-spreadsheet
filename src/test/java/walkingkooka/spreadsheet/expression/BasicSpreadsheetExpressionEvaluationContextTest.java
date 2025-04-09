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
import walkingkooka.collect.list.Lists;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.FakeSpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoaders;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

import java.math.MathContext;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetExpressionEvaluationContextTest implements SpreadsheetExpressionEvaluationContextTesting<BasicSpreadsheetExpressionEvaluationContext>,
        SpreadsheetMetadataTesting {

    private final static SpreadsheetCellReference CELL_REFERENCE = SpreadsheetSelection.parseCell("Z9");

    private final static Optional<SpreadsheetCell> CELL = Optional.of(
            CELL_REFERENCE.setFormula(SpreadsheetFormula.EMPTY.setText("'CurrentCell"))
    );

    private final static SpreadsheetExpressionReferenceLoader SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT = SpreadsheetExpressionReferenceLoaders.fake(); // SpreadsheetExpressionReferenceContextREPOSITORY

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadataTesting.METADATA_EN_AU.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
            SpreadsheetId.with(1)
    );

    private final static SpreadsheetStoreRepository SPREADSHEET_STORE_REPOSITORY = SpreadsheetStoreRepositories.fake();

    // with.............................................................................................................

    @Test
    public void testWithNullCellFails() {
        this.withFails(
                null,
                SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT,
                SERVER_URL,
                //REFERENCE_TO_VALUE,
                METADATA,
                SPREADSHEET_STORE_REPOSITORY,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullSpreadsheetExpressionReferenceContextFails() {
        this.withFails(
                CELL,
                null,
                SERVER_URL,
                METADATA,
                SPREADSHEET_STORE_REPOSITORY,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullServerUrlFails() {
        this.withFails(
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT,
                null,
                METADATA,
                SPREADSHEET_STORE_REPOSITORY,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullMetadataFails() {
        this.withFails(
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT,
                SERVER_URL,
                null,
                SPREADSHEET_STORE_REPOSITORY,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullStorageFails() {
        this.withFails(
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT,
                SERVER_URL,
                METADATA,
                null,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullSpreadsheetConverterContextFails() {
        this.withFails(
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT,
                SERVER_URL,
                METADATA,
                SPREADSHEET_STORE_REPOSITORY,
                null,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT

        );
    }

    @Test
    public void testWithNullExpressionFunctionProviderFails() {
        this.withFails(
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT,
                SERVER_URL,
                METADATA,
                SPREADSHEET_STORE_REPOSITORY,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                null,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        this.withFails(
                CELL,
                SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT,
                SERVER_URL,
                METADATA,
                SPREADSHEET_STORE_REPOSITORY,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                null
        );
    }

    private void withFails(final Optional<SpreadsheetCell> cell,
                           final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader,
                           final AbsoluteUrl serverUrl,
                           final SpreadsheetMetadata spreadsheetMetadata,
                           final SpreadsheetStoreRepository spreadsheetStoreRepository,
                           final SpreadsheetConverterContext spreadsheetConverterContext,
                           final ExpressionFunctionProvider expressionFunctionProvider,
                           final ProviderContext providerContext) {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetExpressionEvaluationContext.with(
                        cell,
                        spreadsheetExpressionReferenceLoader,
                        serverUrl,
                        spreadsheetMetadata,
                        spreadsheetStoreRepository,
                        spreadsheetConverterContext,
                        expressionFunctionProvider,
                        providerContext
                )
        );
    }

    // setCell..........................................................................................................

    @Test
    public void testSetCellDifferentCell() {
        final BasicSpreadsheetExpressionEvaluationContext context = this.createContext();

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
                Optional.of(spreadsheetCell)
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
                BasicSpreadsheetExpressionEvaluationContext.with(
                        CELL,
                        SpreadsheetExpressionReferenceLoaders.fake(),
                        SERVER_URL,
                        METADATA,
                        new FakeSpreadsheetStoreRepository() {
                            @Override
                            public SpreadsheetCellStore cells() {
                                return cellStore;
                            }
                        },
                        SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                        EXPRESSION_FUNCTION_PROVIDER,
                        PROVIDER_CONTEXT
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
                BasicSpreadsheetExpressionEvaluationContext.with(
                        CELL,
                        SpreadsheetExpressionReferenceLoaders.fake(),
                        SERVER_URL,
                        METADATA,
                        new FakeSpreadsheetStoreRepository() {
                            @Override
                            public SpreadsheetCellStore cells() {
                                return cellStore;
                            }
                        },
                        SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                        EXPRESSION_FUNCTION_PROVIDER,
                        PROVIDER_CONTEXT
                ),
                SpreadsheetSelection.parseColumn("A"),
                SpreadsheetSelection.parseRow("2")
        );
    }

    // parseFormula.....................................................................................................

    @Test
    public void testParseFormulaQuotedString() {
        final String text = "abc123";
        final String expression = '"' + text + '"';

        this.parseFormulaAndCheck(
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
    public void testParseFormulaNumber() {
        final String text = "123";

        this.parseFormulaAndCheck(
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
    public void testParseFormulaNumber2() {
        final String text = "1" + DECIMAL + "5";

        this.parseFormulaAndCheck(
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
    public void testParseFormulaAdditionExpression() {
        final String text = "1+2";

        this.parseFormulaAndCheck(
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
    public void testParseFormulaEqualsAdditionExpressionFails() {
        final String text = "=1+2";

        this.parseFormulaAndFail(
                text,
                "Invalid character '=' at (1,1) expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | \"true\" | \"false\" | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    // ExpressionEvaluationContextTesting................................................................................

    @Override
    public BasicSpreadsheetExpressionEvaluationContext createContext() {
        return this.createContext(SPREADSHEET_EXPRESSION_REFERENCE_CONTEXT);
    }

    private BasicSpreadsheetExpressionEvaluationContext createContext(final SpreadsheetExpressionReferenceLoader spreadsheetExpressionReferenceLoader) {
        return BasicSpreadsheetExpressionEvaluationContext.with(
                CELL,
                spreadsheetExpressionReferenceLoader,
                SERVER_URL,
                METADATA,
                SPREADSHEET_STORE_REPOSITORY,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Override
    public void testEvaluateExpressionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateFunctionWithNullFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateFunctionWithNullParametersFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testReferenceWithNullReferenceFails() {
        throw new UnsupportedOperationException();
    }

    // HasConverter.....................................................................................................

    @Test
    public void testConverter() {
        final BasicSpreadsheetExpressionEvaluationContext context = this.createContext();

        final ExpressionNumber from = context.expressionNumberKind().create(123);
        final String to = context.convertOrFail(from, String.class);

        this.checkEquals(
                to,
                context.converter().convertOrFail(from, String.class, context),
                () -> "converter with context and context convertOrFail should return the same"
        );
    }

    // DecimalNumberContextTesting.....................................................................................

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
        return "e";
    }

    @Override
    public char groupSeparator() {
        return DECIMAL_NUMBER_CONTEXT.groupSeparator();
    }

    @Override
    public MathContext mathContext() {
        return DECIMAL_NUMBER_CONTEXT.mathContext();
    }

    @Override
    public char negativeSign() {
        return DECIMAL_NUMBER_CONTEXT.negativeSign();
    }

    @Override
    public char percentageSymbol() {
        return DECIMAL_NUMBER_CONTEXT.percentageSymbol();
    }

    @Override
    public char positiveSign() {
        return DECIMAL_NUMBER_CONTEXT.positiveSign();
    }

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = METADATA.decimalNumberContext();

    @Override
    public void testExpressionFunctionWithNullFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testIsPureNullNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testLoadLabelWithNullLabelFails() {
        throw new UnsupportedOperationException();
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<BasicSpreadsheetExpressionEvaluationContext> type() {
        return BasicSpreadsheetExpressionEvaluationContext.class;
    }
}
