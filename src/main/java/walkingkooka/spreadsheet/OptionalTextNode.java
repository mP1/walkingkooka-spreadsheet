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

import walkingkooka.CanBeEmpty;
import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.TextNode;

import java.util.Objects;
import java.util.Optional;

/**
 * A typed Optional {@link TextNode}. This is especially necessary because generics are erased and it is not possible
 * to test and distinguish between empty Optionals of different values.
 */
public final class OptionalTextNode implements Value<Optional<TextNode>>,
        CanBeEmpty,
        TreePrintable {

    public final static OptionalTextNode EMPTY = new OptionalTextNode(Optional.empty());

    static {
        SpreadsheetValueType.DATE.isEmpty();

        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(OptionalTextNode.class),
                OptionalTextNode::unmarshall,
                OptionalTextNode::marshall,
                OptionalTextNode.class
        );
    }

    private final Optional<TextNode> value;

    private OptionalTextNode(final Optional<TextNode> value) {
        this.value = value;
    }

    public static OptionalTextNode with(final Optional<TextNode> value) {
        Objects.requireNonNull(value, "value");

        return value.isPresent() ?
                new OptionalTextNode(value) :
                Cast.to(EMPTY);
    }

    // TextNode...........................................................................................................

    static OptionalTextNode unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        return with(
                Optional.ofNullable(
                        context.unmarshallWithType(node)
                )
        );
    }

    @Override
    public Optional<TextNode> value() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof OptionalTextNode &&
                        this.equals0(Cast.to(other));
    }

    // json.............................................................................................................

    private boolean equals0(final OptionalTextNode other) {
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallWithType(
                this.value.orElse(null)
        );
    }

    // CanBeEmpty.......................................................................................................

    @Override
    public boolean isEmpty() {
        return !this.value.isPresent();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());
        printer.indent();
        {
            final Optional<TextNode> value = this.value;
            if(value.isPresent()) {
                value.get()
                        .printTree(printer);
            }
        }
        printer.outdent();
    }
}
