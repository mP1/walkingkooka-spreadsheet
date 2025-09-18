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

package walkingkooka.spreadsheet.format.provider;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Optional;

/**
 * A typed {@link Optional} necessary because generic types are lost in java.
 * This class is intended to be a target when converting a cell to a formatter to patch cells.
 */
public final class OptionalSpreadsheetFormatterSelector implements Value<Optional<SpreadsheetFormatterSelector>> {

    public final static OptionalSpreadsheetFormatterSelector EMPTY = new OptionalSpreadsheetFormatterSelector(Optional.empty());

    public static OptionalSpreadsheetFormatterSelector with(final Optional<SpreadsheetFormatterSelector> value) {
        Objects.requireNonNull(value, "value");

        return value.isPresent() ?
            new OptionalSpreadsheetFormatterSelector(value) :
            EMPTY;
    }

    private OptionalSpreadsheetFormatterSelector(final Optional<SpreadsheetFormatterSelector> value) {
        this.value = value;
    }

    @Override
    public Optional<SpreadsheetFormatterSelector> value() {
        return this.value;
    }

    private final Optional<SpreadsheetFormatterSelector> value;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof OptionalSpreadsheetFormatterSelector &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final OptionalSpreadsheetFormatterSelector other) {
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    // json.............................................................................................................

    static OptionalSpreadsheetFormatterSelector unmarshall(final JsonNode node,
                                                           final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallOptional(
                node,
                SpreadsheetFormatterSelector.class
            )
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallOptional(this.value);
    }

    static {
        SpreadsheetFormatterSelector.DEFAULT_TEXT_FORMAT.name();

        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(OptionalSpreadsheetFormatterSelector.class),
            OptionalSpreadsheetFormatterSelector::unmarshall,
            OptionalSpreadsheetFormatterSelector::marshall,
            OptionalSpreadsheetFormatterSelector.class
        );
    }
}
