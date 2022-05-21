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
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContextTesting;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.store.SpreadsheetCellStore;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;
import walkingkooka.tree.expression.ExpressionNumberKind;

import java.math.MathContext;
import java.util.Locale;
import java.util.Optional;

public final class BasicSpreadsheetEngineContextSpreadsheetExpressionEvaluationContextTest implements SpreadsheetExpressionEvaluationContextTesting<BasicSpreadsheetEngineContextSpreadsheetExpressionEvaluationContext> {

    private final static SpreadsheetCellReference CELL_REFERENCE = SpreadsheetSelection.parseCell("B2");

    private final static Optional<SpreadsheetCell> CELL = Optional.of(
            CELL_REFERENCE.setFormula(SpreadsheetFormula.EMPTY.setText("=1+2"))
    );

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("http://example.com");

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

    // ExpressionEvaluationContextTesting................................................................................

    @Override
    public void testFunctionNullFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testFunctionUnknownFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateNullFunctionNameFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testEvaluateUnknownFunctionNameFails() {
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
        return DECIMAL_NUMBER_CONTEXT.exponentSymbol();
    }

    @Override
    public char groupingSeparator() {
        return DECIMAL_NUMBER_CONTEXT.groupingSeparator();
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

    private final static DecimalNumberContext DECIMAL_NUMBER_CONTEXT = DecimalNumberContexts.american(MathContext.DECIMAL32);

    // ClassTesting......................................................................................................

    @Override
    public Class<BasicSpreadsheetEngineContextSpreadsheetExpressionEvaluationContext> type() {
        return BasicSpreadsheetEngineContextSpreadsheetExpressionEvaluationContext.class;
    }

    @Override
    public BasicSpreadsheetEngineContextSpreadsheetExpressionEvaluationContext createContext() {
        return this.createContext(SpreadsheetCellStores.fake());
    }

    public BasicSpreadsheetEngineContextSpreadsheetExpressionEvaluationContext createContext(final SpreadsheetCellStore cellStore) {
        return BasicSpreadsheetEngineContextSpreadsheetExpressionEvaluationContext.with(
                CELL,
                cellStore,
                SERVER_URL,
                SpreadsheetMetadata.EMPTY.set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-US"))
                        .loadFromLocale()
                        .set(SpreadsheetMetadataPropertyName.PRECISION, DECIMAL_NUMBER_CONTEXT.mathContext().getPrecision())
                        .set(SpreadsheetMetadataPropertyName.ROUNDING_MODE, DECIMAL_NUMBER_CONTEXT.mathContext().getRoundingMode())
                        .set(SpreadsheetMetadataPropertyName.DATETIME_OFFSET, 0L)
                        .set(SpreadsheetMetadataPropertyName.DEFAULT_YEAR, 20)
                        .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, ExpressionNumberKind.DEFAULT)
                        .set(SpreadsheetMetadataPropertyName.TEXT_FORMAT_PATTERN, SpreadsheetPattern.parseTextFormatPattern("@"))
                        .set(SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR, 20),
                (n) -> {
                    throw new UnsupportedOperationException();
                },
                (r) -> {
                    throw new UnsupportedOperationException();
                }
        );
    }
}
