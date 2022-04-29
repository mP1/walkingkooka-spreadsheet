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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A visitor which resolves any {@link ExpressionReference} down to values. A range may match many cells, resulting in
 * a {@link List} while a single cell might return a value.
 */
final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static Optional<Object> values(final ExpressionReference reference,
                                   final SpreadsheetEngine engine,
                                   final SpreadsheetEngineContext context) {
        final SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor visitor =
                new SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor(engine, context);
        visitor.accept(reference);
        return Optional.ofNullable(visitor.value);
    }

    // @VisibleForTesting
    SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor(final SpreadsheetEngine engine,
                                                                                                                           final SpreadsheetEngineContext context) {
        super();
        this.engine = engine;
        this.context = context;
    }

    // a cell always returns an Optional of a scalar value
    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        final List<Object> values = this.extractCells(
                this.engine.loadCell(
                        reference,
                        SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                        context
                ),
                reference
        );

        switch (values.size()) {
            case 0:
                this.value = null;
                break;
            case 1:
                this.value = values.get(0);
                break;
            default:
                throw new UnsupportedOperationException();
        }
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

    // a range always returns a Optional<List>
    @Override
    protected void visit(final SpreadsheetCellRange range) {
        this.value =
                Lists.immutable(
                        this.extractCells(
                                this.engine.loadCells(
                                        Sets.of(range),
                                        SpreadsheetEngineEvaluation.COMPUTE_IF_NECESSARY,
                                        context
                                ),
                                range
                        )
                );
    }

    private final SpreadsheetEngine engine;
    private final SpreadsheetEngineContext context;

    private List<Object> extractCells(final SpreadsheetDelta delta,
                                      final SpreadsheetSelection selection) {
        return delta.cells()
                .stream()
                .filter(c -> selection.test(c.reference()))
                .flatMap(c -> extractValue(c))
                .collect(Collectors.toList());
    }

    // J2cl Optional does not emulate Optional.stream().
    private Stream<Object> extractValue(final SpreadsheetCell cell) {
        return cell.formula()
                .value()
                .map(v -> Arrays.stream(new Object[]{v}))
                .orElse(Arrays.stream(new Object[0]));
    }

    private Object value;

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
