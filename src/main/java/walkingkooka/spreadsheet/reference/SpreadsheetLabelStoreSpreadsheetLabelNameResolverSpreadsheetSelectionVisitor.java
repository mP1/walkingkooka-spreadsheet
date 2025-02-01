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

package walkingkooka.spreadsheet.reference;

import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;

import java.util.Objects;

/**
 * A {@link SpreadsheetSelectionVisitor} that handles all selections with special logic to resolve {@link SpreadsheetLabelName}
 * to a non {@link SpreadsheetLabelName} result.
 */
final class SpreadsheetLabelStoreSpreadsheetLabelNameResolverSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static SpreadsheetSelection resolveLabel(final SpreadsheetLabelName labelName,
                                             final SpreadsheetLabelStore store) {
        Objects.requireNonNull(labelName, "labelName");

        final SpreadsheetLabelStoreSpreadsheetLabelNameResolverSpreadsheetSelectionVisitor visitor = new SpreadsheetLabelStoreSpreadsheetLabelNameResolverSpreadsheetSelectionVisitor(store);
        visitor.accept(labelName);
        return visitor.nonLabel;
    }

    // @VisibleForTesting
    SpreadsheetLabelStoreSpreadsheetLabelNameResolverSpreadsheetSelectionVisitor(final SpreadsheetLabelStore store) {
        super();
        this.store = store;
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference range) {
        this.nonLabel = range;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.nonLabel = reference;
    }

    @Override
    protected void visit(final SpreadsheetColumnReference reference) {
        this.nonLabel = reference;
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference range) {
        this.nonLabel = range;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.accept(
                this.store.load(label)
                        .orElseThrow(() -> new IllegalArgumentException("Label not found: " + label))
                        .target()
        );
    }

    @Override
    protected void visit(final SpreadsheetRowReference reference) {
        this.nonLabel = reference;
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference range) {
        this.nonLabel = range;
    }

    private final SpreadsheetLabelStore store;

    /**
     * The resolved {@link SpreadsheetSelection}, basically any sub-class except for {@link SpreadsheetLabelName}.
     */
    private SpreadsheetSelection nonLabel;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
