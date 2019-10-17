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
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.type.JavaVisibility;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowTest implements ClassTesting2<SpreadsheetRow>,
        ComparableTesting2<SpreadsheetRow>,
        JsonNodeMarshallingTesting<SpreadsheetRow>,
        ToStringTesting<SpreadsheetRow> {

    private final static int ROW = 20;
    private final static SpreadsheetRowReference REFERENCE = reference(ROW);

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetRow.with(null));
    }

    @Test
    public void testWith() {
        final SpreadsheetRow row = this.createRow();

        this.checkReference(row);
    }

    // SetReference.....................................................................................................

    @Test
    public void testSetReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> this.createRow().setReference(null));
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetRow row = this.createRow();
        assertSame(row, row.setReference(row.reference()));
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetRow row = this.createRow();
        final SpreadsheetRowReference differentReference = differentReference();
        final SpreadsheetRow different = row.setReference(differentReference);
        assertNotSame(row, different);

        this.checkReference(different, differentReference);

        this.checkReference(row);
    }

    // equals .............................................................................................

    @Test
    public void testCompareDifferentRow() {
        this.compareToAndCheckLess(this.createComparable(ROW + 999));
    }

    // compare..........................................................................................................

    @Test
    public void testArraySort() {
        final SpreadsheetRow row1 = SpreadsheetRow.with(SpreadsheetColumnOrRowReference.parseRow("1"));
        final SpreadsheetRow row2 = SpreadsheetRow.with(SpreadsheetColumnOrRowReference.parseRow("2"));
        final SpreadsheetRow row3 = SpreadsheetRow.with(SpreadsheetColumnOrRowReference.parseRow("3"));
        final SpreadsheetRow row4 = SpreadsheetRow.with(SpreadsheetColumnOrRowReference.parseRow("$4"));

        this.compareToArraySortAndCheck(row3, row1, row4, row2,
                row1, row2, row3, row4);
    }

    // JsonNodeMarshallingTesting............................................................................................

    @Test
    public void testJsonNodeUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails"), IllegalArgumentException.class);
    }

    @Test
    public void testJsonNode() {
        final SpreadsheetRowReference reference = reference(ROW);
        this.marshallAndCheck(reference, reference.marshall(this.marshallContext()));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetRow.with(REFERENCE), "$21");
    }

    private SpreadsheetRow createRow() {
        return this.createComparable();
    }

    @Override
    public SpreadsheetRow createComparable() {
        return this.createComparable(ROW);
    }

    private SpreadsheetRow createComparable(final int row) {
        return SpreadsheetRow.with(reference(row));
    }

    private static SpreadsheetRowReference differentReference() {
        return reference(999);
    }

    private static SpreadsheetRowReference reference(final int row) {
        return SpreadsheetReferenceKind.ABSOLUTE.row(row);
    }

    private void checkReference(final SpreadsheetRow row) {
        this.checkReference(row, REFERENCE);
    }

    private void checkReference(final SpreadsheetRow row, final SpreadsheetRowReference reference) {
        assertEquals(reference, row.reference(), "reference");
        assertEquals(Optional.of(reference), row.id(), "id");
    }

    @Override
    public Class<SpreadsheetRow> type() {
        return SpreadsheetRow.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    @Override
    public boolean compareAndEqualsMatch() {
        return true;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetRow createJsonNodeMappingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetRow unmarshall(final JsonNode jsonNode,
                                     final JsonNodeUnmarshallContext context) {
        return SpreadsheetRow.unmarshall(jsonNode, context);
    }
}
