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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetExpressionReferenceTestCase<R extends SpreadsheetExpressionReference> extends SpreadsheetSelectionTestCase<R> {

    SpreadsheetExpressionReferenceTestCase() {
        super();
    }

    // add..............................................................................................................

    @Test
    public final void testAddFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection().add(0)
        );
    }

    // addSaturated.....................................................................................................

    @Test
    public final void testAddSaturatedFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection().addSaturated(0)
        );
    }

    // addIfRelative....................................................................................................

    @Test
    public final void testAddIfRelativeFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createSelection()
                .addIfRelative(0)
        );
    }

    @Test
    public final void testAddIfRelative() {
        this.addIfRelativeAndCheck(
            this.createSelection(),
            0,
            0
        );
    }

    final void addIfRelativeAndCheck(final R selection,
                                     final int columnDelta,
                                     final int rowDelta) {
        assertSame(
            selection,
            selection.addIfRelative(
                columnDelta,
                rowDelta
            ),
            () -> selection + " addIfRelative " + columnDelta + ", " + selection
        );
    }

    final void addIfRelativeAndCheck(final R selection,
                                     final int columnDelta,
                                     final int rowDelta,
                                     final R expected) {
        this.checkEquals(
            expected,
            selection.addIfRelative(
                columnDelta,
                rowDelta
            ),
            () -> selection + " addIfRelative " + columnDelta + ", " + selection
        );
    }

    // cellColumnOrRowText..............................................................................................

    @Test
    public final void testCellColumnOrRow() {
        this.cellColumnOrRowTextAndCheck("cell");
    }

    // toExpressionReference............................................................................................

    @Test
    public final void testExpressionReference() {
        this.toExpressionReferenceAndCheck();
    }

    // equalsIgnoreReferenceKind..........................................................................................

    @Test
    public final void testEqualsIgnoreReferenceKindNullFalse() {
        this.equalsIgnoreReferenceKindAndCheck(this.createSelection(), null, false);
    }

    @Test
    public final void testEqualsIgnoreReferenceKindDifferentTypeFalse() {
        this.equalsIgnoreReferenceKindAndCheck(this.createSelection(), this, false);
    }

    @Test
    public final void testEqualsIgnoreReferenceKindSameTrue() {
        final R reference = this.createSelection();
        this.equalsIgnoreReferenceKindAndCheck(reference,
            reference,
            true);
    }

    @Test
    public final void testEqualsIgnoreReferenceKindSameTrue2() {
        this.equalsIgnoreReferenceKindAndCheck(this.createSelection(),
            this.createSelection(),
            true);
    }

    final void equalsIgnoreReferenceKindAndCheck(final R reference1,
                                                 final Object other,
                                                 final boolean expected) {
        this.checkEquals(expected,
            reference1.equalsIgnoreReferenceKind(other),
            () -> reference1 + " equalsIgnoreReferenceKind " + other
        );
        if (other instanceof SpreadsheetExpressionReference) {
            final R reference2 = (R) other;
            this.checkEquals(expected,
                reference2.equalsIgnoreReferenceKind(reference1),
                () -> reference2 + " equalsIgnoreReferenceKind " + reference1);
        }
    }
}
