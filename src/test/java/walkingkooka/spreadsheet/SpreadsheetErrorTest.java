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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetErrorTest implements ClassTesting2<SpreadsheetError>,
        HashCodeEqualsDefinedTesting2<SpreadsheetError>,
        JsonNodeMarshallingTesting<SpreadsheetError>,
        TreePrintableTesting,
        ToStringTesting<SpreadsheetError> {

    private final static SpreadsheetErrorKind KIND = SpreadsheetErrorKind.NA;
    private final static String MESSAGE = "message #1";

    @Test
    public void testWithNullValueFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetError.with(KIND, null)
        );
    }

    @Test
    public void testWithWhitespaceValueFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetError.with(KIND, " \t")
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, MESSAGE);
        this.checkKind(error, KIND);
        this.checkValue(error, MESSAGE);
    }

    @Test
    public void testWithEmptyMessage() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, null);
        this.checkKind(error, KIND);
        this.checkValue(error, "");
    }

    // TreePrintable...................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                this.createObject(),
                "#NA \"message #1\"\n"
        );
    }

    // equals..........................................................................................................

    @Test
    public void testEqualsDifferentKind() {
        this.checkNotEquals(
                SpreadsheetError.with(
                        SpreadsheetErrorKind.NAME,
                        MESSAGE
                )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
                SpreadsheetError.with(
                        KIND,
                        "different"
                )
        );
    }

    @Test
    public void testEqualsDifferentCase() {
        this.checkNotEquals(
                SpreadsheetError.with(
                        KIND,
                        MESSAGE.toUpperCase()
                )
        );
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testJsonNodeUnmarshallStringInvalidFails() {
        this.unmarshallFails(JsonNode.string(""));
    }

    @Test
    public void testJsonNodeUnmarshallString() {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(JsonPropertyName.with("kind"), JsonNode.string(KIND.name()))
                        .set(JsonPropertyName.with("message"), JsonNode.string(MESSAGE)),
                SpreadsheetError.with(
                        KIND,
                        MESSAGE
                )
        );
    }

    @Test
    public void testJsonNodeMarshall() {
        this.marshallAndCheck(
                this.createObject(),
                JsonNode.object()
                        .set(SpreadsheetError.KIND_PROPERTY, JsonNode.string(KIND.name()))
                        .set(SpreadsheetError.MESSAGE_PROPERTY, JsonNode.string(MESSAGE))
        );
    }

    @Test
    public void testJsonNodeMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                KIND + " \"" + MESSAGE + "\""
        );
    }

    @Override
    public SpreadsheetError createObject() {
        return SpreadsheetError.with(
                KIND,
                MESSAGE
        );
    }

    private void checkKind(final SpreadsheetError error,
                           final SpreadsheetErrorKind kind) {
        this.checkEquals(
                kind,
                error.kind(),
                "kind"
        );

        this.checkEquals(
                kind,
                error.spreadsheetErrorKind(),
                "spreadsheetErrorKind"
        );
    }

    private void checkValue(final SpreadsheetError error,
                            final String value) {
        this.checkEquals(
                value,
                error.value(),
                "error"
        );
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
    public SpreadsheetError createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    @Override
    public SpreadsheetError unmarshall(final JsonNode jsonNode,
                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetError.unmarshall(jsonNode, context);
    }
}
