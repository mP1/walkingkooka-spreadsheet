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
import walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;
import walkingkooka.tree.json.patch.PatchableTesting;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetRowTest extends SpreadsheetColumnOrRowTestCase<SpreadsheetRow, SpreadsheetRowReference>
    implements HasSpreadsheetReferenceTesting,
    PatchableTesting<SpreadsheetRow> {

    private final static SpreadsheetRowReference REFERENCE = SpreadsheetReferenceKind.RELATIVE.firstRow();

    private final static SpreadsheetRowReference DIFFERENT_REFERENCE = SpreadsheetReferenceKind.RELATIVE.row(99);

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetRow.with(null));
    }

    @Test
    public void testWith() {
        final SpreadsheetRow row = this.createRow();

        this.referenceAndCheck(row);
        this.hiddenAndCheck(row);
    }


    @Test
    public void testWithAbsoluteSpreadsheetRow() {
        final SpreadsheetRow row = SpreadsheetRow.with(
            REFERENCE.setReferenceKind(SpreadsheetReferenceKind.ABSOLUTE)
        );

        this.referenceAndCheck(row);
        this.hiddenAndCheck(row);
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
        final SpreadsheetRow different = row.setReference(DIFFERENT_REFERENCE);
        assertNotSame(row, different);

        this.referenceAndCheck(
            different,
            DIFFERENT_REFERENCE
        );

        this.referenceAndCheck(row);
    }

    @Test
    public void testSetReferenceDifferentAbsoluteColumn() {
        final SpreadsheetRow row = this.createRow();
        final SpreadsheetRowReference differentReference = SpreadsheetRowReference.parseRow("$99");
        final SpreadsheetRow different = row.setReference(differentReference);
        assertNotSame(row, different);

        this.referenceAndCheck(
            different,
            differentReference.toRelative()
        );

        this.referenceAndCheck(row);
    }

    // SetHidden........................................................................................................

    @Test
    public void testSetHiddenSame() {
        final SpreadsheetRow row = this.createRow();

        this.checkEquals(row, row.setHidden(row.hidden()));
    }

    @Test
    public void testSetHiddenDifferent() {
        final SpreadsheetRow row = this.createRow();
        final SpreadsheetRow different = row.setHidden(DIFFERENT_HIDDEN);
        assertNotSame(row, different);

        this.hiddenAndCheck(different, DIFFERENT_HIDDEN);

        this.hiddenAndCheck(row);
    }

    // equals ..........................................................................................................

    @Test
    public void testEqualsDifferentRow() {
        this.checkNotEquals(
            DIFFERENT_REFERENCE.row()
        );
    }

    @Test
    public void testEqualsDifferentHidden() {
        this.checkNotEquals(
            this.createObject()
                .setHidden(DIFFERENT_HIDDEN)
        );
    }

    // JsonNodeMarshallerTesting........................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            SpreadsheetSelection.parseRow("123").row(),
            "{\n" +
                "  \"123\": {\n" +
                "    \"hidden\": false\n" +
                "  }\n" +
                "}"
        );
    }

    // Patchable........................................................................................................

    @Test
    public void testPatchHiddenTrue() {
        final SpreadsheetRow row = SpreadsheetSelection.parseRow("1").row();

        this.patchAndCheck(
            row,
            "{\n" +
                "  \"hidden\": true\n" +
                "}",
            row.setHidden(true)
        );
    }

    @Test
    public void testPatchHiddenFalse() {
        final SpreadsheetRow row = SpreadsheetSelection.parseRow("2").row();

        this.patchAndCheck(
            row.setHidden(true),
            "{\n" +
                "  \"hidden\": false\n" +
                "}",
            row.setHidden(false)
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintableHidden() {
        final SpreadsheetRow row = this.createObject()
            .setHidden(true);

        this.treePrintAndCheck(
            row,
            "" + REFERENCE + LINE_ENDING +
                "  hidden" + LINE_ENDING
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            REFERENCE.row(),
            "1"
        );
    }

    @Test
    public void testToStringHiddenTrue() {
        this.toStringAndCheck(
            REFERENCE.row()
                .setHidden(true),
            "1 hidden=true"
        );
    }

    // HasSpreadsheetReference..........................................................................................

    @Test
    public void testReference() {
        final SpreadsheetRowReference row = SpreadsheetSelection.parseRow("123");

        this.referenceAndCheck(
            row.row(),
            row
        );
    }

    // helpers.........................................................................................................

    private SpreadsheetRow createRow() {
        return this.createObject();
    }

    @Override
    public SpreadsheetRow createObject() {
        return REFERENCE.row();
    }

    private void referenceAndCheck(final SpreadsheetRow row) {
        this.referenceAndCheck(
            row,
            REFERENCE
        );
    }

    private void hiddenAndCheck(final SpreadsheetRow row) {
        this.hiddenAndCheck(
            row,
            false
        );
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetRow> type() {
        return SpreadsheetRow.class;
    }

    // JsonNodeMarshallerTesting........................................................................................

    @Override
    public SpreadsheetRow unmarshall(final JsonNode jsonNode,
                                     final JsonNodeUnmarshallContext context) {
        return SpreadsheetRow.unmarshall(jsonNode, context);
    }

    // PatchableTesting.................................................................................................

    @Override
    public SpreadsheetRow createPatchable() {
        return this.createRow();
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
