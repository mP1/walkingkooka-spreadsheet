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
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.test.ParseStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetLabelMappingExpressionReferenceTest implements ClassTesting2<SpreadsheetLabelMappingExpressionReference>,
        JsonNodeMarshallingTesting<SpreadsheetLabelMappingExpressionReference>,
        ParseStringTesting<SpreadsheetLabelMappingExpressionReference> {

    // unmarshall.....................................................................................................

    @Test
    public void testJsonRoundtripCellReference() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetExpressionReference.parseCellReference("A1"));
    }

    @Test
    public void testJsonRoundtripLabel() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetExpressionReference.labelName("Label123"));
    }

    @Test
    public void testJsonRoundtripRange() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetExpressionReference.parseRange("B2:C3"));
    }

    // parse............................................................................................................

    @Test
    public void testParseCellReferenceUpperCaseRelativeRelative() {
        final String reference = "A2";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.RELATIVE.column(0).setRow(SpreadsheetReferenceKind.RELATIVE.row(1)));
    }

    @Test
    public void testParseCellReferenceUpperCaseRelativeAbsolute() {
        final String reference = "C$4";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.RELATIVE.column(2).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(3)));
    }

    @Test
    public void testParseCellReferenceUpperCaseAbsoluteRelative() {
        final String reference = "$E6";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.ABSOLUTE.column(4).setRow(SpreadsheetReferenceKind.RELATIVE.row(5)));
    }

    @Test
    public void testParseCellReferenceUpperCaseAbsoluteAbsolute() {
        final String reference = "$G$8";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.ABSOLUTE.column(6).setRow(SpreadsheetReferenceKind.ABSOLUTE.row(7)));
    }

    @Test
    public void testParseCellReferenceLowercaseRelativeRelative() {
        final String reference = "i10";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.RELATIVE.column(8).setRow(SpreadsheetReferenceKind.RELATIVE.row(9)));
    }

    @Test
    public void testParseCellReferenceLowercaseAbsolute() {
        final String reference = "$k12";
        this.parseStringAndCheck(reference, SpreadsheetReferenceKind.ABSOLUTE.column(10).setRow(SpreadsheetReferenceKind.RELATIVE.row(11)));
    }

    @Test
    public void testParseLabel() {
        final String label = "label123";
        this.parseStringAndCheck(label, SpreadsheetExpressionReference.labelName(label));
    }

    @Test
    public void testParseRange() {
        final String range = "A2:B2";
        this.parseStringAndCheck(range, SpreadsheetExpressionReference.parseRange(range));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetLabelMappingExpressionReference> type() {
        return SpreadsheetLabelMappingExpressionReference.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeTesting...................................................................................................

    @Override
    public SpreadsheetLabelMappingExpressionReference unmarshall(final JsonNode node,
                                                                 final JsonNodeUnmarshallContext context) {
        return SpreadsheetExpressionReference.unmarshallSpreadsheetLabelMappingExpressionReference(node, context);
    }

    @Override
    public SpreadsheetLabelMappingExpressionReference createJsonNodeMappingValue() {
        return SpreadsheetExpressionReference.parseSpreadsheetLabelMappingExpressionReference("A1");
    }

    // ParseStringTesting...............................................................................................

    @Override
    public SpreadsheetLabelMappingExpressionReference parseString(final String text) {
        return SpreadsheetExpressionReference.parseSpreadsheetLabelMappingExpressionReference(text);
    }

    @Override
    public Class<? extends RuntimeException> parseStringFailedExpected(final Class<? extends RuntimeException> throwing) {
        return throwing;
    }

    @Override
    public RuntimeException parseStringFailedExpected(final RuntimeException expected) {
        return expected;
    }
}
