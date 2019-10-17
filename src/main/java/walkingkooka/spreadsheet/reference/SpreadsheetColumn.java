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
import walkingkooka.test.HashCodeEqualsDefined;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a single column within a spreadsheet.
 */
public final class SpreadsheetColumn implements Comparable<SpreadsheetColumn>,
        HashCodeEqualsDefined,
        HateosResource<SpreadsheetColumnReference> {

    /**
     * Factory that creates a new {@link SpreadsheetColumn}
     */
    public static SpreadsheetColumn with(final SpreadsheetColumnReference reference) {
        checkReference(reference);

        return new SpreadsheetColumn(reference);
    }

    private static void checkReference(final SpreadsheetColumnReference reference) {
        Objects.requireNonNull(reference, "reference");
    }

    private SpreadsheetColumn(final SpreadsheetColumnReference reference) {
        super();
        this.reference = reference;
    }

    // HateosResource...................................................................................................

    public Optional<SpreadsheetColumnReference> id() {
        return Optional.of(this.reference());
    }

    @Override
    public String hateosLinkId() {
        return this.id().toString();
    }

    // reference .......................................................................................................

    public SpreadsheetColumnReference reference() {
        return this.reference;
    }

    public SpreadsheetColumn setReference(final SpreadsheetColumnReference reference) {
        checkReference(reference);

        return this.reference.equals(reference) ?
                this :
                this.replace(reference);
    }

    /**
     * The reference that identifies this cell.
     */
    private final SpreadsheetColumnReference reference;

    // replace .............................................................................................

    private SpreadsheetColumn replace(final SpreadsheetColumnReference reference) {
        return new SpreadsheetColumn(reference);
    }

    // JsonNodeContext..................................................................................................

    static SpreadsheetColumn unmarshall(final JsonNode node,
                                        final JsonNodeUnmarshallContext context) {
        return with(context.unmarshall(node, SpreadsheetColumnReference.class));
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshall(this.reference);
    }

    static {
        JsonNodeContext.register("spreadsheet-column",
                SpreadsheetColumn::unmarshall,
                SpreadsheetColumn::marshall,
                SpreadsheetColumn.class);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.reference);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof SpreadsheetColumn &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetColumn other) {
        return this.reference.equals(other.reference());
    }

    @Override
    public String toString() {
        return this.reference.toString();
    }

    // Comparable..........................................................................................

    public int compareTo(final SpreadsheetColumn other) {
        return this.reference.compareTo(other.reference);
    }
}
