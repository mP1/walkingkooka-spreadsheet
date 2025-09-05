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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.security.Group;
import walkingkooka.spreadsheet.security.GroupId;
import walkingkooka.spreadsheet.security.GroupName;
import walkingkooka.spreadsheet.security.UserId;

import java.util.Optional;
import java.util.TreeMap;

public final class TreeMapSpreadsheetGroupStoreTest implements SpreadsheetGroupStoreTesting<TreeMapSpreadsheetGroupStore>,
    ToStringTesting<TreeMapSpreadsheetGroupStore> {

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testLoad1() {
        this.loadAndCheck(this.createNotEmptyStore(), this.groupId2().get(), this.group2());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testLoad2() {
        this.loadAndCheck(this.createNotEmptyStore(), this.groupId3().get(), this.group3());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testSave() {
        final TreeMapSpreadsheetGroupStore store = this.createNotEmptyStore();

        final Group saved = Group.with(this.groupId2(), GroupName.with("saved"));
        store.save(saved);

        this.loadAndCheck(store, saved.id().get(), saved);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testSaveReplaces() {
        final TreeMapSpreadsheetGroupStore store = this.createNotEmptyStore();

        final Optional<GroupId> id = this.group3().id();
        final Group replace = Group.with(id, GroupName.with("replaced"));
        store.save(replace);

        this.loadAndCheck(store, id.get(), replace);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testDelete() {
        final TreeMapSpreadsheetGroupStore store = this.createNotEmptyStore();

        final GroupId id = this.groupId1().get();
        store.delete(id);

        this.loadAndCheck(
            store,
            id
        );
    }

    @Test
    public void testCount() {
        this.countAndCheck(this.createNotEmptyStore(), 3);
    }

    @Test
    public void testCountAfterSave() {
        final TreeMapSpreadsheetGroupStore store = this.createNotEmptyStore();

        store.save(Group.with(Optional.of(GroupId.with(999)), GroupName.with("saved")));

        this.countAndCheck(store, 3 + 1);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testIds() {
        final TreeMapSpreadsheetGroupStore store = this.createStore();

        final Group a = this.group1();
        final Group b = this.group2();
        final Group c = this.group3();

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(store,
            0,
            3,
            a.id().get(), b.id().get(), c.id().get());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testIdsWindow() {
        final TreeMapSpreadsheetGroupStore store = this.createStore();

        final Group a = this.group1();
        final Group b = this.group2();
        final Group c = this.group3();
        final Group d = this.group4();

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.idsAndCheck(store,
            1,
            2,
            b.id().get(), c.id().get());
    }

    @Test
    public void testValues() {
        final TreeMapSpreadsheetGroupStore store = this.createStore();

        final Group a = this.group1();
        final Group b = this.group2();
        final Group c = this.group3();

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(
            store,
            0,
            3,
            a,
            b,
            c
        );
    }

    @Test
    public void testValuesWindow() {
        final TreeMapSpreadsheetGroupStore store = this.createStore();

        final Group a = this.group1();
        final Group b = this.group2();
        final Group c = this.group3();
        final Group d = this.group4();

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(store,
            1,
            2,
            b,
            c
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testAddUserAndLoadUserGroups() {
        final TreeMapSpreadsheetGroupStore store = this.createNotEmptyStore();

        final Group group1 = this.group1();
        final Group group2 = this.group2();

        final UserId user1 = this.user1();
        final UserId user2 = this.user2();

        final GroupId groupId1 = group1.id().get();
        final GroupId groupId2 = group2.id().get();

        store.addUser(user1, groupId1);
        store.addUser(user2, groupId1);
        store.addUser(user2, groupId2);

        this.loadUserGroupsAndCheck(store, user1, Sets.of(group1));
        this.loadUserGroupsAndCheck(store, user2, Sets.of(group1, group2));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void testAddUserRemoveUserAndLoadUserGroups() {
        final TreeMapSpreadsheetGroupStore store = this.createNotEmptyStore();

        final Group group1 = this.group1();
        final Group group2 = this.group2();

        final UserId user1 = this.user1();
        final UserId user2 = this.user2();

        final GroupId groupId1 = group1.id().get();
        final GroupId groupId2 = group2.id().get();

        store.addUser(user1, groupId1);
        store.addUser(user2, groupId1);
        store.addUser(user2, groupId2);

        store.removeUser(user1, groupId1);
        store.removeUser(user2, groupId1);

        this.loadUserGroupsAndCheck(store, user1, Sets.empty());
        this.loadUserGroupsAndCheck(store, user2, Sets.of(group2));
    }

    @Test
    public void testToString() {
        final TreeMapSpreadsheetGroupStore store = this.createNotEmptyStore();
        this.toStringAndCheck(store, "[1 group1, 2 group2, 33 group33]");
    }

    // helpers........................................................................................

    private Group group1() {
        return Group.with(this.groupId1(), GroupName.with("group1"));
    }

    private Group group2() {
        return Group.with(this.groupId2(), GroupName.with("group2"));
    }

    private Group group3() {
        return Group.with(this.groupId3(), GroupName.with("group33"));
    }

    private Group group4() {
        return Group.with(this.groupId4(), GroupName.with("group444"));
    }

    private Optional<GroupId> groupId1() {
        return Optional.of(GroupId.with(1));
    }

    private Optional<GroupId> groupId2() {
        return Optional.of(GroupId.with(2));
    }

    private Optional<GroupId> groupId3() {
        return Optional.of(GroupId.with(33));
    }

    private Optional<GroupId> groupId4() {
        return Optional.of(GroupId.with(444));
    }

    private UserId user1() {
        return UserId.with(1);
    }

    private UserId user2() {
        return UserId.with(2);
    }

    // SpreadsheetGroupStoreTesting............................................................................

    @Override
    public TreeMapSpreadsheetGroupStore createStore() {
        return TreeMapSpreadsheetGroupStore.with();
    }

    private TreeMapSpreadsheetGroupStore createNotEmptyStore() {
        final TreeMapSpreadsheetGroupStore store = this.createStore();

        store.store.save(this.group1());
        store.store.save(this.group2());
        store.store.save(this.group3());

        return store;
    }

    // ClassTesting..........................................................................................

    @Override
    public Class<TreeMapSpreadsheetGroupStore> type() {
        return TreeMapSpreadsheetGroupStore.class;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}
