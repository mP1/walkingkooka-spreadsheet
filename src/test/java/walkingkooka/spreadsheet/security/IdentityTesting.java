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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface IdentityTesting<I extends Identity<ID>, ID extends IdentityId>
    extends ClassTesting2<I>,
    JsonNodeMarshallingTesting<I>,
    HashCodeEqualsDefinedTesting2<I>,
    ToStringTesting<I> {

    @SuppressWarnings("OptionalAssignedToNull")
    @Test
    default void testWithNullIdFails() {
        assertThrows(NullPointerException.class, () -> this.createIdentity(null));
    }

    @Override
    default I createObject() {
        return this.createIdentity();
    }

    default I createIdentity() {
        return this.createIdentity(this.createId());
    }

    @Override
    default I createJsonNodeMarshallingValue() {
        return this.createObject();
    }

    I createIdentity(final Optional<ID> id);

    Optional<ID> createId();

    @Override
    default JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
