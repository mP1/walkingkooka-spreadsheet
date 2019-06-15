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

import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.store.reference.TargetAndSpreadsheetCellReference;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that adds references to each reference present within cell formula.
 */
final class BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor with(final SpreadsheetCellReference target,
                                                                                                                         final BasicSpreadsheetEngine engine) {
        return new BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor(target, engine);
    }

    // VisibleForTesting
    BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor(final SpreadsheetCellReference target,
                                                                                                             final BasicSpreadsheetEngine engine) {
        super();
        this.target = target;
        this.engine = engine;
    }

    @Override
    final protected void visit(final SpreadsheetCellReference reference) {
        this.engine.cellReferencesStore.addReference(TargetAndSpreadsheetCellReference.with(this.target, reference));
    }

    @Override
    final protected void visit(final SpreadsheetLabelName label) {
        this.engine.labelReferencesStore.addReference(TargetAndSpreadsheetCellReference.with(label, this.target));
    }

    @Override
    final protected void visit(final SpreadsheetRange range) {
        this.engine.rangeToCellStore.addValue(range, this.target);
    }

    /**
     * The target cell.
     */
    private final SpreadsheetCellReference target;

    /**
     * The engine holds stores which will have references to this cell updated.
     */
    private final BasicSpreadsheetEngine engine;

    @Override
    public final String toString() {
        return this.target.toString();
    }
}