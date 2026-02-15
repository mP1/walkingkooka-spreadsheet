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

package walkingkooka.spreadsheet.currency;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Currency;
import java.util.Objects;
import java.util.Optional;

/**
 * A typed {@link Optional} necessary because generic types are lost in java.
 * This class is intended to be a target when converting a cell to a {@link Currency} to patch cells.
 */
public final class OptionalCurrency implements Value<Optional<Currency>> {

    public final static OptionalCurrency EMPTY = new OptionalCurrency(Optional.empty());

    public static OptionalCurrency with(final Optional<Currency> value) {
        Objects.requireNonNull(value, "value");

        return value.isPresent() ?
            new OptionalCurrency(value) :
            EMPTY;
    }

    private OptionalCurrency(final Optional<Currency> value) {
        this.value = value;
    }

    @Override
    public Optional<Currency> value() {
        return this.value;
    }

    private final Optional<Currency> value;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof OptionalCurrency &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final OptionalCurrency other) {
        return this.value.equals(other.value);
    }

    @Override
    public String toString() {
        return this.value.map(Object::toString)
            .orElse("");
    }

    // json.............................................................................................................

    static OptionalCurrency unmarshall(final JsonNode node,
                                       final JsonNodeUnmarshallContext context) {
        return with(
            context.unmarshallOptional(
                node,
                Currency.class
            )
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshallOptional(this.value);
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(OptionalCurrency.class),
            OptionalCurrency::unmarshall,
            OptionalCurrency::marshall,
            OptionalCurrency.class
        );
    }
}
