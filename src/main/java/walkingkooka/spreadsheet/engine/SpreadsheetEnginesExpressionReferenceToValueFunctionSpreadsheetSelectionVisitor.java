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
import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
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
final class SpreadsheetEnginesExpressionReferenceToValueFunctionSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static Optional<Optional<Object>> values(final SpreadsheetExpressionReference reference,
                                             final SpreadsheetEngine engine,
                                             final SpreadsheetEngineContext context) {
        final SpreadsheetEnginesExpressionReferenceToValueFunctionSpreadsheetSelectionVisitor visitor =
                new SpreadsheetEnginesExpressionReferenceToValueFunctionSpreadsheetSelectionVisitor(engine, context);
        visitor.accept(reference);
        return Optional.ofNullable(visitor.value);
    }

    /**
     * The loadCell and loadCells only want the values, not interested in all the other stuff like deleted or columns/rows etc.
     */
    private final static Set<SpreadsheetDeltaProperties> SPREADSHEET_DELTA_PROPERTIES = EnumSet.of(SpreadsheetDeltaProperties.CELLS);

    // @VisibleForTesting
    SpreadsheetEnginesExpressionReferenceToValueFunctionSpreadsheetSelectionVisitor(final SpreadsheetEngine engine,
                                                                                    final SpreadsheetEngineContext context) {
        super();
        this.engine = engine;
        this.context = context;
    }

    // a cell always returns an Optional of a scalar value
    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        final SpreadsheetDelta loaded = this.loadCells(reference);

        this.value = loaded.cell(reference)
                .map(c -> c.formula()
                        .value()
                ).orElse(null);
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        // load the cell or cells pointed to by the label
        this.accept(
                this.context.storeRepository()
                        .labels()
                        .loadOrFail(label).target()
        );
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference range) {
        final SpreadsheetDelta delta = loadCells(range);

        this.value = Optional.of(
                Lists.immutable(
                        range.cellStream()
                                .map(c -> this.extractValueOrNull(c, delta))
                                .collect(Collectors.toList())
                )
        );
    }

    private SpreadsheetDelta loadCells(final SpreadsheetSelection range) {
        return this.engine.loadCells(
                range,
                SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                SPREADSHEET_DELTA_PROPERTIES,
                this.context
        );
    }

    private final SpreadsheetEngine engine;
    private final SpreadsheetEngineContext context;

    private Object extractValueOrNull(final SpreadsheetCellReference reference,
                                      final SpreadsheetDelta delta) {
        return delta.cell(reference)
                .map(c -> c.formula()
                        .value()
                        .orElseGet(
                                () -> SpreadsheetError.selectionNotFound(reference)
                        )
                ).orElseGet(
                        () -> SpreadsheetError.selectionNotFound(reference)
                );
    }

    private Optional<Object> value;

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
