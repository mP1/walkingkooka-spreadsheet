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
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;

import java.util.Objects;
import java.util.Optional;

/**
 * Accepts a {@link SpreadsheetLabelName} and returns a {@link SpreadsheetCellReferenceOrRange} or {@link Optional#empty()},
 * using a {@link SpreadsheetLabelStore} to resolve labels.
 */
final class SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static Optional<SpreadsheetCellReferenceOrRange> cellReferenceOrRange(final SpreadsheetExpressionReference reference,
                                                                          final SpreadsheetLabelStore store) {
        Objects.requireNonNull(reference, "reference");
        Objects.requireNonNull(store, "store");

        final SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor visitor = new SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor(store);
        visitor.accept(reference);
        return Optional.ofNullable(
                visitor.cellReferenceOrRange
        );
    }

    SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor(final SpreadsheetLabelStore store) {
        super();
        this.store = store;
    }

    protected void visit(final SpreadsheetCellReference reference) {
        this.cellReferenceOrRange = reference;
    }

    protected void visit(final SpreadsheetLabelName label) {
        final Optional<SpreadsheetLabelMapping> mapping = this.store.load(label);
        if (mapping.isPresent()) {
            this.accept(mapping.get().target());
        }
    }

    protected void visit(final SpreadsheetCellRange range) {
        this.cellReferenceOrRange = range;
    }

    public String toString() {
        return String.valueOf(this.cellReferenceOrRange);
    }

    private final SpreadsheetLabelStore store;

    private SpreadsheetCellReferenceOrRange cellReferenceOrRange = null;
}
