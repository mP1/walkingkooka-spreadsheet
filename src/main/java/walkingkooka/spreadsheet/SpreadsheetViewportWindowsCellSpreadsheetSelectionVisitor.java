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

package walkingkooka.spreadsheet;

import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;

final class SpreadsheetViewportWindowsCellSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    SpreadsheetViewportWindowsCellSpreadsheetSelectionVisitor(final SpreadsheetViewportWindows windows) {
        this.windows = windows;
    }

//    @Override
//    protected void visit(final SpreadsheetCellRange range) {
//        super.visit( final range);
//    }
//
//    @Override
//    protected void visit(final SpreadsheetCellReference cell) {
//        this.visit(cell);
//    }
//
//    @Override
//    protected void visit(final SpreadsheetColumnReference reference) {
//        super.visit( final reference);
//    }
//
//    @Override
//    protected void visit(final SpreadsheetColumnReferenceRange range) {
//        super.visit( final range);
//    }
//
//    @Override
//    protected void visit(final SpreadsheetLabelName label) {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    protected void visit(final SpreadsheetRowReference row) {
//        this.visit(row.toRowRange());
//    }
//
//    @Override
//    protected void visit(final SpreadsheetRowReferenceRange rows) {
//        this.visit(rows.);
//    }

    /**
     * The {@link SpreadsheetViewportWindows} that will provide the bounds for the final {@link #cellRange}.
     */
    private final SpreadsheetViewportWindows windows;

    /**
     * The minimum {@link SpreadsheetCellRange} which will give all {@link SpreadsheetCellReference} within the
     * {@link #windows}.
     */
    private SpreadsheetCellRange cellRange;
}
