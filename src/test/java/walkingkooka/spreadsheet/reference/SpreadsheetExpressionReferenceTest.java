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
import walkingkooka.tree.expression.function.ExpressionFunctionParameterName;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetExpressionReferenceTest implements ClassTesting2<SpreadsheetExpressionReference>,
    JsonNodeMarshallingTesting<SpreadsheetExpressionReference> {

    // testParameterName...............................................................................................

    @Test
    public void testTestParameterNameLabelEqualSameCase() {
        final String label = "Label123";

        this.testParameterNameAndCheck(
            SpreadsheetSelection.labelName(label),
            label,
            true
        );
    }

    @Test
    public void testTestParameterNameLabelEqualDifferentCase() {
        this.testParameterNameAndCheck(
            SpreadsheetSelection.labelName("Label123"),
            "LABEL123",
            true
        );
    }

    @Test
    public void testTestParameterNameLabelDifferent() {
        this.testParameterNameAndCheck(
            SpreadsheetSelection.labelName("Label123"),
            "different",
            false
        );
    }

    @Test
    public void testTestParameterNameCell() {
        this.testParameterNameAndCheck(
            SpreadsheetSelection.A1,
            "different",
            false
        );
    }

    @Test
    public void testTestParameterNameCellRange() {
        this.testParameterNameAndCheck(
            SpreadsheetSelection.parseCellRange("A1:B2"),
            "different",
            false
        );
    }

    private void testParameterNameAndCheck(final SpreadsheetExpressionReference expressionReference,
                                           final String parameterName,
                                           final boolean expected) {
        this.checkEquals(
            expected,
            expressionReference.testParameterName(
                ExpressionFunctionParameterName.with(parameterName)
            ),
            () -> expressionReference + " testParameterName(" + parameterName + ")"
        );
    }

    // unmarshall.....................................................................................................

    @Test
    public void testUnmarshallWithCellReference() {
        final String reference = "A1";
        this.checkEquals(SpreadsheetSelection.parseCell(reference),
            SpreadsheetExpressionReference.unmarshallExpressionReference(JsonNode.string(reference), this.unmarshallContext())
        );
    }

    @Test
    public void testUnmarshallWithLabel() {
        final String label = "label123";
        this.checkEquals(SpreadsheetSelection.labelName(label),
            SpreadsheetExpressionReference.unmarshallExpressionReference(JsonNode.string(label), this.unmarshallContext())
        );
    }

    @Test
    public void testJsonRoundtripCellReference() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetSelection.A1);
    }

    @Test
    public void testJsonRoundtripLabel() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetSelection.labelName("Label123"));
    }

    @Test
    public void testJsonRoundtripRange() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetSelection.parseCellRange("B2:C3"));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetExpressionReference> type() {
        return SpreadsheetExpressionReference.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeTesting...................................................................................................

    @Override
    public SpreadsheetExpressionReference unmarshall(final JsonNode node,
                                                     final JsonNodeUnmarshallContext context) {
        return SpreadsheetExpressionReference.unmarshallExpressionReference(node, context);
    }

    @Override
    public SpreadsheetExpressionReference createJsonNodeMarshallingValue() {
        return SpreadsheetSelection.parseExpressionReference("A1");
    }
}
