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

import walkingkooka.test.Fake;
import walkingkooka.visit.Visiting;

public class FakeSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor implements Fake {

    public FakeSpreadsheetSelectionVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetSelection selection) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final SpreadsheetSelection selection) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference cellRange) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetCellReference cell) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetColumnReference column) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetColumnRangeReference columnRange) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetRowReference row) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void visit(final SpreadsheetRowRangeReference rowRange) {
        throw new UnsupportedOperationException();
    }
}
