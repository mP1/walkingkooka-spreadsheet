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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.datetime.HasNowTesting;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.store.StoreWatcher;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataContextBasicTest implements SpreadsheetMetadataContextTesting2<SpreadsheetMetadataContextBasic>,
    HashCodeEqualsDefinedTesting2<SpreadsheetMetadataContextBasic>,
    HasNowTesting {

    private final static SpreadsheetMetadataCreator CREATE_METADATA =
        (e, dl) ->
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.create(
                    e,
                    NOW
                )
            ).set(
                SpreadsheetMetadataPropertyName.LOCALE,
                dl.get()
            );
    private final static SpreadsheetMetadataStore STORE = SpreadsheetMetadataStores.fake();

    @Test
    public void testWithNullCreateMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataContextBasic.with(
                null,
                STORE
            )
        );
    }

    @Test
    public void testWithNullStoreFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataContextBasic.with(
                CREATE_METADATA,
                null
            )
        );
    }

    // createMetadata...................................................................................................

    @Test
    public void testCreateMetadata() {
        final SpreadsheetMetadataContextBasic context = this.createContext();

        final EmailAddress user = EmailAddress.parse("user@example.com");
        final Optional<Locale> locale = Optional.of(Locale.FRENCH);

        final SpreadsheetMetadata metadata = context.createMetadata(
            user,
            locale
        );

        this.checkNotEquals(
            Optional.empty(),
            metadata.id(),
            "id"
        );

        this.checkEquals(
            user,
            metadata.getOrFail(SpreadsheetMetadataPropertyName.AUDIT_INFO)
                .createdBy(),
            "createdBy"
        );
    }

    @Test
    public void testAddWatcherAndSave() {
        final SpreadsheetMetadataContextBasic context = this.createContext();

        this.fired = true;

        context.addMetadataWatcher(
            new StoreWatcher<>() {
                @Override
                public void onValueChange(final Optional<SpreadsheetMetadata> oldValue,
                                          final Optional<SpreadsheetMetadata> newValue) {
                    SpreadsheetMetadataContextBasicTest.this.fired = true;
                }
            }
        );

        final EmailAddress user = EmailAddress.parse("user@example.com");
        final Optional<Locale> locale = Optional.of(Locale.FRENCH);

        context.createMetadata(
            user,
            locale
        );

        this.checkEquals(
            true,
            this.fired,
            "fired"
        );
    }

    private boolean fired;

    @Override
    public SpreadsheetMetadataContextBasic createContext() {
        return SpreadsheetMetadataContextBasic.with(
            CREATE_METADATA,
            SpreadsheetMetadataStores.treeMap()
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentCreateMetadataFunction() {
        this.checkNotEquals(
            SpreadsheetMetadataContextBasic.with(
                (e, l) -> {
                    throw new UnsupportedOperationException();
                },
                SpreadsheetMetadataStores.treeMap()
            )
        );
    }

    @Test
    public void testEqualsDifferentMetadataStore() {
        this.checkNotEquals(
            SpreadsheetMetadataContextBasic.with(
                CREATE_METADATA,
                SpreadsheetMetadataStores.fake()
            )
        );
    }

    @Override
    public SpreadsheetMetadataContextBasic createObject() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetMetadataContextBasic> type() {
        return SpreadsheetMetadataContextBasic.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
