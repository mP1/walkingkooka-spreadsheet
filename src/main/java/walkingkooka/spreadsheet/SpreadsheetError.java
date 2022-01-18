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

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.text.CharSequences;
import walkingkooka.text.Whitespace;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

/**
 * An error for an individual cell or formula which may be a parsing or execution error.
 */
public final class SpreadsheetError implements Value<String>,
        TreePrintable {

    public static SpreadsheetError with(final SpreadsheetErrorKind kind,
                                        final String message) {
        Objects.requireNonNull(kind, "kind");
        Whitespace.failIfNullOrEmptyOrWhitespace(message, "Message");

        return new SpreadsheetError(
                kind,
                message
        );
    }

    private SpreadsheetError(final SpreadsheetErrorKind kind,
                             final String message) {
        this.kind = kind;
        this.message = message;
    }

    public SpreadsheetErrorKind kind() {
        return this.kind;
    }

    private final SpreadsheetErrorKind kind;

    @Override
    public String value() {
        return this.message;
    }

    /**
     * The error message text.
     */
    private final String message;

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(
                this.kind() + " " + CharSequences.quoteAndEscape(this.value())
        );
    }

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.kind,
                this.message
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetError &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetError error) {
        return this.kind == error.kind &&
                this.message.equals(error.message);
    }

    @Override
    public String toString() {
        return this.kind + " " + CharSequences.quoteAndEscape(this.message);
    }

    // JsonNodeContext..................................................................................................

    static SpreadsheetError unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        SpreadsheetErrorKind kind = null;
        String message = null;

        for (final JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case KIND_PROPERTY_STRING:
                    kind = SpreadsheetErrorKind.valueOf(child.stringOrFail());
                    break;
                case MESSAGE_PROPERTY_STRING:
                    message = child.stringOrFail();
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == kind) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(KIND_PROPERTY, node);
        }
        if (null == message) {
            JsonNodeUnmarshallContext.requiredPropertyMissing(MESSAGE_PROPERTY, node);
        }

        return new SpreadsheetError(
                kind,
                message
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(KIND_PROPERTY, JsonNode.string(this.kind.name()))
                .set(MESSAGE_PROPERTY, JsonNode.string(this.message));
    }

    private final static String KIND_PROPERTY_STRING = "kind";
    private final static String MESSAGE_PROPERTY_STRING = "message";

    // @VisibleForTesting

    final static JsonPropertyName KIND_PROPERTY = JsonPropertyName.with(KIND_PROPERTY_STRING);
    final static JsonPropertyName MESSAGE_PROPERTY = JsonPropertyName.with(MESSAGE_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetError.class),
                SpreadsheetError::unmarshall,
                SpreadsheetError::marshall,
                SpreadsheetError.class
        );
    }
}
