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
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.Whitespace;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

import java.util.Objects;

/**
 * An error for an individual cell or formula which may be a parsing or execution error.
 */
public final class SpreadsheetError implements HashCodeEqualsDefined,
        Value<String>,
        HasJsonNode {

    public static SpreadsheetError with(final String message) {
        Whitespace.failIfNullOrEmptyOrWhitespace(message, "Message");

        return new SpreadsheetError(message);
    }

    private SpreadsheetError(final String message) {
        this.message = message;
    }

    @Override
    public String value() {
        return this.message;
    }

    /**
     * The error message text.
     */
    private final String message;

    @Override
    public int hashCode() {
        return this.message.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetError &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetError error) {
        return this.message.equals(error.message);
    }

    @Override
    public String toString() {
        return this.message;
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link SpreadsheetError} from a {@link JsonNode}.
     */
    static SpreadsheetError fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        return with(node.stringValueOrFail());
    }

    @Override
    public JsonNode toJsonNode() {
        return JsonNode.string(this.message);
    }

    static {
        HasJsonNode.register("spreadsheet-error",
                SpreadsheetError::fromJsonNode,
                SpreadsheetError.class);
    }
}
