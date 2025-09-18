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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class OptionalSpreadsheetFormatterSelectorTest implements ClassTesting<OptionalSpreadsheetFormatterSelector>,
    HashCodeEqualsDefinedTesting2<OptionalSpreadsheetFormatterSelector>,
    ToStringTesting<OptionalSpreadsheetFormatterSelector>,
    JsonNodeMarshallingTesting<OptionalSpreadsheetFormatterSelector> {
    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> OptionalSpreadsheetFormatterSelector.with(null)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            OptionalSpreadsheetFormatterSelector.EMPTY,
            OptionalSpreadsheetFormatterSelector.with(
                Optional.empty()
            )
        );
    }

    @Test
    public void testWithNotEmpty() {
        final Optional<SpreadsheetFormatterSelector> selector = Optional.of(
            SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT
        );

        final OptionalSpreadsheetFormatterSelector optional = OptionalSpreadsheetFormatterSelector.with(selector);

        assertSame(
            selector,
            optional.value()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
            OptionalSpreadsheetFormatterSelector.with(
                Optional.of(
                    SpreadsheetFormatterSelector.parse("different")
                )
            )
        );
    }

    @Override
    public OptionalSpreadsheetFormatterSelector createObject() {
        return this.createJsonNodeMarshallingValue();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final Optional<SpreadsheetFormatterSelector> selector = Optional.of(
            SpreadsheetFormatterSelector.parse("different")
        );
        this.toStringAndCheck(
            OptionalSpreadsheetFormatterSelector.with(selector),
            selector.toString()
        );
    }

    // json..............................................................................................................

    @Test
    public void testJsonMarshallEmpty() {
        this.marshallAndCheck(
            OptionalSpreadsheetFormatterSelector.EMPTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testJsonMarshallNotEmpty() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            JsonNode.string("text-format-pattern @")
        );
    }

    @Test
    public void testJsonRoundtripEmpty() {
        this.marshallRoundTripTwiceAndCheck(
            OptionalSpreadsheetFormatterSelector.EMPTY
        );
    }


    @Test
    public void testJsonRoundtripNotEmpty() {
        this.marshallRoundTripTwiceAndCheck(
            this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public OptionalSpreadsheetFormatterSelector unmarshall(final JsonNode json,
                                                           final JsonNodeUnmarshallContext context) {
        return OptionalSpreadsheetFormatterSelector.unmarshall(
            json,
            context
        );
    }

    @Override
    public OptionalSpreadsheetFormatterSelector createJsonNodeMarshallingValue() {
        return OptionalSpreadsheetFormatterSelector.with(
            Optional.of(
                SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<OptionalSpreadsheetFormatterSelector> type() {
        return OptionalSpreadsheetFormatterSelector.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
