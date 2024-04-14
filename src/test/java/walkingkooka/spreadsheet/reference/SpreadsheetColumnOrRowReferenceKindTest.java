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
import walkingkooka.test.ParseStringTesting;

public final class SpreadsheetColumnOrRowReferenceKindTest implements ClassTesting2<SpreadsheetColumnOrRowReferenceKind>,
        ParseStringTesting<SpreadsheetColumnOrRowReference> {

    // firstAbsolute....................................................................................................

    @Test
    public void testFirstAbsoluteColumn() {
        this.firstAbsoluteAndCheck(
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                SpreadsheetSelection.parseColumn("$A")
        );
    }

    @Test
    public void testFirstAbsoluteRow() {
        this.firstAbsoluteAndCheck(
                SpreadsheetColumnOrRowReferenceKind.ROW,
                SpreadsheetSelection.parseRow("$1")
        );
    }

    private void firstAbsoluteAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
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
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                SpreadsheetSelection.parseColumn("A")
        );
    }

    @Test
    public void testFirstRelativeRow() {
        this.firstRelativeAndCheck(
                SpreadsheetColumnOrRowReferenceKind.ROW,
                SpreadsheetSelection.parseRow("1")
        );
    }

    private void firstRelativeAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
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
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                SpreadsheetSelection.parseColumn("$XFD")
        );
    }

    @Test
    public void testLastAbsoluteRow() {
        this.lastAbsoluteAndCheck(
                SpreadsheetColumnOrRowReferenceKind.ROW,
                SpreadsheetSelection.parseRow("$1048576")
        );
    }

    private void lastAbsoluteAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
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
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                SpreadsheetSelection.parseColumn("XFD")
        );
    }

    @Test
    public void testLastRelativeRow() {
        this.lastRelativeAndCheck(
                SpreadsheetColumnOrRowReferenceKind.ROW,
                SpreadsheetSelection.parseRow("1048576")
        );
    }

    private void lastRelativeAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
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
                SpreadsheetColumnOrRowReferenceKind.COLUMN,
                SpreadsheetReferenceKind.RELATIVE,
                2,
                SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testSetValueRowRelative() {
        this.setValueAndCheck(
                SpreadsheetColumnOrRowReferenceKind.ROW,
                SpreadsheetReferenceKind.RELATIVE,
                2,
                SpreadsheetSelection.parseRow("3")
        );
    }

    @Test
    public void testSetValueRowAbsolute() {
        this.setValueAndCheck(
                SpreadsheetColumnOrRowReferenceKind.ROW,
                SpreadsheetReferenceKind.ABSOLUTE,
                3,
                SpreadsheetSelection.parseRow("$4")
        );
    }

    private void setValueAndCheck(final SpreadsheetColumnOrRowReferenceKind kind,
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

    // parse............................................................................................................

    @Test
    public void testParseCellFails() {
        this.parseStringFails(
                "A1",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseCellRangeFails() {
        this.parseStringFails(
                "B2:C3",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseColumn() {
        this.parseStringAndCheck(
                "C",
                SpreadsheetSelection.parseColumn("C")
        );
    }

    @Test
    public void testParseColumnRangeFails() {
        this.parseStringFails(
                "B:C",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseLabelFails() {
        this.parseStringFails(
                "Label123",
                IllegalArgumentException.class
        );
    }

    @Test
    public void testParseRow() {
        this.parseStringAndCheck(
                "4",
                SpreadsheetSelection.parseRow("4")
        );
    }

    @Test
    public void testParseRowRangeFails() {
        this.parseStringFails(
                "2:3",
                IllegalArgumentException.class
        );
    }

    // ClassTesting2....................................................................................................

    @Override
    public Class<SpreadsheetColumnOrRowReferenceKind> type() {
        return SpreadsheetColumnOrRowReferenceKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // parseColumnOrRow.................................................................................................

    @Override
    public SpreadsheetColumnOrRowReference parseString(final String text) {
        return SpreadsheetColumnOrRowReferenceKind.parseColumnOrRow(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> type) {
        return type;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException cause) {
        return cause;
    }
}
