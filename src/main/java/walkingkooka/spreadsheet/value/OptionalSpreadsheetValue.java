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

package walkingkooka.spreadsheet.value;

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
 * This class is intended to be a target when converting a cell to a value to patch cells in a CLIPBOARD PASTE.
 */
public final class OptionalSpreadsheetValue<T> implements Value<Optional<T>> {

    public final static OptionalSpreadsheetValue<?> EMPTY = new OptionalSpreadsheetValue<>(Optional.empty());

    public static <T> OptionalSpreadsheetValue<T> with(final Optional<T> value) {
        Objects.requireNonNull(value, "value");

        return value.isPresent() ?
            new OptionalSpreadsheetValue<>(value) :
            Cast.to(EMPTY);
    }

    private OptionalSpreadsheetValue(final Optional<T> value) {
        this.value = value;
    }

    @Override
    public Optional<T> value() {
        return this.value;
    }

    private final Optional<T> value;

    // T...........................................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof OptionalSpreadsheetValue &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final OptionalSpreadsheetValue<?> other) {
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    // json.............................................................................................................

    static OptionalSpreadsheetValue<?> unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallOptionalWithType(node)
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallOptionalWithType(this.value);
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(OptionalSpreadsheetValue.class),
            OptionalSpreadsheetValue::unmarshall,
            OptionalSpreadsheetValue::marshall,
            OptionalSpreadsheetValue.class
        );
    }
}
