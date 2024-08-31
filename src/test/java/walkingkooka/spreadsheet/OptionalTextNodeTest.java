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
import walkingkooka.Cast;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class OptionalTextNodeTest implements ClassTesting<OptionalTextNode>,
        HashCodeEqualsDefinedTesting2<OptionalTextNode>,
        ToStringTesting<OptionalTextNode>,
        JsonNodeMarshallingTesting<OptionalTextNode>,
        TreePrintableTesting {

    // with.............................................................................................................

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> OptionalTextNode.with(null)
        );
    }

    @Test
    public void testWithEmpty() {
        assertSame(
                OptionalTextNode.EMPTY,
                OptionalTextNode.with(
                        Optional.empty()
                )
        );
    }

    @Test
    public void testWithNotEmpty() {
        final Optional<TextNode> value = Optional.of(
                TextNode.text("Text123")
        );

        final OptionalTextNode optional = OptionalTextNode.with(value);

        assertSame(
                value,
                optional.value()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferent() {
        this.checkNotEquals(
                OptionalTextNode.with(
                        Optional.of(
                                TextNode.text("different")
                        )
                )
        );
    }

    @Override
    public OptionalTextNode createObject() {
        return this.createJsonNodeMarshallingValue();
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final Optional<TextNode> value = Optional.of(
                TextNode.text("Hello123")
        );

        this.toStringAndCheck(
                OptionalTextNode.with(value),
                value.toString()
        );
    }

    // json..............................................................................................................

    @Test
    public void testJsonMarshallEmpty() {
        this.marshallAndCheck(
                OptionalTextNode.EMPTY,
                JsonNode.nullNode()
        );
    }

    @Test
    public void testJsonRoundtripEmpty() {
        this.marshallRoundTripTwiceAndCheck(
                OptionalTextNode.EMPTY
        );
    }

    @Test
    public void testJsonRoundtripTextNode() {
        this.marshallRoundTripTwiceAndCheck(
                OptionalTextNode.with(
                        Optional.of(
                                TextNode.text("Hello123")
                        )
                )
        );
    }

    @Override
    public OptionalTextNode unmarshall(final JsonNode json,
                                       final JsonNodeUnmarshallContext context) {
        return OptionalTextNode.unmarshall(
                json,
                context
        );
    }

    @Override
    public OptionalTextNode createJsonNodeMarshallingValue() {
        return OptionalTextNode.with(
                Optional.of(
                        TextNode.text("Hello123")
                )
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrintableEmpty() {
        this.treePrintAndCheck(
                OptionalTextNode.EMPTY,
                "OptionalTextNode\n"
        );
    }

    @Test
    public void testTreePrintable() {
        this.treePrintAndCheck(
                OptionalTextNode.with(
                        Optional.of(
                                TextNode.text("Hello123")
                        )
                ),
                "OptionalTextNode\n" +
                        "  Text \"Hello123\"\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<OptionalTextNode> type() {
        return Cast.to(OptionalTextNode.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
