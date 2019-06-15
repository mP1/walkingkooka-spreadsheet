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

package walkingkooka.spreadsheet;

import walkingkooka.spreadsheet.parser.SpreadsheetCellReference;
import walkingkooka.spreadsheet.parser.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that resolves labels and ranges to a {@link SpreadsheetCellReference}
 */
final class ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static Optional<SpreadsheetCellReference> toSpreadsheetCellReference(final ExpressionReference reference,
                                                                         final ExpressionReferenceSpreadsheetCellReferenceFunction function) {
        final ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor visitor = new ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor(function);
        visitor.accept(reference);
        return Optional.ofNullable(visitor.reference);
    }

    // @VisibleForTesting
    ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor(final ExpressionReferenceSpreadsheetCellReferenceFunction function) {
        super();
        this.function = function;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.reference = reference;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.function.labelStore.load(label).ifPresent(m -> this.accept(m.reference()));
    }

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.function.rangeToCellStore.load(range).ifPresent(cells -> this.accept(cells.get(0)));
    }

    private final ExpressionReferenceSpreadsheetCellReferenceFunction function;
    SpreadsheetCellReference reference;

    @Override
    public String toString() {
        return this.function + " " + this.reference;
    }
}
