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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.HashCodeEqualsDefinedTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public final class SpreadsheetErrorTest implements ClassTesting2<SpreadsheetError>,
        HashCodeEqualsDefinedTesting2<SpreadsheetError>,
        JsonNodeMarshallingTesting<SpreadsheetError>,
        ToStringTesting<SpreadsheetError> {

    private final static String MESSAGE = "message #1";

    @Test
    public void testWithNullValueFails() {
        assertThrows(NullPointerException.class, () -> SpreadsheetError.with(null));
    }

    @Test
    public void testWithEmptyValueFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetError.with(""));
    }

    @Test
    public void testWithWhitespaceValueFails() {
        assertThrows(IllegalArgumentException.class, () -> SpreadsheetError.with(" \t"));
    }

    @Test
    public void testWith() {
        final SpreadsheetError error = SpreadsheetError.with(MESSAGE);
        this.checkValue(error, MESSAGE);
    }

    // equals...............................................................................................

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(SpreadsheetError.with("different"));
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkNotEquals(SpreadsheetError.with(MESSAGE.toUpperCase()));
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testJsonNodeUnmarshallStringInvalidFails() {
        this.unmarshallFails(JsonNode.string(""), IllegalArgumentException.class);
    }

    @Test
    public void testJsonNodeUnmarshallString() {
        this.unmarshallAndCheck(JsonNode.string(MESSAGE),
                SpreadsheetError.with(MESSAGE));
    }

    @Test
    public void testJsonNodeMarshall() {
        this.marshallAndCheck(this.createObject(), JsonNode.string(MESSAGE));
    }

    @Test
    public void testJsonNodeMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createObject(), MESSAGE);
    }

    @Override
    public SpreadsheetError createObject() {
        return SpreadsheetError.with(MESSAGE);
    }

    private void checkValue(final SpreadsheetError error, final String value) {
        assertEquals(value, error.value(), "error");
    }

    @Override
    public Class<SpreadsheetError> type() {
        return SpreadsheetError.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // JsonNodeMarshallingTesting...........................................................................................

    @Override
    public SpreadsheetError createJsonNodeMappingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetError unmarshall(final JsonNode jsonNode,
                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetError.unmarshall(jsonNode, context);
    }
}
