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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Cast;
import walkingkooka.spreadsheet.SpreadsheetValueTypeVisitor;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.function.Function;

/**
 * A {@link SpreadsheetValueTypeVisitor} which accepts the {@link String} and calls the right parse method on {@link SpreadsheetSelection}.
 */
final class SpreadsheetConverterTextToSpreadsheetSelectionSpreadsheetValueTypeVisitor extends SpreadsheetValueTypeVisitor {

    static <S extends SpreadsheetSelection> S parse(final String string,
                                                    final Class<S> selectionType,
                                                    final SpreadsheetConverterContext context) {
        final SpreadsheetConverterTextToSpreadsheetSelectionSpreadsheetValueTypeVisitor visitor = new SpreadsheetConverterTextToSpreadsheetSelectionSpreadsheetValueTypeVisitor(
                string,
                context
        );
        visitor.accept(selectionType);
        return Cast.to(visitor.selection);
    }

    SpreadsheetConverterTextToSpreadsheetSelectionSpreadsheetValueTypeVisitor(final String string,
                                                                              final SpreadsheetConverterContext context) {
        super();
        this.string = string;
        this.context = context;
    }

    @Override
    protected void visitCellRange() {
        this.parseLabelOr(
                SpreadsheetSelection::parseCellRange
        );
    }

    @Override
    protected void visitCellReference() {
        this.parseLabelOr(
                SpreadsheetSelection::parseCell
        );
    }

    @Override
    protected void visitCellReferenceOrRange() {
        this.parseLabelOr(
                SpreadsheetSelection::parseCellOrCellRange
        );
    }

    private void parseLabelOr(final Function<String, SpreadsheetSelection> parse) {
        final String string = this.string;

        this.selection = SpreadsheetSelection.isLabelText(string) ?
                this.context.resolveIfLabelOrFail(
                        SpreadsheetSelection.labelName(string)
                ) :
                parse.apply(string);
    }

    @Override
    protected void visitColumnReference() {
        this.selection = SpreadsheetSelection.parseColumn(this.string);
    }

    @Override
    protected void visitColumnRangeReference() {
        this.selection = SpreadsheetSelection.parseColumnRange(this.string);
    }

    @Override
    protected void visitLabel() {
        this.selection = SpreadsheetSelection.labelName(this.string);
    }

    @Override
    protected void visitRowReference() {
        this.selection = SpreadsheetSelection.parseRow(this.string);
    }

    @Override
    protected void visitRowRangeReference() {
        this.selection = SpreadsheetSelection.parseRowRange(this.string);
    }

    private final String string;

    // needed to resolve labels
    private final SpreadsheetConverterContext context;

    private SpreadsheetSelection selection;

    @Override
    public String toString() {
        return String.valueOf(this.selection);
    }
}
