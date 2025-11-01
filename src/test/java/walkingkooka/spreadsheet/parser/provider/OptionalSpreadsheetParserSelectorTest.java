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

package walkingkooka.spreadsheet.parser.provider;

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

public final class OptionalSpreadsheetParserSelectorTest implements ClassTesting<OptionalSpreadsheetParserSelector>,
    HashCodeEqualsDefinedTesting2<OptionalSpreadsheetParserSelector>,
    ToStringTesting<OptionalSpreadsheetParserSelector>,
    JsonNodeMarshallingTesting<OptionalSpreadsheetParserSelector> {
    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> OptionalSpreadsheetParserSelector.with(null)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
            OptionalSpreadsheetParserSelector.EMPTY,
            OptionalSpreadsheetParserSelector.with(
                Optional.empty()
            )
        );
    }

    @Test
    public void testWithNotEmpty() {
        final Optional<SpreadsheetParserSelector> selector = Optional.of(
            SpreadsheetParserName.DATE.setValueText("yyyy/mm/ddd")
        );

        final OptionalSpreadsheetParserSelector optional = OptionalSpreadsheetParserSelector.with(selector);

        assertSame(
            selector,
            optional.value()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
            OptionalSpreadsheetParserSelector.with(
                Optional.of(
                    SpreadsheetParserSelector.parse("different")
                )
            )
        );
    }

    @Override
    public OptionalSpreadsheetParserSelector createObject() {
        return this.createJsonNodeMarshallingValue();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final Optional<SpreadsheetParserSelector> selector = Optional.of(
            SpreadsheetParserSelector.parse("parser123")
        );
        this.toStringAndCheck(
            OptionalSpreadsheetParserSelector.with(selector),
            selector.toString()
        );
    }

    // json..............................................................................................................

    @Test
    public void testJsonMarshallEmpty() {
        this.marshallAndCheck(
            OptionalSpreadsheetParserSelector.EMPTY,
            JsonNode.nullNode()
        );
    }

    @Test
    public void testJsonMarshallNotEmpty() {
        this.marshallAndCheck(
            this.createJsonNodeMarshallingValue(),
            JsonNode.string("date yyyy/mm/ddd")
        );
    }

    @Test
    public void testJsonRoundtripEmpty() {
        this.marshallRoundTripTwiceAndCheck(
            OptionalSpreadsheetParserSelector.EMPTY
        );
    }


    @Test
    public void testJsonRoundtripNotEmpty() {
        this.marshallRoundTripTwiceAndCheck(
            this.createJsonNodeMarshallingValue()
        );
    }

    @Override
    public OptionalSpreadsheetParserSelector unmarshall(final JsonNode json,
                                                        final JsonNodeUnmarshallContext context) {
        return OptionalSpreadsheetParserSelector.unmarshall(
            json,
            context
        );
    }

    @Override
    public OptionalSpreadsheetParserSelector createJsonNodeMarshallingValue() {
        return OptionalSpreadsheetParserSelector.with(
            Optional.of(
                SpreadsheetParserSelector.parse("date yyyy/mm/ddd")
            )
        );
    }

    // class............................................................................................................

    @Override
    public Class<OptionalSpreadsheetParserSelector> type() {
        return OptionalSpreadsheetParserSelector.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
