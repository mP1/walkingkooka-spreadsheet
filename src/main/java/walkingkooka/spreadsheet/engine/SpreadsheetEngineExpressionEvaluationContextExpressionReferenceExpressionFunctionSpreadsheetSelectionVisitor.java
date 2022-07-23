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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A visitor which resolves any {@link ExpressionReference} down to values. A range may match many cells, resulting in
 * a {@link List} while a single cell might return a value.
 */
final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static Optional<Object> values(final SpreadsheetExpressionReference reference,
                                   final SpreadsheetEngine engine,
                                   final SpreadsheetEngineContext context) {
        final SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetSelectionVisitor visitor =
                new SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetSelectionVisitor(engine, context);
        visitor.accept(reference);
        return Optional.ofNullable(visitor.value);
    }

    /**
     * The loadCell and loadCells only want the values, not interested in all the other stuff like deleted or columns/rows etc.
     */
    private final static Set<SpreadsheetDeltaProperties> SPREADSHEET_DELTA_PROPERTIES = EnumSet.of(SpreadsheetDeltaProperties.CELLS);

    // @VisibleForTesting
    SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetSelectionVisitor(final SpreadsheetEngine engine,
                                                                                                                 final SpreadsheetEngineContext context) {
        super();
        this.engine = engine;
        this.context = context;
    }

    // a cell always returns an Optional of a scalar value
    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        final SpreadsheetDelta loaded = this.engine.loadCell(
                reference,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SPREADSHEET_DELTA_PROPERTIES,
                this.context
        );

        this.value = extractValueOrNull(
                reference,
                loaded
        );
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        // load the cell or cells pointed to by the label
        this.accept(
                this.context.storeRepository()
                        .labels()
                        .loadOrFail(label).reference()
        );
    }

    @Override
    protected void visit(final SpreadsheetCellRange range) {
        final SpreadsheetDelta delta = this.engine.loadCells(
                Sets.of(range),
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SPREADSHEET_DELTA_PROPERTIES,
                this.context
        );

        this.value = Lists.immutable(
                range.cellStream()
                        .map(c -> this.extractValueOrNull(c, delta))
                        .collect(Collectors.toList())
        );
    }

    private final SpreadsheetEngine engine;
    private final SpreadsheetEngineContext context;

    private Object extractValueOrNull(final SpreadsheetCellReference reference,
                                      final SpreadsheetDelta delta) {
        return delta.cell(reference)
                .map(this::extractValueOrNull0)
                .orElse(null);
    }

    private Object extractValueOrNull0(final SpreadsheetCell cell) {
        return cell.formula()
                .value()
                .orElse(null);
    }

    private Object value;

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
