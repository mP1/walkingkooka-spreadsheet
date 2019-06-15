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

package walkingkooka.spreadsheet.store.security;

import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.GroupName;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.StoreTesting;
import walkingkooka.test.TypeNameTesting;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface SpreadsheetGroupStoreTesting<S extends SpreadsheetGroupStore> extends StoreTesting<S, GroupId, Group>,
        TypeNameTesting<S> {

    default void loadUserGroupsAndCheck(final SpreadsheetGroupStore store,
                                        final UserId userId,
                                        final Set<Group> groups) {
        assertEquals(groups,
                store.loadUserGroups(userId),
                "store loadUserGroups " + userId + " incorrect result");
    }

    // StoreTesting...........................................................

    @Override
    default GroupId id() {
        return GroupId.with(1);
    }

    @Override
    default Group value() {
        return Group.with(this.id(), GroupName.with("Group1"));
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetGroupStore.class.getSimpleName();
    }
}
