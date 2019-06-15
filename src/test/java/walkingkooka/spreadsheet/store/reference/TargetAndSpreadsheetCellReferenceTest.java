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

package walkingkooka.spreadsheet.store.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.test.HashCodeEqualsDefinedTesting;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class TargetAndSpreadsheetCellReferenceTest implements HashCodeEqualsDefinedTesting<TargetAndSpreadsheetCellReference<SpreadsheetLabelName>>,
        ToStringTesting<TargetAndSpreadsheetCellReference<SpreadsheetLabelName>> {

    @Test
    public void testWithNullTarget() {
        assertThrows(NullPointerException.class, () -> {
            TargetAndSpreadsheetCellReference.with(null, this.reference());
        });
    }

    @Test
    public void testWithNullCellReference() {
        assertThrows(NullPointerException.class, () -> {
            TargetAndSpreadsheetCellReference.with(this.label(), null);
        });
    }

    @Test
    public void testWithRefererEqualCellReference() {
        final SpreadsheetCellReference cell = this.reference();

        assertThrows(IllegalArgumentException.class, () -> {
            TargetAndSpreadsheetCellReference.with(cell, cell);
        });
    }

    @Test
    public void testWith() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetCellReference reference = this.reference();
        final TargetAndSpreadsheetCellReference and = TargetAndSpreadsheetCellReference.with(label, reference);
        assertEquals(label, and.target(), "target");
        assertEquals(reference, and.reference(), "reference");
    }

    @Test
    public void testDifferentTarget() {
        this.checkNotEquals(TargetAndSpreadsheetCellReference.with(SpreadsheetLabelName.with("Different"), this.reference()));
    }

    @Test
    public void testDifferentCellReference() {
        this.checkNotEquals(TargetAndSpreadsheetCellReference.with(this.label(), SpreadsheetCellReference.parse("Z99")));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(), this.label() + "->" + this.reference());
    }

    @Override
    public TargetAndSpreadsheetCellReference<SpreadsheetLabelName> createObject() {
        return TargetAndSpreadsheetCellReference.with(this.label(), this.reference());
    }

    private SpreadsheetLabelName label() {
        return SpreadsheetLabelName.with("Label123");
    }

    private SpreadsheetCellReference reference() {
        return SpreadsheetCellReference.parse("A1");
    }

    @Override
    public Class<TargetAndSpreadsheetCellReference<SpreadsheetLabelName>> type() {
        return Cast.to(TargetAndSpreadsheetCellReference.class);
    }
}
