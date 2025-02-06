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

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Wraps another {@link SpreadsheetExpressionEvaluationContext}, but holds a local {@link SpreadsheetCell} property.
 */
final class CellSpreadsheetExpressionEvaluationContext implements SpreadsheetExpressionEvaluationContextDelegator {

    static SpreadsheetExpressionEvaluationContext with(final Optional<SpreadsheetCell> cell,
                                                           final SpreadsheetExpressionEvaluationContext context) {
        Objects.requireNonNull(cell, "cell");
        Objects.requireNonNull(context, "context");

        return cell.equals(context.cell()) ?
                context :
                new CellSpreadsheetExpressionEvaluationContext(
                        cell,
                        context
                );
    }

    private CellSpreadsheetExpressionEvaluationContext(final Optional<SpreadsheetCell> cell,
                                                       final SpreadsheetExpressionEvaluationContext context) {
        this.cell = cell;
        this.context = context;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext setCell(final Optional<SpreadsheetCell> cell) {
        return with(
                cell,
                this.context
        );
    }

    @Override
    public Optional<SpreadsheetCell> cell() {
        return this.cell;
    }

    private final Optional<SpreadsheetCell> cell;

    @Override
    public Optional<SpreadsheetCell> loadCell(final SpreadsheetCellReference cell) {
        this.loadCellCycleCheck(cell);

        return this.context.loadCell(cell);
    }

    @Override
    public Set<SpreadsheetCell> loadCells(final SpreadsheetCellRangeReference range) {
        this.loadCellsCycleCheck(range);

        return this.context.loadCells(range);
    }

    @Override
    public SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName) {
        return this.context.resolveLabel(labelName);
    }

    @Override
    public Optional<Optional<Object>> reference(final ExpressionReference reference) {
        return this.context.reference(reference);
    }

    // SpreadsheetExpressionEvaluationContextDelegator..................................................................

    /**
     * All other {@link SpreadsheetExpressionEvaluationContext} methods are delegated to the wrapped.
     */
    @Override
    public SpreadsheetExpressionEvaluationContext spreadsheetExpressionEvaluationContext() {
        return this.context;
    }

    private final SpreadsheetExpressionEvaluationContext context;

    @Override
    public String toString() {
        return this.cell.toString();
    }
}
