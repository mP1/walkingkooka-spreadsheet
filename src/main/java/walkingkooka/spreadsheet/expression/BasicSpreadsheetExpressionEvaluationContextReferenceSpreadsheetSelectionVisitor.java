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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.map.Maps;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceLoader;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A {@link BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor} that fails by throwing a {@link walkingkooka.spreadsheet.SpreadsheetErrorException}
 * if a {@link SpreadsheetExpressionReference} does not exist.
 */
final class BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static Optional<Optional<Object>> values(final SpreadsheetExpressionReference reference,
                                             final SpreadsheetExpressionReferenceLoader loader,
                                             final SpreadsheetExpressionEvaluationContext context) {
        final BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor visitor =
            new BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor(
                loader,
                context
            );
        visitor.accept(reference);
        return visitor.value;
    }

    // @VisibleForTesting
    BasicSpreadsheetExpressionEvaluationContextReferenceSpreadsheetSelectionVisitor(final SpreadsheetExpressionReferenceLoader loader,
                                                                                    final SpreadsheetExpressionEvaluationContext context) {
        super();
        this.loader = loader;
        this.context = context;
    }

    // a missing CELL is given a value of Optional#empty
    @Override
    protected void visit(final SpreadsheetCellReference cell) {
        this.value = this.loader.loadCell(
            cell,
            this.context
        ).map(c -> Optional.of(
                c.formula()
                    .errorOrValue()
            )
        ).orElse(SpreadsheetExpressionEvaluationContext.REFERENCE_NULL_VALUE);
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        // load the cell or cells pointed to by the label
        this.accept(
            this.loader.loadLabelOrFail(label)
                .reference()
        );
    }

    /**
     * An absent cell-range will return an {@link Optional} wrapping an empty list.
     */
    @Override
    protected void visit(final SpreadsheetCellRangeReference range) {
        final Map<SpreadsheetCellReference, Object> cellToValue = Maps.sorted();

        for (final SpreadsheetCell cell : this.loader.loadCellRange(
            range,
            this.context
        )) {
            cellToValue.put(
                cell.reference(),
                cell.formula()
                    .errorOrValue()
                    .orElse(null)
            );
        }

        // create a list with entries for each cell in the given range. missing values will have an Optional#empty.
        final List<Object> value = Lists.array();
        for (final SpreadsheetCellReference cell : range) {
            value.add(
                //Optional.ofNullable(
                cellToValue.get(cell)
                //)
            );
        }

        this.value = Optional.of(
            Optional.of(value)
        );
    }

    private final SpreadsheetExpressionReferenceLoader loader;

    private final SpreadsheetExpressionEvaluationContext context;

    /**
     * The value for a cell or cell-range and should never be null as a response.
     */
    private Optional<Optional<Object>> value;

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }
}
