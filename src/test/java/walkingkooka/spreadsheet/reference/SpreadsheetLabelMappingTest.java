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
        HateosResourceTesting<SpreadsheetLabelMapping, SpreadsheetLabelName>,
        TreePrintableTesting,
        ToStringTesting<SpreadsheetLabelMapping> {

    private final static SpreadsheetLabelName LABEL = SpreadsheetSelection.labelName("label123");
    private final static SpreadsheetExpressionReference TARGET = cell(1);

    @Test
    public void testWithNullLabelFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetLabelMapping.with(null, TARGET)
        );
    }

    @Test
    public void testWithNullTargetFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetLabelMapping.with(LABEL, null)
        );
    }

    @Test
    public void testWithTargetSameAsLabelFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetLabelMapping.with(LABEL, LABEL)
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        this.checkLabel(mapping, LABEL);
        this.checkTarget(mapping, TARGET);
    }

    // setLabel.......................................................................................................

    @Test
    public void testSetLabelNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().setLabel(null));
    }

    @Test
    public void testSetLabelSameAsTargetFails() {
        final SpreadsheetLabelName different = SpreadsheetLabelName.with("different");
        final SpreadsheetLabelMapping mapping = SpreadsheetLabelMapping.with(LABEL, different);

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> mapping.setLabel(different));
        this.checkEquals("New label \"different\" must be different parse target \"different\"", thrown.getMessage());
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
        this.checkTarget(different, TARGET);
    }

    // setTarget.......................................................................................................

    @Test
    public void testSetTargetNullFails() {
        assertThrows(NullPointerException.class, () -> this.createObject().setTarget(null));
    }

    @Test
    public void testSetTargetSameLabelFails() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        assertSame(LABEL, mapping.label());

        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> mapping.setTarget(LABEL));
        this.checkEquals(
                "Target \"label123\" must be different to label \"label123\"",
                thrown.getMessage()
        );
    }

    @Test
    public void testSetTargetSame() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        assertSame(mapping, mapping.setTarget(TARGET));
    }

    @Test
    public void testSetTargetDifferent() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        final SpreadsheetExpressionReference differentTarget = cell(999);
        final SpreadsheetLabelMapping different = mapping.setTarget(differentTarget);

        assertNotSame(mapping, different);
        this.checkLabel(different, LABEL);
        this.checkTarget(different, differentTarget);
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
                        "  \"target\": \"$B3\"\n" +
                        "}"
        );
    }

    @Test
    public void testJsonRoundtripCellTarget() {
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
        this.checkNotEquals(SpreadsheetLabelMapping.with(SpreadsheetSelection.labelName("different"), TARGET));
    }

    @Test
    public void testEqualsDifferentCell() {
        this.checkNotEquals(SpreadsheetLabelMapping.with(LABEL, cell(99)));
    }

    // compareTo........................................................................................................

    @Test
    public void testCompareToSameLabelsSameCase() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        this.compareToAndCheckEquals(
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.A1
                ),
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.A1
                )
        );
    }

    @Test
    public void testCompareToSameLabelsDifferentCase() {
        this.compareToAndCheckEquals(
                SpreadsheetLabelMapping.with(
                        SpreadsheetSelection.labelName("Label123"),
                        SpreadsheetSelection.A1
                ),
                SpreadsheetLabelMapping.with(
                        SpreadsheetSelection.labelName("LABEL123"),
                        SpreadsheetSelection.A1
                )
        );
    }

    @Test
    public void testCompareToDifferentLabels() {
        this.compareToAndCheckLess(
                SpreadsheetLabelMapping.with(
                        SpreadsheetSelection.labelName("Before"),
                        SpreadsheetSelection.A1
                ),
                SpreadsheetLabelMapping.with(
                        SpreadsheetSelection.labelName("ZZZ"),
                        SpreadsheetSelection.A1
                )
        );
    }

    @Test
    public void testCompareToSameLabelsDifferentTarget() {
        final SpreadsheetLabelName label = SpreadsheetLabelName.with("Label123");

        this.compareToAndCheckLess(
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.parseCell("a1")
                ),
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.parseCell("B2")
                )
        );
    }

    @Test
    public void testCompareToSameLabelsCaseIgnored() {
        final SpreadsheetLabelName label = SpreadsheetLabelName.with("Label123");

        this.compareToAndCheckLess(
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.parseCell("A1")
                ),
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.parseCell("B2")
                )
        );
    }

    @Test
    public void testCompareToSameLabelsKindIgnored() {
        final SpreadsheetLabelName label = SpreadsheetLabelName.with("Label123");

        this.compareToAndCheckLess(
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.parseCell("$A$1")
                ),
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.parseCell("$B$2")
                )
        );
    }

    @Test
    public void testCompareToSameLabelsKindIgnored2() {
        final SpreadsheetLabelName label = SpreadsheetLabelName.with("Label123");

        this.compareToAndCheckEquals(
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.parseCell("$A$1")
                ),
                SpreadsheetLabelMapping.with(
                        label,
                        SpreadsheetSelection.A1
                )
        );
    }

    @Override
    public SpreadsheetLabelMapping createComparable() {
        return SpreadsheetLabelMapping.with(LABEL, TARGET);
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(), LABEL + "=" + TARGET);
    }

    // helpers...............................................................................................

    private void checkLabel(final SpreadsheetLabelMapping mapping, final SpreadsheetLabelName label) {
        this.checkEquals(label, mapping.label(), "label");
    }

    private void checkTarget(final SpreadsheetLabelMapping mapping,
                             final SpreadsheetExpressionReference reference) {
        this.checkEquals(reference, mapping.target(), "reference");
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
