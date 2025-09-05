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
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetMetadataStoreTestCase<S extends SpreadsheetMetadataStore> implements SpreadsheetMetadataStoreTesting<S> {

    SpreadsheetMetadataStoreTestCase() {
        super();
    }

    @Test
    public final void testLoadUnknownFails() {
        this.loadAndCheck(
            this.createStore(),
            this.id()
        );
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
        //noinspection OptionalGetWithoutIsPresent
        store.delete(metadata.get(SpreadsheetMetadataPropertyName.SPREADSHEET_ID).get());

        this.loadAndCheck(
            store,
            this.id()
        );
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

        //noinspection OptionalGetWithoutIsPresent
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

        //noinspection OptionalGetWithoutIsPresent
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

        //noinspection OptionalGetWithoutIsPresent
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
        final S store = this.createStore();

        final SpreadsheetMetadata a = this.metadata(ID, "user1@example.com");
        final SpreadsheetMetadata b = this.metadata(2, "user22@example.com");
        final SpreadsheetMetadata c = this.metadata(3, "user333@example.com");
        final SpreadsheetMetadata d = this.metadata(4, "user4444@example.com");

        store.save(a);
        store.save(b);
        store.save(c);
        store.save(d);

        //noinspection OptionalGetWithoutIsPresent
        this.valuesAndCheck(
            store,
            1,
            2,
            b,
            c
        );
    }

    final SpreadsheetMetadata metadata(final long id, final String creator) {
        return this.metadata(SpreadsheetId.with(id), creator);
    }

    final SpreadsheetMetadata metadata(final SpreadsheetId id,
                                       final String creator) {
        final EmailAddress creatorEmail = EmailAddress.parse(creator);
        final LocalDateTime createDateTime = LocalDateTime.of(1999, 12, 31, 12, 58, 59);
        final EmailAddress modifiedEmail = EmailAddress.parse("modified@example.com");
        final LocalDateTime modifiedDateTime = LocalDateTime.of(2000, 1, 2, 12, 58, 59);

        return SpreadsheetMetadata.EMPTY
            .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, id)
            .set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    creatorEmail,
                    createDateTime,
                    modifiedEmail,
                    modifiedDateTime
                )
            ).set(SpreadsheetMetadataPropertyName.LOCALE, Locale.forLanguageTag("EN-AU"));
    }
}
