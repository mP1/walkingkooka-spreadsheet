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
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProvider;

import java.math.MathContext;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetExpressionEvaluationContextTest implements SpreadsheetExpressionEvaluationContextTesting<BasicSpreadsheetExpressionEvaluationContext>,
        SpreadsheetMetadataTesting {

    private final static SpreadsheetCellReference CELL_REFERENCE = SpreadsheetSelection.parseCell("B2");

    private final static Optional<SpreadsheetCell> CELL = Optional.of(
            CELL_REFERENCE.setFormula(SpreadsheetFormula.EMPTY.setText("=1+2"))
    );

    private final static SpreadsheetCellStore CELL_STORE = SpreadsheetCellStores.fake();

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadataTesting.METADATA_EN_AU;

    private final static Function<ExpressionReference, Optional<Optional<Object>>> REFERENCES = (r) -> {
        throw new UnsupportedOperationException();
    };

    // with.............................................................................................................

    @Test
    public void testWithNullCellFails() {
        this.withFails(
                null,
                CELL_STORE,
                SERVER_URL,
                REFERENCES,
                METADATA,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullCellStoreFails() {
        this.withFails(
                CELL,
                null,
                SERVER_URL,
                REFERENCES,
                METADATA,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullServerUrlFails() {
        this.withFails(
                CELL,
                CELL_STORE,
                null,
                REFERENCES,
                METADATA,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullReferencesFails() {
        this.withFails(
                CELL,
                CELL_STORE,
                SERVER_URL,
                null,
                METADATA,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullMetadataFails() {
        this.withFails(
                CELL,
                CELL_STORE,
                SERVER_URL,
                REFERENCES,
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
                CELL_STORE,
                SERVER_URL,
                REFERENCES,
                METADATA,
                null,
                EXPRESSION_FUNCTION_PROVIDER,
                PROVIDER_CONTEXT

        );
    }

    @Test
    public void testWithNullExpressionFunctionProviderFails() {
        this.withFails(
                CELL,
                CELL_STORE,
                SERVER_URL,
                REFERENCES,
                METADATA,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                null,
                PROVIDER_CONTEXT
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        this.withFails(
                CELL,
                CELL_STORE,
                SERVER_URL,
                REFERENCES,
                METADATA,
                SPREADSHEET_FORMULA_CONVERTER_CONTEXT,
                EXPRESSION_FUNCTION_PROVIDER,
                null
        );
    }

    private void withFails(final Optional<SpreadsheetCell> cell,
                           final SpreadsheetCellStore cellStore,
                           final AbsoluteUrl serverUrl,
                           final Function<ExpressionReference, Optional<Optional<Object>>> references,
                           final SpreadsheetMetadata spreadsheetMetadata,
                           final SpreadsheetConverterContext spreadsheetConverterContext,
                           final ExpressionFunctionProvider expressionFunctionProvider,
                           final ProviderContext providerContext) {
        assertThrows(
                NullPointerException.class,
                () -> BasicSpreadsheetExpressionEvaluationContext.with(
                        cell,
                        cellStore,
                        serverUrl,
                        references,
                        spreadsheetMetadata,
                        spreadsheetConverterContext,
                        expressionFunctionProvider,
                        providerContext
                )
        );
    }

    // loadCell.........................................................................................................

    @Test
    public void testLoadCellCurrentCell() {
        this.loadCellAndCheck(
                this.createContext(SpreadsheetCellStores.fake()),
                CELL_REFERENCE,
                CELL
        );
    }

    @Test
    public void testLoadCell() {
        final SpreadsheetCellStore cellStore = SpreadsheetCellStores.treeMap();

        final SpreadsheetCellReference cellReference = SpreadsheetSelection.parseCell("Z999");
        final SpreadsheetCell cell = cellReference.setFormula(
                SpreadsheetFormula.EMPTY.setText("'9999")
        );
        cellStore.save(cell);

        this.loadCellAndCheck(
                this.createContext(cellStore),
                cellReference,
                Optional.of(cell)
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
                "Invalid character '=' at (1,1) expected BINARY_EXPRESSION | LAMBDA_FUNCTION | NAMED_FUNCTION | LABEL | CELL_RANGE | CELL | GROUP | NEGATIVE | \"#.#E+#;#.#%;#.#;#%;#\" | TEXT | \"#NULL!\" | \"#DIV/0!\" | \"#VALUE!\" | \"#REF!\" | \"#NAME?\" | \"#NAME?\" | \"#NUM!\" | \"#N/A\" | \"#ERROR\" | \"#SPILL!\" | \"#CALC!\""
        );
    }

    // ExpressionEvaluationContextTesting................................................................................

    @Override
    public BasicSpreadsheetExpressionEvaluationContext createContext() {
        return this.createContext(CELL_STORE);
    }

    private BasicSpreadsheetExpressionEvaluationContext createContext(final SpreadsheetCellStore cellStore) {
        return BasicSpreadsheetExpressionEvaluationContext.with(
                CELL,
                cellStore,
                SERVER_URL,
                REFERENCES,
                METADATA,
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
    public void testEvaluateFunctionNullFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateFunctionNullParametersFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testReferenceNullReferenceFails() {
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

    // ClassTesting......................................................................................................

    @Override
    public Class<BasicSpreadsheetExpressionEvaluationContext> type() {
        return BasicSpreadsheetExpressionEvaluationContext.class;
    }
}
