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
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A group defined in the system.
 */
public final class Group extends Identity<GroupId> {

    /**
     * Factory that creates a new {@link Group}.
     */
    public static Group with(final Optional<GroupId> id, final GroupName name) {
        checkId(id);
        Objects.requireNonNull(name, "name");

        return new Group(id, name);
    }

    /**
     * Private ctor use factory.
     */
    private Group(final Optional<GroupId> id, final GroupName name) {
        super(id);
        this.name = name;
    }

    /**
     * Would be setter returns a {@link Group} with the given id, creating a new instance if necessary.
     */
    public Group setId(final Optional<GroupId> id) {
        checkId(id);

        return this.id.equals(id) ?
            this :
            new Group(id, this.name);
    }

    public GroupName name() {
        return this.name;
    }

    private final GroupName name;

    // JsonNodeContext..................................................................................................

    static Group unmarshall(final JsonNode node,
                            final JsonNodeUnmarshallContext context) {
        GroupId id = null;
        GroupName groupName = null;

        for (JsonNode child : node.objectOrFail().children()) {
            final JsonPropertyName name = child.name();
            switch (name.value()) {
                case ID_PROPERTY_STRING:
                    id = context.unmarshall(child, GroupId.class);
                    break;
                case NAME_PROPERTY_STRING:
                    groupName = context.unmarshall(child, GroupName.class);
                    break;
                default:
                    JsonNodeUnmarshallContext.unknownPropertyPresent(name, node);
                    break;
            }
        }

        if (null == groupName) {
            JsonNodeUnmarshallContext.missingProperty(NAME_PROPERTY, node);
        }

        return new Group(Optional.ofNullable(id), groupName);
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        final List<JsonNode> properties = Lists.array();

        this.id.ifPresent(id -> properties.add(context.marshall(id).setName(ID_PROPERTY)));
        properties.add(context.marshall(this.name).setName(NAME_PROPERTY));

        return JsonNode.object()
            .setChildren(properties);
    }

    private final static String NAME_PROPERTY_STRING = "name";
    final static JsonPropertyName NAME_PROPERTY = JsonPropertyName.with(NAME_PROPERTY_STRING);

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(Group.class),
            Group::unmarshall,
            Group::marshall,
            Group.class
        );
    }

    // Identity.........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof Group;
    }

    @Override
    boolean equals1(final Identity<?> other) {
        return this.name.equals(((Group) other).name);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .value(this.id)
            .value(this.name)
            .build();
    }
}
