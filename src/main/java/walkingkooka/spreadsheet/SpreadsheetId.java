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
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

/**
 * Identifies a single spreadsheet.
 */
public final class SpreadsheetId implements Comparable<SpreadsheetId>,
        HashCodeEqualsDefined,
        HasJsonNode,
        HateosResource<Long>,
        Value<Long> {

    /**
     * Parses some text into a {@link SpreadsheetId}. This is the inverse of {@link SpreadsheetId#toString()}.
     */
    public static SpreadsheetId parse(final String text) {
        CharSequences.failIfNullOrEmpty(text, "text");

        try {
            return new SpreadsheetId(Long.parseLong(text, 16));
        } catch (final NumberFormatException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }
    }

    /**
     * Creates a new {@link SpreadsheetId}
     */
    public static SpreadsheetId with(final long value) {
        return new SpreadsheetId(value);
    }

    private SpreadsheetId(final Long value) {
        super();

        this.value = value;
    }

    // HateosResource ....................................................................................................

    @Override
    public Long id() {
        return this.value();
    }

    @Override
    public String idForHateosLink() {
        return this.toString();
    }

    // Value ....................................................................................................

    @Override
    public Long value() {
        return this.value;
    }

    private Long value;

    // HasJsonNode..........................................................................................

    /**
     * Factory that creates a {@link SpreadsheetId} from a {@link JsonNode}
     */
    public static SpreadsheetId fromJsonNode(final JsonNode node) {
        return with(node.fromJsonNode(Long.class));
    }

    @Override
    public JsonNode toJsonNode() {
        return HasJsonNode.toJsonNodeObject(this.value);
    }

    static {
        HasJsonNode.register("spreadsheet-id",
                SpreadsheetId::fromJsonNode,
                SpreadsheetId.class);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetId &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetId id) {
        return this.value.equals(id.value());
    }

    // Comparable....................................................................................................

    @Override
    public int compareTo(final SpreadsheetId other) {
        return this.value.compareTo(other.value);
    }

    // Object........................................................................................................

    @Override
    public String toString() {
        return Long.toHexString(this.value);
    }
}
