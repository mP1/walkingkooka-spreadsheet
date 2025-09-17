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

package walkingkooka.spreadsheet.convert.provider;

import walkingkooka.Value;
import walkingkooka.text.CharSequences;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * Captures an unsupported value and type, with type being the class name, because GWT doesnt support {@link Class} serialization.
 */
public final class MissingConverterValue implements Value<Object>, TreePrintable {

    public static MissingConverterValue with(final Object value,
                                             final String type) {
        return new MissingConverterValue(
            value,
            CharSequences.failIfNullOrEmpty(type, "type")
        );
    }

    private MissingConverterValue(final Object value,
                                  final String type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public Object value() {
        return this.value;
    }

    private final Object value;

    public String type() {
        return this.type;
    }

    private final String type;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.value,
            this.type
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof MissingConverterValue && this.equals0((MissingConverterValue) other);
    }

    private boolean equals0(final MissingConverterValue other) {
        return Objects.equals(this.value, other.value) &&
            this.type == other.type;
    }

    @Override
    public String toString() {
        return CharSequences.quoteIfChars(this.value) + " " + this.type;
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        TreePrintable.printTreeOrToString(
            this.value,
            printer
        );
        printer.indent();
        {
            printer.println(this.type);
        }
        printer.outdent();
    }

    // json.............................................................................................................

    static MissingConverterValue unmarshall(final JsonNode node,
                                            final JsonNodeUnmarshallContext context) {
        Object value = null;
        String type = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case VALUE_PROPERTY_STRING:
                    value = context.unmarshallWithType(child);
                    break;
                case TYPE_PROPERTY_STRING:
                    type = context.unmarshall(
                        child,
                        String.class
                    );
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        return with(
            value,
            type
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        JsonObject object = JsonNode.object();

        final Object value = this.value;
        if (null != value) {
            object = object.set(
                VALUE_PROPERTY,
                context.marshallWithType(value)
            );
        }

        return object.set(
            TYPE_PROPERTY,
            context.marshall(this.type)
        );
    }

    private final static String VALUE_PROPERTY_STRING = "value";
    final static JsonPropertyName VALUE_PROPERTY = JsonPropertyName.with(VALUE_PROPERTY_STRING);

    private final static String TYPE_PROPERTY_STRING = "type";
    final static JsonPropertyName TYPE_PROPERTY = JsonPropertyName.with(TYPE_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(MissingConverterValue.class),
            MissingConverterValue::unmarshall,
            MissingConverterValue::marshall,
            MissingConverterValue.class
        );
    }
}
