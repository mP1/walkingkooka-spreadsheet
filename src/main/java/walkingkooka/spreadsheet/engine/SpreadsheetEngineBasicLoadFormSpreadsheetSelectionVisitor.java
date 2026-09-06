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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.validation.form.FormField;

final class SpreadsheetEngineBasicLoadFormSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static void acceptFormField(final FormField<SpreadsheetValidationReference> field,
                                final SpreadsheetEngineBasicChanges changes) {
        final SpreadsheetEngineBasicLoadFormSpreadsheetSelectionVisitor visitor = new SpreadsheetEngineBasicLoadFormSpreadsheetSelectionVisitor(changes);
        visitor.accept(
            field.reference()
                .toSpreadsheetSelection()
        );
    }


    SpreadsheetEngineBasicLoadFormSpreadsheetSelectionVisitor(final SpreadsheetEngineBasicChanges changes) {
        super();
        this.changes = changes;
    }

    @Override
    protected void visit(final SpreadsheetCellReference cell) {
        this.changes.getOrCreateCellCache(
            cell,
            SpreadsheetEngineBasicChangesCacheStatusCell.UNLOADED
        );
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference cellRange) {
        throw new IllegalArgumentException("FormField reference should not be a " + cellRange.textLabel() + " " + cellRange);
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.changes.getOrCreateLabelCache(
            label,
            SpreadsheetEngineBasicChangesCacheStatusLabel.UNLOADED
        );
    }

    private final SpreadsheetEngineBasicChanges changes;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.changes.toString();
    }
}
