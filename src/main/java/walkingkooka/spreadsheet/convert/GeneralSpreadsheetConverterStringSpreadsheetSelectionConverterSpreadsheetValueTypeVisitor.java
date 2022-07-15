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

/**
 * A {@link SpreadsheetValueTypeVisitor} which accepts the {@link String} and calls the right parse method on {@link SpreadsheetSelection}.
 */
final class GeneralSpreadsheetConverterStringSpreadsheetSelectionConverterSpreadsheetValueTypeVisitor extends SpreadsheetValueTypeVisitor {

    static <S extends SpreadsheetSelection> S parse(final String string,
                                                    final Class<S> selectionType) {
        final GeneralSpreadsheetConverterStringSpreadsheetSelectionConverterSpreadsheetValueTypeVisitor visitor = new GeneralSpreadsheetConverterStringSpreadsheetSelectionConverterSpreadsheetValueTypeVisitor(string);
        visitor.accept(selectionType);
        return Cast.to(visitor.selection);
    }

    GeneralSpreadsheetConverterStringSpreadsheetSelectionConverterSpreadsheetValueTypeVisitor(final String string) {
        super();
        this.string = string;
    }

    @Override
    protected void visitCellRange() {
        this.selection = SpreadsheetSelection.parseCellRange(this.string);
    }

    @Override
    protected void visitCellReference() {
        this.selection = SpreadsheetSelection.parseCell(this.string);
    }

    @Override
    protected void visitColumnReference() {
        this.selection = SpreadsheetSelection.parseColumn(this.string);
    }

    @Override
    protected void visitColumnReferenceRange() {
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
    protected void visitRowReferenceRange() {
        this.selection = SpreadsheetSelection.parseRowRange(this.string);
    }

    private final String string;

    private SpreadsheetSelection selection;

    @Override
    public String toString() {
        return String.valueOf(this.selection);
    }
}
