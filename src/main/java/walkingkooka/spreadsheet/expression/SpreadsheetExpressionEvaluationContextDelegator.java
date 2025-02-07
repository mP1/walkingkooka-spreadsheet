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

import walkingkooka.convert.Converter;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionEvaluationContextDelegator;

import java.util.Optional;

/**
 * Delegates all {@link ExpressionEvaluationContextDelegator} and most {@link SpreadsheetExpressionEvaluationContext}
 * except for cell getter and loaders:
 * <ul>
 *     <li>{@link #cell()}</li>
 *     <li>{@link #loadCell(SpreadsheetCellReference)}</li>
 *     <li>{@link #loadCellRange(SpreadsheetCellRangeReference)}</li>
 * </ul>
 */
public interface SpreadsheetExpressionEvaluationContextDelegator extends SpreadsheetExpressionEvaluationContext,
        ExpressionEvaluationContextDelegator {

    @Override
    default ExpressionEvaluationContext expressionEvaluationContext() {
        return this.spreadsheetExpressionEvaluationContext();
    }

    // SpreadsheetExpressionEvaluationContext...........................................................................

    @Override
    default Converter<SpreadsheetConverterContext> converter() {
        return this.spreadsheetExpressionEvaluationContext().converter();
    }

    @Override
    default boolean isText(final Object value) {
        return this.spreadsheetExpressionEvaluationContext()
                .isText(value);
    }

    @Override
    default Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName labelName) {
        return this.spreadsheetExpressionEvaluationContext()
                .loadLabel(labelName);
    }

    @Override
    default SpreadsheetFormulaParserToken parseFormula(final TextCursor formula) {
        return this.spreadsheetExpressionEvaluationContext()
                .parseFormula(formula);
    }

    @Override
    default AbsoluteUrl serverUrl() {
        return this.spreadsheetExpressionEvaluationContext()
                .serverUrl();
    }

    @Override
    default SpreadsheetMetadata spreadsheetMetadata() {
        return this.spreadsheetExpressionEvaluationContext()
                .spreadsheetMetadata();
    }

    SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext();
}
