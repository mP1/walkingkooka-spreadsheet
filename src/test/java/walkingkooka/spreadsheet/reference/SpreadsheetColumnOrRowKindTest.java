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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

public final class SpreadsheetColumnOrRowKindTest implements ClassTesting2<SpreadsheetColumnOrRowKind> {

    // firstAbsolute....................................................................................................

    @Test
    public void testFirstAbsoluteColumn() {
        this.firstAbsoluteAndCheck(
                SpreadsheetColumnOrRowKind.COLUMN,
                SpreadsheetSelection.parseColumn("$A")
        );
    }

    @Test
    public void testFirstAbsoluteRow() {
        this.firstAbsoluteAndCheck(
                SpreadsheetColumnOrRowKind.ROW,
                SpreadsheetSelection.parseRow("$1")
        );
    }

    private void firstAbsoluteAndCheck(final SpreadsheetColumnOrRowKind kind,
                                       final SpreadsheetColumnOrRowReference expected) {
        this.checkEquals(
                expected,
                kind.firstAbsolute()
        );
    }

    // firstRelative....................................................................................................

    @Test
    public void testFirstRelativeColumn() {
        this.firstRelativeAndCheck(
                SpreadsheetColumnOrRowKind.COLUMN,
                SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testFirstRelativeRow() {
        this.firstRelativeAndCheck(
                SpreadsheetColumnOrRowKind.ROW,
                SpreadsheetSelection.parseRow("1")
        );
    }

    private void firstRelativeAndCheck(final SpreadsheetColumnOrRowKind kind,
                                       final SpreadsheetColumnOrRowReference expected) {
        this.checkEquals(
                expected,
                kind.firstRelative()
        );
    }

    // lastAbsolute....................................................................................................

    @Test
    public void testLastAbsoluteColumn() {
        this.lastAbsoluteAndCheck(
                SpreadsheetColumnOrRowKind.COLUMN,
                SpreadsheetSelection.parseColumn("$XFD")
        );
    }

    @Test
    public void testLastAbsoluteRow() {
        this.lastAbsoluteAndCheck(
                SpreadsheetColumnOrRowKind.ROW,
                SpreadsheetSelection.parseRow("$1048576")
        );
    }

    private void lastAbsoluteAndCheck(final SpreadsheetColumnOrRowKind kind,
                                      final SpreadsheetColumnOrRowReference expected) {
        this.checkEquals(
                expected,
                kind.lastAbsolute()
        );
    }

    // lastRelative....................................................................................................

    @Test
    public void testLastRelativeColumn() {
        this.lastRelativeAndCheck(
                SpreadsheetColumnOrRowKind.COLUMN,
                SpreadsheetSelection.parseColumn("XFD")
        );
    }

    @Test
    public void testLastRelativeRow() {
        this.lastRelativeAndCheck(
                SpreadsheetColumnOrRowKind.ROW,
                SpreadsheetSelection.parseRow("1048576")
        );
    }

    private void lastRelativeAndCheck(final SpreadsheetColumnOrRowKind kind,
                                      final SpreadsheetColumnOrRowReference expected) {
        this.checkEquals(
                expected,
                kind.lastRelative()
        );
    }

    // setValue.........................................................................................................

    @Test
    public void testSetValueColumnRelative() {
        this.setValueAndCheck(
                SpreadsheetColumnOrRowKind.COLUMN,
                SpreadsheetReferenceKind.RELATIVE,
                2,
                SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testSetValueRowRelative() {
        this.setValueAndCheck(
                SpreadsheetColumnOrRowKind.ROW,
                SpreadsheetReferenceKind.RELATIVE,
                2,
                SpreadsheetSelection.parseRow("3")
        );
    }

    @Test
    public void testSetValueRowAbsolute() {
        this.setValueAndCheck(
                SpreadsheetColumnOrRowKind.ROW,
                SpreadsheetReferenceKind.ABSOLUTE,
                3,
                SpreadsheetSelection.parseRow("$4")
        );
    }

    private void setValueAndCheck(final SpreadsheetColumnOrRowKind kind,
                                  final SpreadsheetReferenceKind referenceKind,
                                  final int value,
                                  final SpreadsheetColumnOrRowReference expected) {
        this.checkEquals(
                expected,
                kind.setValue(
                        referenceKind,
                        value
                ),
                () -> kind + " setValue " + referenceKind + ", " + value
        );
    }

    // ClassTesting2....................................................................................................

    @Override
    public Class<SpreadsheetColumnOrRowKind> type() {
        return SpreadsheetColumnOrRowKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
