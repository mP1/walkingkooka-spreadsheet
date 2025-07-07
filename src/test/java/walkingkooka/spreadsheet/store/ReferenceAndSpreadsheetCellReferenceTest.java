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

package walkingkooka.spreadsheet.store;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReferenceAndSpreadsheetCellReferenceTest implements HashCodeEqualsDefinedTesting2<ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName>>,
    ToStringTesting<ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName>> {

    @Test
    public void testWithNullReference() {
        assertThrows(
            NullPointerException.class,
            () -> ReferenceAndSpreadsheetCellReference.with(
                null,
                this.cell()
            )
        );
    }

    @Test
    public void testWithNullCell() {
        assertThrows(
            NullPointerException.class,
            () -> ReferenceAndSpreadsheetCellReference.with(
                this.label(),
                null
            )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetCellReference reference = this.cell();
        final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> and = ReferenceAndSpreadsheetCellReference.with(
            label,
            reference
        );

        this.referenceAndCheck(
            and
        );
        this.cellAndCheck(
            and
        );
    }

    @Test
    public void testWithSelfCell() {
        final SpreadsheetCellReference cell = this.cell();
        final ReferenceAndSpreadsheetCellReference<SpreadsheetCellReference> and = ReferenceAndSpreadsheetCellReference.with(
            cell,
            cell
        );

        this.referenceAndCheck(
            and,
            cell
        );
        this.cellAndCheck(
            and,
            cell
        );
    }

    // setReference.....................................................................................................

    @Test
    public void testSetReferenceWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setReference(null)
        );
    }

    @Test
    public void testSetReferenceWithSame() {
        final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> referenceAndSpreadsheetCellReference = this.createObject();
        assertSame(
            referenceAndSpreadsheetCellReference,
            referenceAndSpreadsheetCellReference.setReference(this.label())
        );
    }

    @Test
    public void testSetReferenceWithDifferent() {
        final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> referenceAndSpreadsheetCellReference = this.createObject();

        final SpreadsheetLabelName differentReference = SpreadsheetSelection.labelName("Different");
        final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> different = referenceAndSpreadsheetCellReference.setReference(differentReference);

        assertNotSame(
            referenceAndSpreadsheetCellReference,
            different
        );

        this.referenceAndCheck(referenceAndSpreadsheetCellReference);
        this.referenceAndCheck(
            different,
            differentReference
        );

        this.cellAndCheck(referenceAndSpreadsheetCellReference);
        this.cellAndCheck(different);
    }

    // setCell..........................................................................................................

    @Test
    public void testSetCellWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createObject()
                .setCell(null)
        );
    }

    @Test
    public void testSetCellWithSame() {
        final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> referenceAndSpreadsheetCellReference = this.createObject();
        assertSame(
            referenceAndSpreadsheetCellReference,
            referenceAndSpreadsheetCellReference.setCell(this.cell())
        );
    }

    @Test
    public void testSetCellWithDifferent() {
        final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> referenceAndSpreadsheetCellReference = this.createObject();

        final SpreadsheetCellReference differentCell = SpreadsheetSelection.parseCell("Z9");
        final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> different = referenceAndSpreadsheetCellReference.setCell(differentCell);

        assertNotSame(
            referenceAndSpreadsheetCellReference,
            different
        );

        this.cellAndCheck(
            referenceAndSpreadsheetCellReference
        );
        this.cellAndCheck(
            different,
            differentCell
        );

        this.referenceAndCheck(referenceAndSpreadsheetCellReference);
        this.referenceAndCheck(different);
    }

    // property helpers.................................................................................................

    private void referenceAndCheck(final ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> referenceAndSpreadsheetCellReference) {
        this.referenceAndCheck(
            referenceAndSpreadsheetCellReference,
            this.label()
        );
    }

    private <TT extends SpreadsheetExpressionReference> void referenceAndCheck(final ReferenceAndSpreadsheetCellReference<TT> referenceAndSpreadsheetCellReference,
                                                                               final TT expected) {
        this.checkEquals(
            expected,
            referenceAndSpreadsheetCellReference.reference()
        );
    }

    private void cellAndCheck(final ReferenceAndSpreadsheetCellReference<?> referenceAndSpreadsheetCellReference) {
        this.cellAndCheck(
            referenceAndSpreadsheetCellReference,
            this.cell()
        );
    }

    private void cellAndCheck(final ReferenceAndSpreadsheetCellReference<?> referenceAndSpreadsheetCellReference,
                              final SpreadsheetCellReference expected) {
        this.checkEquals(
            expected,
            referenceAndSpreadsheetCellReference.cell()
        );
    }

    // equals............................................................................................................

    @Test
    public void testEqualsDifferentReference() {
        this.checkNotEquals(
            ReferenceAndSpreadsheetCellReference.with(
                SpreadsheetSelection.labelName("Different"),
                this.cell()
            )
        );
    }

    @Test
    public void testEqualsDifferentCell() {
        this.checkNotEquals(
            ReferenceAndSpreadsheetCellReference.with(
                this.label(),
                SpreadsheetSelection.parseCell("Z99")
            )
        );
    }

    @Override
    public ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName> createObject() {
        return ReferenceAndSpreadsheetCellReference.with(
            this.label(),
            this.cell()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createObject(),
            this.label() + "->" + this.cell()
        );
    }

    // helpers..........................................................................................................

    private SpreadsheetLabelName label() {
        return SpreadsheetSelection.labelName("Label123");
    }

    private SpreadsheetCellReference cell() {
        return SpreadsheetSelection.A1;
    }

    // class............................................................................................................

    @Override
    public Class<ReferenceAndSpreadsheetCellReference<SpreadsheetLabelName>> type() {
        return Cast.to(ReferenceAndSpreadsheetCellReference.class);
    }
}
