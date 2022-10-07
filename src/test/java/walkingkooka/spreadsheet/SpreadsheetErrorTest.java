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
import walkingkooka.spreadsheet.engine.FakeSpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetErrorTest implements ClassTesting2<SpreadsheetError>,
        HashCodeEqualsDefinedTesting2<SpreadsheetError>,
        JsonNodeMarshallingTesting<SpreadsheetError>,
        TreePrintableTesting,
        ToStringTesting<SpreadsheetError> {

    private final static SpreadsheetErrorKind KIND = SpreadsheetErrorKind.NA;
    private final static String MESSAGE = "message #1";
    private final static Optional<?> VALUE = Optional.of(
            123
    );

    @Test
    public void testWithNullMessageFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetError.with(KIND, null, VALUE)
        );
    }

    @Test
    public void testWithNullValueFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetError.with(KIND, MESSAGE, null)
        );
    }

    @Test
    public void testWith() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, MESSAGE, VALUE);
        this.checkKind(error, KIND);
        this.checkMessage(error, MESSAGE);
        this.checkValue(error, VALUE);
    }

    @Test
    public void testWithEmptyMessage() {
        final SpreadsheetError error = SpreadsheetError.with(KIND, "", VALUE);
        this.checkKind(error, KIND);
        this.checkMessage(error, "");
        this.checkValue(error, VALUE);
    }

    // notFound........................................................................................................

    @Test
    public void testNotFoundWithCell() {
        final SpreadsheetCellReference cell = SpreadsheetSelection.parseCell("A99");

        final SpreadsheetError error = SpreadsheetError.notFound(cell);
        this.checkKind(error, SpreadsheetErrorKind.NAME);
        this.checkMessage(error, "Cell not found: A99");
        this.checkValue(error, Optional.of(cell));
    }

    @Test
    public void testNotFoundWithLabel() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        final SpreadsheetError error = SpreadsheetError.notFound(label);
        this.checkKind(error, SpreadsheetErrorKind.NAME);
        this.checkMessage(error, "Label not found: Label123");
        this.checkValue(error, Optional.of(label));
    }

    // isMissingCell....................................................................................................

    @Test
    public void testIsMissingCellDIV0() {
        this.isMissingCellAndCheck(
                SpreadsheetErrorKind.DIV0.setMessage("Ignored"),
                false
        );
    }

    @Test
    public void testIsMissingCellDIV0WithValue() {
        this.isMissingCellAndCheck(
                SpreadsheetErrorKind.DIV0.setMessageAndValue(
                        "Ignored",
                        1
                ),
                false
        );
    }

    @Test
    public void testIsMissingCellNAMEWithCellReference() {
        this.isMissingCellAndCheck(
                SpreadsheetErrorKind.NAME.setMessageAndValue(
                        "Ignored",
                        SpreadsheetSelection.parseCell("A1")
                ),
                true
        );
    }

    @Test
    public void testIsMissingCellNAMEWithLabel() {
        this.isMissingCellAndCheck(
                SpreadsheetErrorKind.NAME.setMessageAndValue(
                        "Ignored",
                        SpreadsheetSelection.labelName("Label123")
                ),
                false
        );
    }

    private void isMissingCellAndCheck(final SpreadsheetError error,
                                       final boolean is) {
        this.checkEquals(
                is,
                error.isMissingCell(),
                () -> error + ".isMissingCell()"
        );
    }

    // replaceWithValueIfPossible......................................................................................

    @Test
    public void testReplaceWithValueIfPossibleWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetErrorKind.ERROR.setMessage("!!")
                        .replaceWithValueIfPossible(null)
        );
    }

    @Test
    public void testReplaceWithValueIfPossibleWithMissingCellBecomesZero() {
        final ExpressionNumberKind kind = ExpressionNumberKind.BIG_DECIMAL;

        this.replaceWithValueIfPossibleAndCheck(
                SpreadsheetError.notFound(SpreadsheetSelection.parseCell("A1")),
                new FakeSpreadsheetEngineContext() {
                    @Override
                    public SpreadsheetMetadata metadata() {
                        return SpreadsheetMetadata.EMPTY.defaults()
                                .set(SpreadsheetMetadataPropertyName.EXPRESSION_NUMBER_KIND, kind);
                    }
                },
                kind.zero()
        );
    }

    @Test
    public void testReplaceWithValueIfPossibleWithNotMissingCell() {
        this.replaceWithValueIfPossibleAndCheck(
                SpreadsheetErrorKind.DIV0.setMessage("!!")
        );
    }

    private void replaceWithValueIfPossibleAndCheck(final SpreadsheetError error) {
        this.replaceWithValueIfPossibleAndCheck(
                error,
                SpreadsheetEngineContexts.fake(),
                error
        );
    }

    private void replaceWithValueIfPossibleAndCheck(final SpreadsheetError error,
                                                    final SpreadsheetEngineContext context,
                                                    final Object expected) {
        this.checkEquals(
                expected,
                error.replaceWithValueIfPossible(context),
                () -> error + " replaceWithValueIfPossible"
        );
    }

    // TreePrintable...................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                this.createObject(),
                "#N/A\n" +
                        "  \"message #1\"\n" +
                        "  123"
        );
    }

    // equals..........................................................................................................

    @Test
    public void testEqualsDifferentKind() {
        this.checkNotEquals(
                SpreadsheetError.with(
                        SpreadsheetErrorKind.NAME,
                        MESSAGE,
                        VALUE
                )
        );
    }

    @Test
    public void testEqualsDifferentMessage() {
        this.checkNotEquals(
                SpreadsheetError.with(
                        KIND,
                        "different",
                        VALUE
                )
        );
    }

    @Test
    public void testEqualsMessageDifferentCase() {
        this.checkNotEquals(
                SpreadsheetError.with(
                        KIND,
                        MESSAGE.toUpperCase(),
                        VALUE
                )
        );
    }

    @Test
    public void testEqualsDifferentValue() {
        this.checkNotEquals(
                SpreadsheetError.with(
                        SpreadsheetErrorKind.NAME,
                        MESSAGE,
                        Optional.of("different-value")
                )
        );
    }

    // JsonNodeMarshallingTesting.......................................................................................

    @Test
    public void testUnmarshallStringInvalidFails() {
        this.unmarshallFails(JsonNode.string(""));
    }

    @Test
    public void testUnmarshallString() {
        this.unmarshallAndCheck(
                JsonNode.object()
                        .set(SpreadsheetError.KIND_PROPERTY, JsonNode.string(KIND.name()))
                        .set(SpreadsheetError.MESSAGE_PROPERTY, JsonNode.string(MESSAGE))
                        .set(SpreadsheetError.VALUE_PROPERTY, this.marshallContext().marshallWithType(VALUE.get())),
                SpreadsheetError.with(
                        KIND,
                        MESSAGE,
                        VALUE
                )
        );
    }

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
                this.createObject(),
                JsonNode.object()
                        .set(SpreadsheetError.KIND_PROPERTY, JsonNode.string(KIND.name()))
                        .set(SpreadsheetError.MESSAGE_PROPERTY, JsonNode.string(MESSAGE))
                        .set(SpreadsheetError.VALUE_PROPERTY, this.marshallContext().marshallWithType(VALUE.get()))
        );
    }

    @Test
    public void testMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    // toString...............................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createObject(),
                KIND + " \"" + MESSAGE + "\" " + VALUE.get()
        );
    }

    @Override
    public SpreadsheetError createObject() {
        return SpreadsheetError.with(
                KIND,
                MESSAGE,
                VALUE
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

    private void checkMessage(final SpreadsheetError error,
                              final String message) {
        this.checkEquals(
                message,
                error.message(),
                "message"
        );
    }

    private void checkValue(final SpreadsheetError error,
                            final Optional<?> value) {
        this.checkEquals(
                value,
                error.value(),
                "value"
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
