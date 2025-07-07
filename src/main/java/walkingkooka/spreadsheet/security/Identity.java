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
import walkingkooka.HasId;
import walkingkooka.Value;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.json.JsonPropertyName;

import java.util.Objects;
import java.util.Optional;

/**
 * Base class for all security related identifies
 */
public abstract class Identity<I extends IdentityId> implements Value<Optional<I>>,
    HasId<Optional<I>> {

    /**
     * Factory that creates a new {@link Group}.
     */
    public static Group group(final Optional<GroupId> id, final GroupName name) {
        return Group.with(id, name);
    }

    /**
     * Factory that creates a new {@link User}.
     */
    public static User user(final Optional<UserId> id, final EmailAddress email) {
        return User.with(id, email);
    }

    static void checkId(final Optional<?> id) {
        Objects.requireNonNull(id, "id");
    }

    /**
     * Package private to limit subclassing.
     */
    Identity(final Optional<I> id) {
        super();
        this.id = id;
    }

    // HateosResource ..................................................................................................

    @Override
    public final Optional<I> id() {
        return this.id;
    }

    // Identity........................................................................................................

    @Override
    public final Optional<I> value() {
        return this.id;
    }

    final Optional<I> id;

    // JsonNodeContext..................................................................................................

    final static String ID_PROPERTY_STRING = "id";
    final static JsonPropertyName ID_PROPERTY = JsonPropertyName.with(ID_PROPERTY_STRING);

    // Object...........................................................................................................

    @Override
    public final int hashCode() {
        return this.id.hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public final boolean equals(final Object other) {
        return this == other ||
            this.canBeEqual(other) &&
                this.equals0(Cast.to(other));
    }

    /**
     * subclasses should do an instanceof test.
     */
    abstract boolean canBeEqual(final Object other);

    private boolean equals0(final Identity<?> other) {
        return this.id.equals(other.id) &&
            this.equals1(other);
    }

    abstract boolean equals1(final Identity<?> other);
}
