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

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A user in the system.
 */
public final class User extends Identity<UserId> {

    /**
     * Factory that creates a new {@link User}.
     */
    public static User with(final Optional<UserId> id, final EmailAddress email) {
        checkId(id);
        Objects.requireNonNull(email, "email");

        return new User(id, email);
    }

    /**
     * Private ctor use factory.
     */
    private User(final Optional<UserId> id, final EmailAddress email) {
        super(id);
        this.email = email;
    }

    /**
     * Would be setter returns a {@link User} with the given id, creating a new instance if necessary.
     */
    public User setId(final Optional<UserId> id) {
        checkId(id);

        return this.id.equals(id) ?
                this :
                new User(id, this.email);
    }

    public EmailAddress email() {
        return this.email;
    }

    private final EmailAddress email;

    // HateosResource...................................................................................................

    @Override
    public String idForHateosLink() {
        return this.email.value().replace("-", "\\-");
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link User} from a {@link JsonNode}.
     */
    public static User fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        UserId id = null;
        EmailAddress email = null;

        try {
            for (JsonNode child : node.objectOrFail().children()) {
                final JsonNodeName name = child.name();
                switch (name.value()) {
                    case ID_PROPERTY_STRING:
                        id = UserId.fromJsonNode(child);
                        break;
                    case EMAIL_PROPERTY_STRING:
                        email = EmailAddress.fromJsonNode(child);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown property " + name + "=" + node);
                }
            }
        } catch (final JsonNodeException cause) {
            throw new IllegalArgumentException(cause.getMessage(), cause);
        }

        if (null == email) {
            HasJsonNode.requiredPropertyMissing(EMAIL_PROPERTY, node);
        }

        return new User(Optional.ofNullable(id), email);
    }

    @Override
    public JsonNode toJsonNode() {
        final List<JsonNode> properties = Lists.array();

        this.id.ifPresent(id -> properties.add(id.toJsonNode().setName(ID_PROPERTY)));
        properties.add(this.email.toJsonNode().setName(EMAIL_PROPERTY));

        return JsonNode.object()
                .setChildren(properties);
    }

    private final static String EMAIL_PROPERTY_STRING = "email";
    final static JsonNodeName EMAIL_PROPERTY = JsonNodeName.with(EMAIL_PROPERTY_STRING);

    static {
        HasJsonNode.register("user",
                User::fromJsonNode,
                User.class);
    }

    // Identity.........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof User;
    }

    @Override
    boolean equals1(final Identity<?> other) {
        return this.email.equals(User.class.cast(other).email);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.id)
                .value(this.email)
                .build();
    }
}
