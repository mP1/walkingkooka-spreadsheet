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

import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStore;

import java.util.Objects;

/**
 * A {@link SpreadsheetSelectionVisitor} that resolves an {@link SpreadsheetSelection} to a {@link SpreadsheetSelection}
 * if it is a label..
 */
final class BasicSpreadsheetEngineContextResolveLabelSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static SpreadsheetSelection resolveLabel(final SpreadsheetSelection selection,
                                             final SpreadsheetLabelStore store) {
        Objects.requireNonNull(selection, "selection");

        final BasicSpreadsheetEngineContextResolveLabelSpreadsheetSelectionVisitor visitor = new BasicSpreadsheetEngineContextResolveLabelSpreadsheetSelectionVisitor(store);
        visitor.accept(selection);
        return visitor.result;
    }

    // @VisibleForTesting
    BasicSpreadsheetEngineContextResolveLabelSpreadsheetSelectionVisitor(final SpreadsheetLabelStore store) {
        super();
        this.store = store;
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference range) {
        this.result = range;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.result = reference;
    }

    @Override
    protected void visit(final SpreadsheetColumnReference reference) {
        this.result = reference;
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference range) {
        this.result = range;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.accept(
                this.store.loadOrFail(label)
                        .target()
        );
    }

    @Override
    protected void visit(final SpreadsheetRowReference reference) {
        this.result = reference;
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference range) {
        this.result = range;
    }

    private final SpreadsheetLabelStore store;

    /**
     * The resolved {@link SpreadsheetSelection}, basically any sub class except for {@link SpreadsheetLabelName}.
     */
    private SpreadsheetSelection result;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
