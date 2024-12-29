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

import walkingkooka.visit.Visiting;
import walkingkooka.visit.Visitor;

import java.util.Objects;

/**
 * A {@link Visitor} for all known implementations of {@link SpreadsheetSelection}.
 */
public abstract class SpreadsheetSelectionVisitor extends Visitor<SpreadsheetSelection> {

    protected SpreadsheetSelectionVisitor() {
        super();
    }

    @Override
    public final void accept(final SpreadsheetSelection selection) {
        Objects.requireNonNull(selection, "selection");

        if (Visiting.CONTINUE == this.startVisit(selection)) {
            selection.accept(this);
        }
        this.endVisit(selection);
    }

    protected Visiting startVisit(final SpreadsheetSelection reference) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetSelection reference) {
        // nop
    }

    protected void visit(final SpreadsheetCellRangeReference range) {
        // nop
    }

    protected void visit(final SpreadsheetCellReference reference) {
        // nop
    }

    protected void visit(final SpreadsheetColumnReference reference) {
        // nop
    }

    protected void visit(final SpreadsheetColumnRangeReference range) {
        // nop
    }

    protected void visit(final SpreadsheetLabelName label) {
        // nop
    }

    protected void visit(final SpreadsheetRowReference reference) {
        // nop
    }

    protected void visit(final SpreadsheetRowRangeReference range) {
        // nop
    }
}
