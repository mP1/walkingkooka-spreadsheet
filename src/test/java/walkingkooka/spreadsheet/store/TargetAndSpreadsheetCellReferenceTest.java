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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TargetAndSpreadsheetCellReferenceTest implements HashCodeEqualsDefinedTesting2<TargetAndSpreadsheetCellReference<SpreadsheetLabelName>>,
        ToStringTesting<TargetAndSpreadsheetCellReference<SpreadsheetLabelName>> {

    @Test
    public void testWithNullTarget() {
        assertThrows(
                NullPointerException.class,
                () -> TargetAndSpreadsheetCellReference.with(
                        null,
                        this.cell()
                )
        );
    }

    @Test
    public void testWithNullCell() {
        assertThrows(
                NullPointerException.class,
                () -> TargetAndSpreadsheetCellReference.with(
                        this.label(),
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetCellReference reference = this.cell();
        final TargetAndSpreadsheetCellReference<?> and = TargetAndSpreadsheetCellReference.with(
                label,
                reference
        );

        this.checkEquals(
                label,
                and.target(),
                "target"
        );
        this.checkEquals(
                reference,
                and.cell(),
                "cell"
        );
    }

    @Test
    public void testWithSelfCell() {
        final SpreadsheetCellReference reference = this.cell();
        final TargetAndSpreadsheetCellReference<?> and = TargetAndSpreadsheetCellReference.with(
                reference,
                reference
        );

        this.checkEquals(
                reference,
                and.target(),
                "target"
        );
        this.checkEquals(
                reference,
                and.cell(),
                "cell"
        );
    }

    // equals............................................................................................................

    @Test
    public void testEqualsDifferentTarget() {
        this.checkNotEquals(
                TargetAndSpreadsheetCellReference.with(
                        SpreadsheetSelection.labelName("Different"),
                        this.cell()
                )
        );
    }

    @Test
    public void testEqualsDifferentCell() {
        this.checkNotEquals(
                TargetAndSpreadsheetCellReference.with(
                        this.label(),
                        SpreadsheetSelection.parseCell("Z99")
                )
        );
    }

    @Override
    public TargetAndSpreadsheetCellReference<SpreadsheetLabelName> createObject() {
        return TargetAndSpreadsheetCellReference.with(
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
    public Class<TargetAndSpreadsheetCellReference<SpreadsheetLabelName>> type() {
        return Cast.to(TargetAndSpreadsheetCellReference.class);
    }
}
