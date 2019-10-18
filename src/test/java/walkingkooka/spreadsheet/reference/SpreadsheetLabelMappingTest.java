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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.net.http.server.hateos.HateosResourceTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetLabelMappingTest implements ClassTesting2<SpreadsheetLabelMapping>,
        HashCodeEqualsDefinedTesting2<SpreadsheetLabelMapping>,
        JsonNodeMarshallingTesting<SpreadsheetLabelMapping>,
        HateosResourceTesting<SpreadsheetLabelMapping>,
        ToStringTesting<SpreadsheetLabelMapping> {

    private final static SpreadsheetLabelName LABEL = SpreadsheetExpressionReference.labelName("label");
    private final static ExpressionReference REFERENCE = cell(1);

    @Test
    public void testWithNullLabelFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetLabelMapping.with(null, REFERENCE));
    }

    @Test
    public void testWithNullReferenceFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetLabelMapping.with(LABEL, null));
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
    public void testSetLabelSame() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        assertSame(mapping, mapping.setLabel(LABEL));
    }

    @Test
    public void testSetLabelDifferent() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        final SpreadsheetLabelName differentLabel = SpreadsheetExpressionReference.labelName("different");
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
    public void testSetReferenceSame() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        assertSame(mapping, mapping.setReference(REFERENCE));
    }

    @Test
    public void testSetReferenceDifferent() {
        final SpreadsheetLabelMapping mapping = this.createObject();
        final ExpressionReference differentReference = cell(999);
        final SpreadsheetLabelMapping different = mapping.setReference(differentReference);

        assertNotSame(mapping, different);
        this.checkLabel(different, LABEL);
        this.checkReference(different, differentReference);
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testJsonNodeUnmarshallEmptyObjectFails() {
        this.unmarshallFails(JsonNode.object(), JsonNodeException.class);
    }

    // HateosResource....................................................................................................

    @Test
    public void testHateosLinkId() {
        final String text = "ABC12345678";
        this.hateosLinkIdAndCheck(SpreadsheetLabelMapping.with(SpreadsheetLabelName.with(text), SpreadsheetExpressionReference.parseCellReference("A1")),
                text);
    }

    // equals...........................................................................................................

    @Test
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(SpreadsheetLabelMapping.with(SpreadsheetExpressionReference.labelName("different"), REFERENCE));
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
    public SpreadsheetLabelMapping createObject() {
        return SpreadsheetLabelMapping.with(LABEL, REFERENCE);
    }

    private void checkLabel(final SpreadsheetLabelMapping mapping, final SpreadsheetLabelName label) {
        assertEquals(label, mapping.label(), "label");
    }

    private void checkReference(final SpreadsheetLabelMapping mapping, final ExpressionReference reference) {
        assertEquals(reference, mapping.reference(), "reference");
    }

    private static ExpressionReference cell(final int column) {
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
    public SpreadsheetLabelMapping createJsonNodeMappingValue() {
        return this.createObject();
    }

    // HateosResourceTesting............................................................................................

    @Override
    public SpreadsheetLabelMapping createHateosResource() {
        return this.createObject();
    }
}
