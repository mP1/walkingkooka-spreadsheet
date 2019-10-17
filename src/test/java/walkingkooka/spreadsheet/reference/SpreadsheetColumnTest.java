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

public final class SpreadsheetColumnTest implements ClassTesting2<SpreadsheetColumn>,
        ComparableTesting2<SpreadsheetColumn>,
        JsonNodeMarshallingTesting<SpreadsheetColumn>,
        ToStringTesting<SpreadsheetColumn> {

    private final static int COLUMN = 20;
    private final static SpreadsheetColumnReference REFERENCE = reference(COLUMN);

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetColumn.with(null));
    }

    @Test
    public void testWith() {
        final SpreadsheetColumn column = this.createColumn();

        this.checkReference(column);
    }

    // SetReference.....................................................................................................

    @Test
    public void testSetReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> this.createColumn().setReference(null));
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetColumn column = this.createColumn();
        assertSame(column, column.setReference(column.reference()));
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetColumn column = this.createColumn();
        final SpreadsheetColumnReference differentReference = differentReference();
        final SpreadsheetColumn different = column.setReference(differentReference);
        assertNotSame(column, different);

        this.checkReference(different, differentReference);

        this.checkReference(column);
    }

    // equals .............................................................................................

    @Test
    public void testCompareDifferentColumn() {
        this.compareToAndCheckLess(this.createComparable(COLUMN + 999));
    }

    @Test
    public void testArraySort() {
        final SpreadsheetColumn column1 = SpreadsheetColumn.with(SpreadsheetColumnOrRowReference.parseColumn("A"));
        final SpreadsheetColumn column2 = SpreadsheetColumn.with(SpreadsheetColumnOrRowReference.parseColumn("$B"));
        final SpreadsheetColumn column3 = SpreadsheetColumn.with(SpreadsheetColumnOrRowReference.parseColumn("C"));
        final SpreadsheetColumn column4 = SpreadsheetColumn.with(SpreadsheetColumnOrRowReference.parseColumn("$D"));

        this.compareToArraySortAndCheck(column3, column1, column4, column2,
                column1, column2, column3, column4);
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Test
    public void testJsonNodeUnmarshallStringFails() {
        this.unmarshallFails(JsonNode.string("fails"), IllegalArgumentException.class);
    }

    @Test
    public void testJsonNode() {
        final SpreadsheetColumnReference reference = reference(COLUMN);
        this.marshallAndCheck(reference, reference.marshall(this.marshallContext()));
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetColumn.with(REFERENCE), "$U");
    }

    private SpreadsheetColumn createColumn() {
        return this.createComparable();
    }

    @Override
    public SpreadsheetColumn createComparable() {
        return this.createComparable(COLUMN);
    }

    private SpreadsheetColumn createComparable(final int column) {
        return SpreadsheetColumn.with(reference(column));
    }

    private static SpreadsheetColumnReference differentReference() {
        return reference(999);
    }

    private static SpreadsheetColumnReference reference(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column);
    }

    private void checkReference(final SpreadsheetColumn column) {
        this.checkReference(column, REFERENCE);
    }

    private void checkReference(final SpreadsheetColumn column, final SpreadsheetColumnReference reference) {
        assertEquals(reference, column.reference(), "reference");
        assertEquals(Optional.of(reference), column.id(), "id");
    }

    @Override
    public Class<SpreadsheetColumn> type() {
        return SpreadsheetColumn.class;
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
    public SpreadsheetColumn createJsonNodeMappingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetColumn unmarshall(final JsonNode jsonNode,
                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetColumn.unmarshall(jsonNode, context);
    }
}
