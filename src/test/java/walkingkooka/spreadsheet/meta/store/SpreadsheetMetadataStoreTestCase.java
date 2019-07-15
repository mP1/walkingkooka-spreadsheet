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

package walkingkooka.spreadsheet.meta.store;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetMetadataStoreTestCase<S extends SpreadsheetMetadataStore> implements SpreadsheetMetadataStoreTesting<S> {

    SpreadsheetMetadataStoreTestCase() {
        super();
    }

    @Test
    public final void testLoadUnknownFails() {
        this.loadFailCheck(this.id());
    }

    @Test
    public void testSaveAndLoad() {
        final S store = this.createStore();

        final SpreadsheetMetadata metadata = this.metadata(ID, "user1@example.com");
        store.save(metadata);

        assertSame(metadata, store.loadOrFail(this.id()));
    }

    @Test
    public void testSaveDeleteLoad() {
        final S store = this.createStore();

        final SpreadsheetMetadata metadata = this.metadata(ID, "user1@example.com");
        store.save(metadata);
        store.delete(metadata.get(SpreadsheetMetadataPropertyName.SPREADSHEET_ID).get());

        this.loadFailCheck(store, this.id());
    }

    @Test
    public void testCount() {
        final S store = this.createStore();

        final SpreadsheetMetadata a = this.metadata(ID, "user1@example.com");
        final SpreadsheetMetadata b = this.metadata(2, "user22@example.com");
        final SpreadsheetMetadata c = this.metadata(3, "user333@example.com");

        store.save(a);
        store.save(b);
        store.save(c);

        this.countAndCheck(store, 3);
    }

    @Test
    public void testIds() {
        final S store = this.createStore();

        final SpreadsheetMetadata a = this.metadata(ID, "user1@example.com");
        final SpreadsheetMetadata b = this.metadata(2, "user22@example.com");
        final SpreadsheetMetadata c = this.metadata(3, "user333@example.com");

        store.save(a);
        store.save(b);
        store.save(c);

        this.idsAndCheck(store,
                0,
                3,
                a.id().get(), b.id().get(), c.id().get());
    }

    @Test
    public void testIdsWindow() {
        final S store = this.createStore();

        final SpreadsheetMetadata a = this.metadata(ID, "user1@example.com");
        final SpreadsheetMetadata b = this.metadata(2, "user22@example.com");
        final SpreadsheetMetadata c = this.metadata(3, "user333@example.com");
        final SpreadsheetMetadata d = this.metadata(4, "user4444@example.com");

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.idsAndCheck(store,
                ID,
                2,
                b.id().get(), c.id().get());
    }

    @Test
    public void testValues() {
        final S store = this.createStore();

        final SpreadsheetMetadata a = this.metadata(ID, "user1@example.com");
        final SpreadsheetMetadata b = this.metadata(2, "user22@example.com");
        final SpreadsheetMetadata c = this.metadata(3, "user333@example.com");

        store.save(a);
        store.save(b);
        store.save(c);

        this.valuesAndCheck(store,
                a.id().get(),
                3,
                a, b, c);
    }

    @Test
    public void testValuesWindow() {
        final S store = this.createStore();

        final SpreadsheetMetadata a = this.metadata(ID, "user1@example.com");
        final SpreadsheetMetadata b = this.metadata(2, "user22@example.com");
        final SpreadsheetMetadata c = this.metadata(3, "user333@example.com");
        final SpreadsheetMetadata d = this.metadata(4, "user4444@example.com");

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        this.valuesAndCheck(store,
                b.id().get(),
                2, b, c);
    }

    final SpreadsheetMetadata metadata(final long id, final String creator) {
        return this.metadata(SpreadsheetId.with(id), creator);
    }

    final SpreadsheetMetadata metadata(final SpreadsheetId id, final String creator) {
        return SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, id,
                SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse(creator)));
    }
}
