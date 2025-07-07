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

import org.junit.jupiter.api.Test;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class GroupTest extends IdentityTestCase<Group, GroupId> {

    private final static long ID_VALUE = 123;

    @Test
    public void testWithNullNameFails() {
        assertThrows(NullPointerException.class, () -> Group.with(this.createId(), null));
    }

    @Test
    public void testWith() {
        final Optional<GroupId> id = this.createId();
        final Group group = Group.with(id, name());
        this.checkEquals(id, group.value(), "id");
        this.checkEquals(name(), group.name(), "name");
    }

    @Test
    public void testSetIdSame() {
        final Group group = this.createIdentity();
        assertSame(group, group.setId(this.createId()));
    }

    @Test
    public void testSetIdDifferent() {
        final Group group = this.createIdentity();
        final Optional<GroupId> id = Optional.of(GroupId.with(999));

        final Group different = group.setId(id);
        this.checkEquals(id, different.id(), "id");
        this.checkEquals(name(), different.name(), "name");
    }

    @Test
    public void testEqualsDifferentName() {
        this.checkNotEquals(Group.with(this.createId(), GroupName.with("different")));
    }

    @Test
    public void testUser() {
        this.checkNotEquals(User.with(Optional.of(UserId.with(ID_VALUE)), EmailAddress.parse("user@example.com")));
    }

    // JsonNodeMarshallingTesting.................................................................................................

    @Test
    public void testUnmarshallWithoutId() {
        this.unmarshallAndCheck(this.jsonNodeWithoutId(),
            Group.with(Optional.empty(), this.name()));
    }

    @Test
    public void testUnmarshallWithId() {
        this.unmarshallAndCheck(this.jsonNodeWithId(),
            Group.with(this.createId(), this.name()));
    }

    @Test
    public void testMarshallWithoutId() {
        this.marshallAndCheck(this.createIdentity().setId(Optional.empty()), this.jsonNodeWithoutId());
    }

    @Test
    public void testMarshallWithId() {
        this.marshallAndCheck(this.createObject(), this.jsonNodeWithId());
    }

    private JsonNode jsonNodeWithoutId() {
        return JsonNode.object()
            .set(Group.NAME_PROPERTY, this.marshallContext().marshall(this.name()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private JsonNode jsonNodeWithId() {
        final JsonNodeMarshallContext context = this.marshallContext();

        return JsonNode.object()
            .set(Group.ID_PROPERTY, context.marshall(createId().get()))
            .set(Group.NAME_PROPERTY, context.marshall(this.name()));
    }

    @Test
    @Override
    public void testMarshallRoundtripTwice() {
        this.marshallRoundTripTwiceAndCheck(this.createObject());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(Group.with(this.createId(), name()), "1 Group-123");
    }

    @Override
    public Group createIdentity(final Optional<GroupId> id) {
        return Group.with(id, name());
    }

    @Override
    public Optional<GroupId> createId() {
        return Optional.of(GroupId.with(1));
    }

    private GroupName name() {
        return GroupName.with("Group-123");
    }

    @Override
    public Class<Group> type() {
        return Group.class;
    }

    // JsonNodeMarshallingTesting............................................................................................

    @Override
    public Group unmarshall(final JsonNode from,
                            final JsonNodeUnmarshallContext context) {
        return Group.unmarshall(from, context);
    }
}
