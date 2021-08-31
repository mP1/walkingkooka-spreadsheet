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

package walkingkooka.spreadsheet.reference.store;

import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Objects;
import java.util.Optional;

/**
 * Accepts a {@link SpreadsheetLabelName} and returns a {@link SpreadsheetCellReference} or {@link Optional#empty()},
 * using a {@link SpreadsheetLabelStore} to resolve labels.
 */
final class SpreadsheetLabelStoreCellReferenceSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    private final SpreadsheetLabelStore store;
    private SpreadsheetCellReference reference = null;

    static Optional<SpreadsheetCellReference> reference(final ExpressionReference reference,
                                                        final SpreadsheetLabelStore store) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(store, "store");

        final SpreadsheetLabelStoreCellReferenceSpreadsheetExpressionReferenceVisitor visitor = new SpreadsheetLabelStoreCellReferenceSpreadsheetExpressionReferenceVisitor(store);
        visitor.accept(reference);
        return Optional.ofNullable(
                visitor.reference
        );
    }

    SpreadsheetLabelStoreCellReferenceSpreadsheetExpressionReferenceVisitor(final SpreadsheetLabelStore store) {
        super();
        this.store = store;
    }

    protected void visit(final SpreadsheetCellReference reference) {
        this.reference = reference;
    }

    protected void visit(final SpreadsheetLabelName label) {
        final Optional<SpreadsheetLabelMapping> mapping = this.store.load(label);
        if (mapping.isPresent()) {
            this.accept(mapping.get().reference());
        }
    }

    protected void visit(final SpreadsheetCellRange range) {
        this.reference = range.begin();
    }

    public String toString() {
        return String.valueOf(this.reference);
    }
}
