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

import walkingkooka.tree.json.FromJsonNodeException;
import walkingkooka.tree.json.HasJsonNode;
import walkingkooka.tree.json.JsonNode;

/**
 * The primary key for a {@link Group}.
 */
public final class GroupId extends IdentityId
        implements Comparable<GroupId> {

    static GroupId fromJsonNode(final JsonNode node) {
        try {
            return with(node.fromJsonNode(Long.class));
        } catch (final FromJsonNodeException cause) {
            throw cause;
        } catch (final RuntimeException cause) {
            throw new FromJsonNodeException(cause.getMessage(), node, cause);
        }
    }

    public static GroupId with(final long value) {
        return new GroupId(value);
    }

    private GroupId(final long value) {
        super(value);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof GroupId;
    }

    // HasJsonNode.......................................................................................

    static {
        HasJsonNode.register("group-id",
                GroupId::fromJsonNode,
                GroupId.class);
    }

    // Comparable..............................................................................................

    @Override
    public int compareTo(final GroupId other) {
        return this.compareTo0(other);
    }
}
