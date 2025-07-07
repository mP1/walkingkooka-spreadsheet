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
import walkingkooka.spreadsheet.store.ReferenceAndSpreadsheetCellReference;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

/**
 * A {@link SpreadsheetSelectionVisitor} that adds references to each reference present within cell formula.
 */
final class BasicSpreadsheetEngineChangesAddFormulaReferenceSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static BasicSpreadsheetEngineChangesAddFormulaReferenceSpreadsheetSelectionVisitor with(final SpreadsheetCellReference cell,
                                                                                            final SpreadsheetStoreRepository repository) {
        return new BasicSpreadsheetEngineChangesAddFormulaReferenceSpreadsheetSelectionVisitor(
            cell,
            repository
        );
    }

    // VisibleForTesting
    BasicSpreadsheetEngineChangesAddFormulaReferenceSpreadsheetSelectionVisitor(final SpreadsheetCellReference cell,
                                                                                final SpreadsheetStoreRepository repository) {
        super();
        this.cell = cell;
        this.repository = repository;
    }

    @Override
    protected void visit(final SpreadsheetCellReference formulaCell) {
        this.repository.cellReferences()
            .addCell(
                ReferenceAndSpreadsheetCellReference.with(
                    formulaCell,
                    this.cell
                )
            );
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.repository.labelReferences()
            .addCell(
                ReferenceAndSpreadsheetCellReference.with(
                    label,
                    this.cell
                )
            );
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference cellRange) {
        this.repository.rangeToCells()
            .addValue(
                cellRange,
                this.cell
            );
    }

    /**
     * The target cell.
     */
    private final SpreadsheetCellReference cell;

    /**
     * Used to get stores.
     */
    private final SpreadsheetStoreRepository repository;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.cell.toString();
    }
}
