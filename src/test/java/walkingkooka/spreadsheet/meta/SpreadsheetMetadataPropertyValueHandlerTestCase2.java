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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetMetadataPropertyValueHandlerTestCase2<P extends SpreadsheetMetadataPropertyValueHandler<T>, T> extends SpreadsheetMetadataPropertyValueHandlerTestCase<P>
        implements ToStringTesting<P> {

    SpreadsheetMetadataPropertyValueHandlerTestCase2() {
        super();
    }

    @Test
    public final void testCheckNullValueFails() {
        this.checkFails(null,
                "Missing value " + CharSequences.quote(this.propertyName().value()));
    }

    @Test
    public final void testCheckInvalidValueFails() {
        this.checkFails(this,
                "Expected " + this.propertyValueType() + " but got " + this + " (" + this.getClass().getSimpleName() + ") in " + CharSequences.quote(this.propertyName().value()));
    }

    @Test
    public final void testCheckInvalidValueFails2() {
        final StringBuilder value = new StringBuilder("123abc");
        this.checkFails(value,
                "Expected " + this.propertyValueType() + " but got \"123abc\" (java.lang.StringBuilder) in " + CharSequences.quote(this.propertyName().value()));
    }

    @Test
    public final void testCheck() {
        this.check(this.propertyValue());
    }

    @Test
    public final void testRoundtripJson() {
        final T value = this.propertyValue();
        final P handler = this.handler();

        final JsonNode json = handler.marshall(value, this.marshallContext());

        assertEquals(value,
                handler.unmarshall(json, this.propertyName(), this.unmarshallContext()),
                () -> "value " + CharSequences.quoteIfChars(value) + " to json " + json);
    }

    @Test
    public final void testToString() {
        this.toStringAndCheck(this.handler(), this.expectedToString());
    }

    abstract String expectedToString();

    final void check(final Object value) {
        final SpreadsheetMetadataPropertyName<?> propertyName = this.propertyName();
        this.handler().check(value, propertyName);
        propertyName.checkValue(value);
    }

    final void checkFails(final Object value, final String message) {
        final SpreadsheetMetadataPropertyName<?> propertyName = this.propertyName();

        final SpreadsheetMetadataPropertyValueException thrown = assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> this.check(value));
        this.checkSpreadsheetMetadataPropertyValueException(thrown, message, propertyName, value);

        final SpreadsheetMetadataPropertyValueException thrown2 = assertThrows(SpreadsheetMetadataPropertyValueException.class, () -> propertyName.checkValue(value));
        this.checkSpreadsheetMetadataPropertyValueException(thrown2, message, propertyName, value);
    }

    private void checkSpreadsheetMetadataPropertyValueException(final SpreadsheetMetadataPropertyValueException thrown,
                                                                final String message,
                                                                final SpreadsheetMetadataPropertyName<?> propertyName,
                                                                final Object value) {
        if (null != message) {
            assertEquals(message, thrown.getMessage(), "message");
        }
        assertEquals(propertyName, thrown.name(), "propertyName");
        assertEquals(value, thrown.value(), "value");
    }

    final void unmarshallAndCheck(final JsonNode node, final T value) {
        assertEquals(value,
                this.handler().unmarshall(node, this.propertyName(), this.unmarshallContext()),
                () -> "from JsonNode " + node);
    }

    final void marshallAndCheck(final T value, final JsonNode node) {
        assertEquals(node,
                this.handler().marshall(value, this.marshallContext()),
                () -> "marshall " + CharSequences.quoteIfChars(value));
    }

    // helper...........................................................................................................

    abstract P handler();

    abstract SpreadsheetMetadataPropertyName<T> propertyName();

    abstract T propertyValue();

    abstract String propertyValueType();

    final JsonNodeUnmarshallContext unmarshallContext() {
        return JsonNodeUnmarshallContexts.basic();
    }

    final JsonNodeMarshallContext marshallContext() {
        return JsonNodeMarshallContexts.basic();
    }

    final JsonNode marshall(final Object value) {
        return this.marshallContext().marshall(value);
    }
}
