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

package walkingkooka.spreadsheet.value;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReferenceTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetColumnTest extends SpreadsheetColumnOrRowTestCase<SpreadsheetColumn, SpreadsheetColumnReference>
    implements HasSpreadsheetReferenceTesting,
    PatchableTesting<SpreadsheetColumn> {

    private final static SpreadsheetColumnReference REFERENCE = SpreadsheetReferenceKind.RELATIVE.firstColumn();

    private final static SpreadsheetColumnReference DIFFERENT_REFERENCE = SpreadsheetReferenceKind.RELATIVE.column(99);

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetColumn.with(null));
    }

    @Test
    public void testWith() {
        final SpreadsheetColumn column = this.createColumn();

        this.referenceAndCheck(column);
        this.hiddenAndCheck(column);
    }

    @Test
    public void testWithAbsoluteSpreadsheetColumn() {
        final SpreadsheetColumn column = SpreadsheetColumn.with(
            REFERENCE.setReferenceKind(SpreadsheetReferenceKind.ABSOLUTE)
        );

        this.referenceAndCheck(column);
        this.hiddenAndCheck(column);
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
        final SpreadsheetColumn different = column.setReference(DIFFERENT_REFERENCE);
        assertNotSame(column, different);

        this.referenceAndCheck(
            different,
            DIFFERENT_REFERENCE
        );

        this.referenceAndCheck(column);
    }

    @Test
    public void testSetReferenceDifferentAbsoluteColumn() {
        final SpreadsheetColumn column = this.createColumn();
        final SpreadsheetColumnReference differentReference = SpreadsheetColumnReference.parseColumn("$Z");
        final SpreadsheetColumn different = column.setReference(differentReference);
        assertNotSame(column, different);

        this.referenceAndCheck(
            different,
            differentReference.toRelative()
        );

        this.referenceAndCheck(column);
    }

    // SetHidden........................................................................................................

    @Test
    public void testSetHiddenSame() {
        final SpreadsheetColumn column = this.createColumn();

        this.checkEquals(column, column.setHidden(column.hidden()));
    }

    @Test
    public void testSetHiddenDifferent() {
        final SpreadsheetColumn column = this.createColumn();
        final SpreadsheetColumn different = column.setHidden(DIFFERENT_HIDDEN);
        assertNotSame(column, different);

        this.hiddenAndCheck(
            different,
            DIFFERENT_HIDDEN
        );

        this.hiddenAndCheck(column);
    }

    // equals ..........................................................................................................

    @Test
    public void testEqualsDifferentColumn() {
        this.checkNotEquals(
            DIFFERENT_REFERENCE.column()
        );
    }

    @Test
    public void testCompareDifferentHidden() {
        this.checkNotEquals(
            this.createObject()
                .setHidden(DIFFERENT_HIDDEN)
        );
    }

    // JsonNodeMarshallerTesting........................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            SpreadsheetSelection.parseColumn("ABC")
                .column(),
            "{\n" +
                "  \"ABC\": {\n" +
                "    \"hidden\": false\n" +
                "  }\n" +
                "}"
        );
    }

    // Patchable........................................................................................................

    @Test
    public void testPatchHiddenTrue() {
        final SpreadsheetColumn column = SpreadsheetSelection.parseColumn("A")
            .column();

        this.patchAndCheck(
            column,
            "{\n" +
                "  \"hidden\": true\n" +
                "}",
            column.setHidden(true)
        );
    }

    @Test
    public void testPatchHiddenFalse() {
        final SpreadsheetColumn column = SpreadsheetSelection.parseColumn("B")
            .column();

        this.patchAndCheck(
            column.setHidden(true),
            "{\n" +
                "  \"hidden\": false\n" +
                "}",
            column.setHidden(false)
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintableHidden() {
        final SpreadsheetColumn column = this.createObject()
            .setHidden(true);

        this.treePrintAndCheck(
            column,
            "" + REFERENCE + LINE_ENDING +
                "  hidden" + LINE_ENDING
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            REFERENCE.column(),
            "A"
        );
    }

    @Test
    public void testToStringHiddenTrue() {
        this.toStringAndCheck(
            REFERENCE.column()
                .setHidden(true),
            "A hidden=true"
        );
    }

    // HasSpreadsheetReference..........................................................................................

    @Test
    public void testReference() {
        final SpreadsheetColumnReference column = SpreadsheetSelection.parseColumn("AB");

        this.referenceAndCheck(
            column.column(),
            column
        );
    }

    // helpers.........................................................................................................

    private SpreadsheetColumn createColumn() {
        return this.createObject();
    }

    @Override
    public SpreadsheetColumn createObject() {
        return REFERENCE.column();
    }

    private void referenceAndCheck(final SpreadsheetColumn column) {
        this.referenceAndCheck(
            column,
            REFERENCE
        );
    }

    private void hiddenAndCheck(final SpreadsheetColumn column) {
        this.hiddenAndCheck(
            column,
            false
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetColumn> type() {
        return SpreadsheetColumn.class;
    }

    // JsonNodeMarshallerTesting........................................................................................

    @Override
    public SpreadsheetColumn unmarshall(final JsonNode jsonNode,
                                        final JsonNodeUnmarshallContext context) {
        return SpreadsheetColumn.unmarshall(jsonNode, context);
    }

    // PatchableTesting.................................................................................................

    @Override
    public SpreadsheetColumn createPatchable() {
        return this.createColumn();
    }

    @Override
    public JsonNode createPatch() {
        return JsonNode.object();
    }

    @Override
    public JsonNodeUnmarshallContext createPatchContext() {
        return JsonNodeUnmarshallContexts.fake();
    }
}
