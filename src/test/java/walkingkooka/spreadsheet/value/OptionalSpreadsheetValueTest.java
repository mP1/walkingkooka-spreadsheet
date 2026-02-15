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

package walkingkooka.spreadsheet.value;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelector;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class OptionalSpreadsheetValueTest implements ClassTesting<OptionalSpreadsheetValue<?>>,
    HashCodeEqualsDefinedTesting2<OptionalSpreadsheetValue<?>>,
    ToStringTesting<OptionalSpreadsheetValue<?>>,
    JsonNodeMarshallingTesting<OptionalSpreadsheetValue<?>> {
    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> OptionalSpreadsheetValue.with(null)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            OptionalSpreadsheetValue.EMPTY,
            OptionalSpreadsheetValue.with(
                Optional.empty()
            )
        );
    }

    @Test
    public void testWithNotEmpty() {
        final Optional<String> value = Optional.of(
            "Hello123"
        );

        final OptionalSpreadsheetValue<?> optional = OptionalSpreadsheetValue.with(value);

        assertSame(
            value,
            optional.value()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
            OptionalSpreadsheetValue.with(
                Optional.of(
                    SpreadsheetFormatterSelector.parse("different")
                )
            )
        );
    }

    @Override
    public OptionalSpreadsheetValue<?> createObject() {
        return this.createJsonNodeMarshallingValue();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final String value = "Hello123";

        this.toStringAndCheck(
            OptionalSpreadsheetValue.with(
                Optional.of(value)
            ),
            CharSequences.quoteAndEscape(value)
                .toString()
        );
    }

    @Test
    public void testToStringWithEmpty() {
        this.toStringAndCheck(
            OptionalSpreadsheetValue.EMPTY,
            ""
        );
    }

    // json..............................................................................................................

    @Test
    public void testJsonMarshallEmpty() {
        this.marshallAndCheck(
            OptionalSpreadsheetValue.EMPTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testJsonMarshallNotEmpty() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            JsonNode.string("Hello123")
        );
    }

    @Test
    public void testJsonRoundtripEmpty() {
        this.marshallRoundTripTwiceAndCheck(
            OptionalSpreadsheetValue.EMPTY
        );
    }

    @Test
    public void testJsonRoundtripExpressionDate() {
        this.marshallRoundTripTwiceAndCheck(
            OptionalSpreadsheetValue.with(
                Optional.of(
                    LocalDate.of(1999, 12, 31)
                )
            )
        );
    }

    @Test
    public void testJsonRoundtripExpressionNumber() {
        this.marshallRoundTripTwiceAndCheck(
            OptionalSpreadsheetValue.with(
                Optional.of(
                    this.unmarshallContext()
                        .expressionNumberKind()
                        .create(12.5)
                )
            )
        );
    }

    @Test
    public void testJsonRoundtripString() {
        this.marshallRoundTripTwiceAndCheck(
            OptionalSpreadsheetValue.with(
                Optional.of(
                    "Hello123"
                )
            )
        );
    }

    @Override
    public OptionalSpreadsheetValue<?> unmarshall(final JsonNode json,
                                                  final JsonNodeUnmarshallContext context) {
        return OptionalSpreadsheetValue.unmarshall(
            json,
            context
        );
    }

    @Override
    public OptionalSpreadsheetValue<?> createJsonNodeMarshallingValue() {
        return OptionalSpreadsheetValue.with(
            Optional.of(
                "Hello123"
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<OptionalSpreadsheetValue<?>> type() {
        return Cast.to(OptionalSpreadsheetValue.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
