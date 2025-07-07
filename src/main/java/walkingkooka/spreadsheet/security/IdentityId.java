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

package walkingkooka.spreadsheet.security;

import walkingkooka.Cast;
import walkingkooka.Value;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

/**
 * Base class for all ids.
 */
public abstract class IdentityId implements Value<Long> {

    /**
     * Package private to limit subclassing.
     */
    IdentityId(final long value) {
        super();
        this.value = value;
    }

    @Override
    public final Long value() {
        return this.value;
    }

    private final long value;

    // JsonNodeContext...................................................................................................

    /**
     * Stores the id.
     */
    final JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshall(this.value);
    }

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return Long.hashCode(this.value);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public final boolean equals(final Object other) {
        return this == other ||
            this.canBeEqual(other) &&
                this.equals0(Cast.to(other));
    }

    /**
     * subclasses should do an instanceof test.
     */
    abstract boolean canBeEqual(final Object other);

    private boolean equals0(final IdentityId other) {
        return this.value == other.value;
    }

    @Override
    public final String toString() {
        return String.valueOf(this.value);
    }

    // Comparable..............................................................................................

    final int compareTo0(final IdentityId other) {
        return Long.compare(this.value, other.value());
    }
}
