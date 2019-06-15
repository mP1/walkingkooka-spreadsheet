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
import walkingkooka.compare.ComparableTesting;
import walkingkooka.tree.json.JsonNode;

public final class UserIdTest extends IdentityIdTestCase<UserId> implements ComparableTesting<UserId> {

    @Test
    public void testCompareLess() {
        this.compareToAndCheckLess(UserId.with(999));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(UserId.with(123), "123");
    }

    @Override
    UserId createId(final long value) {
        return UserId.with(value);
    }

    @Override
    public Class<UserId> type() {
        return UserId.class;
    }

    // ComparableTesting....................................................................

    @Override
    public UserId createComparable() {
        return UserId.with(99);
    }

    // HasJsonNodeTesting..................................................................

    @Override
    public final UserId fromJsonNode(final JsonNode from) {
        return UserId.fromJsonNode(from);
    }
}
