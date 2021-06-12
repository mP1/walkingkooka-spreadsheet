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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetReferenceKindTest implements ClassTesting2<SpreadsheetReferenceKind>,
        ToStringTesting<SpreadsheetReferenceKind> {

    @Test
    public void testFirstColumnAbsolute() {
        this.firstColumnAndCheck(SpreadsheetReferenceKind.ABSOLUTE);
    }

    @Test
    public void testFirstColumnRelative() {
        this.firstColumnAndCheck(SpreadsheetReferenceKind.RELATIVE);
    }

    private void firstColumnAndCheck(final SpreadsheetReferenceKind kind) {
        this.columnCheck(kind.firstColumn(), 0, kind);
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
        this.columnCheck(kind.lastColumn(), SpreadsheetColumnReference.MAX - 1, kind);
    }

    private void columnCheck(final SpreadsheetColumnReference column,
                             final int value,
                             final SpreadsheetReferenceKind kind) {
        assertEquals(value, column.value(), "value");
        assertSame(kind, column.referenceKind(), "referenceKind");
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
        final SpreadsheetRowReference row = kind.firstRow();
        assertEquals(0, row.value(), "value");
        assertSame(kind, row.referenceKind(), "referenceKind");
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
