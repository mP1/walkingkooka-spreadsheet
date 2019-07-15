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
import walkingkooka.tree.json.FromJsonNodeException;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonNodeException;
import walkingkooka.tree.json.JsonNodeName;

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

    // HateosResource...................................................................................................

    @Override
    public String hateosLinkId() {
        return this.name.value().replace("-", "\\-");
    }

    // HasJsonNode......................................................................................................

    /**
     * Factory that creates a {@link Group} from a {@link JsonNode}.
     */
    static Group fromJsonNode(final JsonNode node) {
        Objects.requireNonNull(node, "node");

        GroupId id = null;
        GroupName groupName = null;

        try {
            for (JsonNode child : node.objectOrFail().children()) {
                final JsonNodeName name = child.name();
                switch (name.value()) {
                    case ID_PROPERTY_STRING:
                        id = GroupId.fromJsonNode(child);
                        break;
                    case NAME_PROPERTY_STRING:
                        groupName = GroupName.fromJsonNode(child);
                        break;
                    default:
                        HasJsonNode.unknownPropertyPresent(name, node);
                }
            }
        } catch (final JsonNodeException cause) {
            throw new FromJsonNodeException(cause.getMessage(), node, cause);
        }

        if (null == groupName) {
            HasJsonNode.requiredPropertyMissing(NAME_PROPERTY, node);
        }

        try {
            return new Group(Optional.ofNullable(id), groupName);
        } catch (final RuntimeException cause) {
            throw new FromJsonNodeException(cause.getMessage(), node, cause);
        }
    }

    @Override
    public JsonNode toJsonNode() {
        final List<JsonNode> properties = Lists.array();

        this.id.ifPresent(id -> properties.add(id.toJsonNode().setName(ID_PROPERTY)));
        properties.add(this.name.toJsonNode().setName(NAME_PROPERTY));

        return JsonNode.object()
                .setChildren(properties);
    }

    private final static String NAME_PROPERTY_STRING = "name";
    final static JsonNodeName NAME_PROPERTY = JsonNodeName.with(NAME_PROPERTY_STRING);

    static {
        HasJsonNode.register("group",
                Group::fromJsonNode,
                Group.class);
    }

    // Identity.........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof Group;
    }

    @Override
    boolean equals1(final Identity<?> other) {
        return this.name.equals(Group.class.cast(other).name);
    }

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.id)
                .value(this.name)
                .build();
    }
}
