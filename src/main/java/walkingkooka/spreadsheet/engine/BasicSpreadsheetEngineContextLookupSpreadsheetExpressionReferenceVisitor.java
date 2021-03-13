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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.SpreadsheetViewport;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that resolves an {@link SpreadsheetExpressionReference} to a {@link SpreadsheetCellReference}.
 */
final class BasicSpreadsheetEngineContextLookupSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static SpreadsheetCellReference lookup(final SpreadsheetExpressionReference reference,
                                           final SpreadsheetLabelStore store) {
        final BasicSpreadsheetEngineContextLookupSpreadsheetExpressionReferenceVisitor visitor = new BasicSpreadsheetEngineContextLookupSpreadsheetExpressionReferenceVisitor(store);
        visitor.accept(reference);
        return visitor.result;
    }

    // @VisibleForTesting
    BasicSpreadsheetEngineContextLookupSpreadsheetExpressionReferenceVisitor(final SpreadsheetLabelStore store) {
        super();
        this.store = store;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.result = reference;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        final SpreadsheetLabelMapping mapping = this.store.load(label)
                .orElseThrow(() -> new IllegalArgumentException("Unknown label " + label));
        this.accept(mapping.reference());
    }

    private final SpreadsheetLabelStore store;

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.result = range.begin();
    }

    @Override
    protected void visit(final SpreadsheetViewport viewport) {
        throw new IllegalArgumentException("Expected cell, label or range not viewport");
    }

    private SpreadsheetCellReference result;

    @Override
    public String toString() {
        return this.store.toString();
    }
}
