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
import walkingkooka.ToStringTesting;
import walkingkooka.compare.ComparableTesting2;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetLabelMappingTest implements ClassTesting2<SpreadsheetLabelMapping>,
        ComparableTesting2<SpreadsheetLabelMapping>,
        JsonNodeMarshallingTesting<SpreadsheetLabelMapping>,
        HateosResourceTesting<SpreadsheetLabelMapping>,
        TreePrintableTesting,
        ToStringTesting<SpreadsheetLabelMapping> {

    private final static SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName("label123");
    private final static SpreadsheetExpressionReference REFERENCE = cell(1);

    @Test
    public void testWithNullLabelFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetLabelMapping.with(null, REFERENCE));
    }

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetLabelMapping.with(LABEL, null));
    }

    @Test
    public void testWithReferenceSameAsLabelFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetLabelMapping.with(LABEL, LABEL));
    }

    @Test
    public void testWith() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        this.checkLabel(mapping, LABEL);
        this.checkReference(mapping, REFERENCE);
    }

    // setLabel.......................................................................................................

    @Test
    public void testSetLabelNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().setLabel(null));
    }

    @Test
    public void testSetLabelSameAsReferenceFails() {
        final SpreadsheetLabelName different = SpreadsheetLabelName.with("different");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(LABEL, different);

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> mapping.setLabel(different));
        this.checkEquals("New label \"different\" must be different from reference \"different\"", thrown.getMessage());
    }

    @Test
    public void testSetLabelSame() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        assertSame(mapping, mapping.setLabel(LABEL));
    }

    @Test
    public void testSetLabelDifferent() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        final SpreadsheetLabelName differentLabel = SpreadsheetSelection.labelName("different");
        final SpreadsheetLabelMapping different = mapping.setLabel(differentLabel);

        assertNotSame(mapping, different);
        this.checkLabel(different, differentLabel);
        this.checkReference(different, REFERENCE);
    }

    // setReference.......................................................................................................

    @Test
    public void testSetReferenceNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().setReference(null));
    }

    @Test
    public void testSetReferenceSameLabelFails() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        assertSame(LABEL, mapping.label());

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> mapping.setReference(LABEL));
        this.checkEquals(
                "Reference \"label123\" must be different to label \"label123\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testSetReferenceSame() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        assertSame(mapping, mapping.setReference(REFERENCE));
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        final SpreadsheetExpressionReference differentReference = cell(999);
        final SpreadsheetLabelMapping different = mapping.setReference(differentReference);

        assertNotSame(mapping, different);
        this.checkLabel(different, LABEL);
        this.checkReference(different, differentReference);
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testUnmarshallEmptyObjectFails() {
        this.unmarshallFails(JsonNode.object());
    }

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createObject(),
                "{\n" +
                        "  \"label\": \"label123\",\n" +
                        "  \"reference\": \"$B3\"\n" +
                        "}"
        );
    }

    @Test
    public void testJsonRoundtripCellReference() {
        this.marshallRoundTrip2(SpreadsheetSelection.A1);
    }

    @Test
    public void testJsonRoundtripLabelName() {
        this.marshallRoundTrip2(SpreadsheetLabelName.labelName("LABEL456"));
    }

    @Test
    public void testJsonRoundtripRange() {
        this.marshallRoundTrip2(SpreadsheetSelection.parseCellRange("A1:B2"));
    }

    private void marshallRoundTrip2(final SpreadsheetExpressionReference reference) {
        this.marshallRoundTripTwiceAndCheck(
                SpreadsheetLabelName.with("Label234").mapping(reference)
        );
    }

    // HateosResource....................................................................................................

    @Test
    public void testHateosLinkId() {
        final String text = "ABC12345678";
        this.hateosLinkIdAndCheck(SpreadsheetLabelMapping.with(SpreadsheetLabelName.with(text), SpreadsheetSelection.A1),
                text);
    }

    // TreePrintableTesting.............................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                this.createComparable(),
                "label123: $B3" + EOL
        );
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(SpreadsheetLabelMapping.with(SpreadsheetSelection.labelName("different"), REFERENCE));
    }

    @Test
    public void testEqualsDifferentCell() {
        this.checkNotEquals(SpreadsheetLabelMapping.with(LABEL, cell(99)));
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(), LABEL + "=" + REFERENCE);
    }

    // helpers...............................................................................................

    @Override
    public SpreadsheetLabelMapping createComparable() {
        return SpreadsheetLabelMapping.with(LABEL, REFERENCE);
    }

    private void checkLabel(final SpreadsheetLabelMapping mapping, final SpreadsheetLabelName label) {
        this.checkEquals(label, mapping.label(), "label");
    }

    private void checkReference(final SpreadsheetLabelMapping mapping,
                                final SpreadsheetExpressionReference reference) {
        this.checkEquals(reference, mapping.reference(), "reference");
    }

    private static SpreadsheetCellReference cell(final int column) {
        return SpreadsheetReferenceKind.ABSOLUTE.column(column)
                .setRow(SpreadsheetReferenceKind.RELATIVE.row(2));
    }

    // ClassTesting...............................................................................................

    @Override
    public Class<SpreadsheetLabelMapping> type() {
        return SpreadsheetLabelMapping.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetLabelMapping unmarshall(final JsonNode node,
                                              final JsonNodeUnmarshallContext context) {
        return SpreadsheetLabelMapping.unmarshall(node, context);
    }

    @Override
    public SpreadsheetLabelMapping createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    // HateosResourceTesting............................................................................................

    @Override
    public SpreadsheetLabelMapping createHateosResource() {
        return this.createObject();
    }
}
