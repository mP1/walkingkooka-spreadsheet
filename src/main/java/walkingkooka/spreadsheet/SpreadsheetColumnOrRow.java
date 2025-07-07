
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
import walkingkooka.ToStringBuilder;
import walkingkooka.UsesToStringBuilder;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.reference.HasSpreadsheetReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * Base class for both column and row.
 */
public abstract class SpreadsheetColumnOrRow<R extends SpreadsheetSelection & Comparable<R>> implements HasSpreadsheetReference<R>,
    HateosResource<R>,
    TreePrintable,
    UsesToStringBuilder {

    static void checkReference(final SpreadsheetSelection reference) {
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

    @Override
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
        return ToStringBuilder.buildFrom(this);
    }

    // UsesToStringBuilder..............................................................................................

    @Override
    public void buildToString(final ToStringBuilder builder) {
        builder.value(this.reference)
            .label("hidden")
            .value(this.hidden());
    }

    // json.............................................................................................................

    final static String HIDDEN_PROPERTY_STRING = "hidden";

    final static JsonPropertyName HIDDEN_PROPERTY = JsonPropertyName.with(HIDDEN_PROPERTY_STRING);

    final JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.object()
            .set(
                JsonPropertyName.with(this.reference.toString()),
                JsonNode.object()
                    .set(HIDDEN_PROPERTY, JsonNode.booleanNode(this.hidden))
            );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.reference().toString());
        printer.indent();

        if (this.hidden()) {
            printer.println(HIDDEN_PROPERTY_STRING);
        }

        printer.outdent();
    }
}
