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
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetExpressionReferenceTest implements ClassTesting2<SpreadsheetExpressionReference>,
        JsonNodeMarshallingTesting<SpreadsheetExpressionReference> {

    // unmarshall.....................................................................................................

    @Test
    public void testJsonNodeUnmarshallWithCellReference() {
        final String reference = "A1";
        assertEquals(SpreadsheetSelection.parseCell(reference),
                SpreadsheetExpressionReference.unmarshallExpressionReference(JsonNode.string(reference), this.unmarshallContext())
        );
    }

    @Test
    public void testJsonNodeUnmarshallWithLabel() {
        final String label = "label123";
        assertEquals(SpreadsheetExpressionReference.labelName(label),
                SpreadsheetExpressionReference.unmarshallExpressionReference(JsonNode.string(label), this.unmarshallContext())
        );
    }

    @Test
    public void testJsonRoundtripCellReference() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetSelection.parseCell("A1"));
    }

    @Test
    public void testJsonRoundtripLabel() {
        this.marshallRoundTripTwiceAndCheck(SpreadsheetExpressionReference.labelName("Label123"));
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
