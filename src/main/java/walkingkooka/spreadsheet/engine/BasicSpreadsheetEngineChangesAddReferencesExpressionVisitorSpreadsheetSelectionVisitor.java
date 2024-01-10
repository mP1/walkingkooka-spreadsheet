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

import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;
import walkingkooka.spreadsheet.store.TargetAndSpreadsheetCellReference;

/**
 * A {@link SpreadsheetSelectionVisitor} that adds references to each reference present within cell formula.
 */
final class BasicSpreadsheetEngineChangesAddReferencesExpressionVisitorSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static BasicSpreadsheetEngineChangesAddReferencesExpressionVisitorSpreadsheetSelectionVisitor with(final SpreadsheetCellReference target,
                                                                                                       final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineChangesAddReferencesExpressionVisitorSpreadsheetSelectionVisitor(target, context);
    }

    // VisibleForTesting
    BasicSpreadsheetEngineChangesAddReferencesExpressionVisitorSpreadsheetSelectionVisitor(final SpreadsheetCellReference target,
                                                                                           final SpreadsheetEngineContext context) {
        super();
        this.target = target;
        this.context = context;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.context.storeRepository()
                .cellReferences()
                .addReference(TargetAndSpreadsheetCellReference.with(this.target, reference));
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.context.storeRepository()
                .labelReferences()
                .addReference(TargetAndSpreadsheetCellReference.with(label, this.target));
    }

    @Override
    protected void visit(final SpreadsheetCellRange range) {
        this.context.storeRepository()
                .rangeToCells()
                .addValue(range, this.target);
    }

    /**
     * The target cell.
     */
    private final SpreadsheetCellReference target;

    /**
     * Used to get stores.
     */
    private final SpreadsheetEngineContext context;

    @Override
    public String toString() {
        return this.target.toString();
    }
}
