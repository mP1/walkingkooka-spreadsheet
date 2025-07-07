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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetReferenceKindTest implements ClassTesting2<SpreadsheetReferenceKind>,
    ToStringTesting<SpreadsheetReferenceKind> {

    @Test
    public void testColumnAbsolute() {
        this.ColumnAndCheck(1, SpreadsheetReferenceKind.ABSOLUTE);
    }

    @Test
    public void testColumnRelative() {
        this.ColumnAndCheck(1, SpreadsheetReferenceKind.RELATIVE);
    }

    private void ColumnAndCheck(final int value, final SpreadsheetReferenceKind kind) {
        this.columnCheck(kind.column(value), value, kind, false, false);
    }

    @Test
    public void testFirstColumnAbsolute() {
        this.firstColumnAndCheck(SpreadsheetReferenceKind.ABSOLUTE);
    }

    @Test
    public void testFirstColumnRelative() {
        this.firstColumnAndCheck(SpreadsheetReferenceKind.RELATIVE);
    }

    private void firstColumnAndCheck(final SpreadsheetReferenceKind kind) {
        this.columnCheck(kind.firstColumn(), 0, kind, true, false);
    }

    @Test
    public void testLastColumnAbsolute() {
        this.lastColumnAndCheck(SpreadsheetReferenceKind.ABSOLUTE);
    }

    @Test
    public void testLastColumnRelative() {
        this.lastColumnAndCheck(SpreadsheetReferenceKind.RELATIVE);
    }

    private void lastColumnAndCheck(final SpreadsheetReferenceKind kind) {
        this.columnCheck(kind.lastColumn(), SpreadsheetColumnReference.MAX_VALUE, kind, false, true);
    }

    private void columnCheck(final SpreadsheetColumnReference column,
                             final int value,
                             final SpreadsheetReferenceKind kind,
                             final boolean first,
                             final boolean last) {
        this.checkEquals(value, column.value(), "value");
        assertSame(kind, column.referenceKind(), "referenceKind");
        this.checkEquals(first, column.isFirst(), "first");
        this.checkEquals(last, column.isLast(), "last");
    }

    @Test
    public void testRowAbsolute() {
        this.RowAndCheck(1, SpreadsheetReferenceKind.ABSOLUTE);
    }

    @Test
    public void testRowRelative() {
        this.RowAndCheck(1, SpreadsheetReferenceKind.RELATIVE);
    }

    private void RowAndCheck(final int value, final SpreadsheetReferenceKind kind) {
        this.rowCheck(kind.row(value), value, kind, false, false);
    }

    @Test
    public void testFirstRowAbsolute() {
        this.firstRowAndCheck(SpreadsheetReferenceKind.ABSOLUTE);
    }

    @Test
    public void testFirstRowRelative() {
        this.firstRowAndCheck(SpreadsheetReferenceKind.RELATIVE);
    }

    private void firstRowAndCheck(final SpreadsheetReferenceKind kind) {
        this.rowCheck(kind.firstRow(), 0, kind, true, false);
    }

    @Test
    public void testLastRowAbsolute() {
        this.lastRowAndCheck(SpreadsheetReferenceKind.ABSOLUTE);
    }

    @Test
    public void testLastRowRelative() {
        this.lastRowAndCheck(SpreadsheetReferenceKind.RELATIVE);
    }

    private void lastRowAndCheck(final SpreadsheetReferenceKind kind) {
        this.rowCheck(kind.lastRow(), SpreadsheetRowReference.MAX_VALUE, kind, false, true);
    }

    private void rowCheck(final SpreadsheetRowReference row,
                          final int value,
                          final SpreadsheetReferenceKind kind,
                          final boolean first,
                          final boolean last) {
        this.checkEquals(value, row.value(), "value");
        assertSame(kind, row.referenceKind(), "referenceKind");
        this.checkEquals(first, row.isFirst(), "first");
        this.checkEquals(last, row.isLast(), "last");
    }

    @Test
    public void testAbsoluteFlip() {
        this.flipAndCheck(SpreadsheetReferenceKind.ABSOLUTE, SpreadsheetReferenceKind.RELATIVE);
    }

    @Test
    public void testRelativeFlip() {
        this.flipAndCheck(SpreadsheetReferenceKind.RELATIVE, SpreadsheetReferenceKind.ABSOLUTE);
    }

    private void flipAndCheck(final SpreadsheetReferenceKind before,
                              final SpreadsheetReferenceKind after) {
        assertSame(after, before.flip(), () -> before + ".flip");
    }

    @Override
    public Class<SpreadsheetReferenceKind> type() {
        return SpreadsheetReferenceKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
