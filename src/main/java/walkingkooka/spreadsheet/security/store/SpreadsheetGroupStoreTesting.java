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

package walkingkooka.spreadsheet.security.store;

import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.GroupName;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.store.StoreTesting;

import java.util.Optional;
import java.util.Set;

public interface SpreadsheetGroupStoreTesting<S extends SpreadsheetGroupStore> extends StoreTesting<S, GroupId, Group>,
    TypeNameTesting<S> {

    default void loadUserGroupsAndCheck(final SpreadsheetGroupStore store,
                                        final UserId userId,
                                        final Set<Group> groups) {
        this.checkEquals(groups,
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
        return Group.with(Optional.of(this.id()), GroupName.with("Group1"));
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetGroupStore.class.getSimpleName();
    }
}
