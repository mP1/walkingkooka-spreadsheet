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
import walkingkooka.tree.json.marshall.FromJsonNodeContext;
import walkingkooka.tree.json.marshall.ToJsonNodeContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class UserTest extends IdentityTestCase<User, UserId> {

    private final static EmailAddress EMAIL = EmailAddress.parse("user@example.com");

    @Test
    public void testWithNullEmailFails() {
        assertThrows(NullPointerException.class, () -> {
            User.with(this.createId(), null);
        });
    }

    @Test
    public void testWith() {
        final Optional<UserId> id = this.createId();
        final User user = User.with(id, EMAIL);
        assertEquals(id, user.value(), "id");
        assertEquals(EMAIL, user.email(), "email");
    }

    @Test
    public void testSetIdSame() {
        final User user = this.createIdentity();
        assertSame(user, user.setId(this.createId()));
    }

    @Test
    public void testSetIdDifferent() {
        final User user = this.createIdentity();
        final Optional<UserId> id = Optional.of(UserId.with(999));

        final User different = user.setId(id);
        assertEquals(id, different.id(), "id");
        assertEquals(EMAIL, different.email(), "email");
    }

    // JsonNodeMappingTesting.................................................................................................

    @Test
    public void testFromJsonNodeWithoutId() {
        this.fromJsonNodeAndCheck(this.jsonNodeWithoutId(),
                User.with(Optional.empty(), EMAIL));
    }

    @Test
    public void testFromJsonNodeWithId() {
        this.fromJsonNodeAndCheck(this.jsonNodeWithId(),
                User.with(this.createId(), EMAIL));
    }

    @Test
    public void testToJsonNodeWithoutId() {
        this.toJsonNodeAndCheck(this.createIdentity().setId(Optional.empty()), this.jsonNodeWithoutId());
    }

    @Test
    public void testToJsonNodeWithId() {
        this.toJsonNodeAndCheck(this.createObject(), this.jsonNodeWithId());
    }

    private JsonNode jsonNodeWithoutId() {
        return JsonNode.object()
                .set(User.EMAIL_PROPERTY, this.toJsonNodeContext().toJsonNode(EMAIL));
    }

    private JsonNode jsonNodeWithId() {
        final ToJsonNodeContext context = this.toJsonNodeContext();

        return JsonNode.object()
                .set(User.ID_PROPERTY, context.toJsonNode(createId().get()))
                .set(User.EMAIL_PROPERTY, context.toJsonNode(EMAIL));
    }

    @Test
    public void testToJsonNodeRoundtripTwice() {
        this.toJsonNodeRoundTripTwiceAndCheck(this.createObject());
    }

    // ToStringTesting.................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(User.with(this.createId(), EMAIL), "1 " + EMAIL);
    }

    @Override
    public User createIdentity(final Optional<UserId> id) {
        return User.with(id, EMAIL);
    }

    @Override
    public Optional<UserId> createId() {
        return Optional.of(UserId.with(1));
    }

    @Override
    public Class<User> type() {
        return User.class;
    }

    // JsonNodeMappingTesting...........................................................................................

    @Override
    public final User fromJsonNode(final JsonNode from,
                                   final FromJsonNodeContext context) {
        return User.fromJsonNode(from, context);
    }
}
