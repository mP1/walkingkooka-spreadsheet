
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

package walkingkooka.spreadsheet.reference;

import walkingkooka.Cast;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Base class for both column and row.
 */
public abstract class SpreadsheetColumnOrRow<R extends SpreadsheetColumnOrRowReference> implements HateosResource<R> {

    static void checkReference(final SpreadsheetColumnOrRowReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    SpreadsheetColumnOrRow(final R reference,
                           final boolean hidden) {
        super();
        this.reference = reference;
        this.hidden = hidden;
    }

    // HateosResource...................................................................................................

    @Override
    public final Optional<R> id() {
        return Optional.of(this.reference());
    }

    @Override
    public final String hateosLinkId() {
        return this.id().toString();
    }

    // reference .......................................................................................................

    public final R reference() {
        return this.reference;
    }

    /**
     * The reference for this column/row
     */
    final R reference;

    // hidden .......................................................................................................

    public final boolean hidden() {
        return this.hidden;
    }

    /**
     * true indicates the column or row is hidden.
     */
    final boolean hidden;

    // HashCodeEqualsDefined............................................................................................

    @Override
    public final int hashCode() {
        return this.reference.hashCode();
    }

    @Override
    public final boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetColumnOrRow &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetColumnOrRow<?> other) {
        return this.reference.equals(other.reference()) &&
                this.hidden == other.hidden;
    }

    @Override
    public final String toString() {
        return this.reference.toString();
    }

    // json.............................................................................................................

    final static String REFERENCE_PROPERTY_STRING = "reference";
    final static String HIDDEN_PROPERTY_STRING = "hidden";

    final static JsonPropertyName REFERENCE_PROPERTY = JsonPropertyName.with(REFERENCE_PROPERTY_STRING);
    final static JsonPropertyName HIDDEN_PROPERTY = JsonPropertyName.with(HIDDEN_PROPERTY_STRING);

    final JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
                .set(REFERENCE_PROPERTY, context.marshall(this.reference))
                .set(HIDDEN_PROPERTY, JsonNode.booleanNode(this.hidden));
    }
}
