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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStore;
import walkingkooka.store.LoadStoreException;
import walkingkooka.tree.expression.ExpressionEvaluationException;
import walkingkooka.tree.expression.ExpressionReference;

/**
 * A visitor which resolves any {@link ExpressionReference} down to a {@link SpreadsheetCellReference}.
 */
final class SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static SpreadsheetCellReference reference(final ExpressionReference reference,
                                              final SpreadsheetLabelStore store) {
        final SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor visitor =
                new SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor(store);
        visitor.accept(reference);
        return visitor.reference;
    }

    // @VisibleForTesting
    SpreadsheetEngineExpressionEvaluationContextExpressionReferenceExpressionFunctionSpreadsheetExpressionReferenceVisitor(final SpreadsheetLabelStore store) {
        super();
        this.store = store;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.reference = reference;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        try {
            this.accept(this.store.loadOrFail(label).reference());
        } catch (final LoadStoreException cause) {
            throw new ExpressionEvaluationException("Unknown label: " + label);
        }
    }

    private final SpreadsheetLabelStore store;

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.reference = range.begin();
    }

    private SpreadsheetCellReference reference = null;

    @Override
    public String toString() {
        return String.valueOf(this.reference);
    }
}
