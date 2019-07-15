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

import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.User;
import walkingkooka.spreadsheet.security.UserId;
import walkingkooka.spreadsheet.store.SpreadsheetStore;

import java.util.Set;

/**
 * A {@link SpreadsheetStore} that holds all {@link Group groups} for a spreadsheet.
 */
public interface SpreadsheetGroupStore extends SpreadsheetStore<GroupId, Group> {

    /**
     * Adds a user to an existing group.
     */
    void addUser(final UserId userId, final GroupId groupId);

    /**
     * Adds a user from an existing group.
     */
    void removeUser(final UserId userId, final GroupId groupId);

    /**
     * Loads all the groups for the provided {@link User}
     */
    Set<Group> loadUserGroups(final UserId userId);
}
