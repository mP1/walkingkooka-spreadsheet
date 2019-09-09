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

package walkingkooka.spreadsheet.context.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.context.FakeSpreadsheetContext;
import walkingkooka.spreadsheet.context.SpreadsheetContext;
import walkingkooka.spreadsheet.context.SpreadsheetContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetContextLoadMetadataHateosHandlerTest extends SpreadsheetContextSpreadsheetMetadataStoreHateosHandlerTestCase<SpreadsheetContextLoadMetadataHateosHandler> {

    SpreadsheetContextLoadMetadataHateosHandlerTest() {
        super();
    }

    // handle...........................................................................................................

    @Test
    public void testHandleIdWithMetadataResourceFails() {
        final Optional<SpreadsheetId> id = this.id();

        this.handleFails(id,
                Optional.of(this.metadataWithDefaults()),
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Test
    public void testHandleLoad() {
        final SpreadsheetId id = this.spreadsheetId();
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, id,
                SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com")));

        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
        store.save(metadata);

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetContext() {
                    @Override
                    public SpreadsheetStoreRepository storeRepository(final SpreadsheetId i) {
                        assertEquals(id, i, "spreadsheetId");
                        return new FakeSpreadsheetStoreRepository() {
                            @Override
                            public SpreadsheetMetadataStore metadatas() {
                                return store;
                            }
                        };
                    }
                }),
                Optional.of(id),
                Optional.empty(),
                HateosHandler.NO_PARAMETERS,
                Optional.of(metadata));
    }

    // toString.........................................................................................................

    @Test
    public final void testToString() {
        final SpreadsheetContext context = this.context();
        this.toStringAndCheck(this.createHandler(context), context + " loadMetadata");
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetContextLoadMetadataHateosHandler createHandler(final SpreadsheetContext context) {
        return SpreadsheetContextLoadMetadataHateosHandler.with(context);
    }

    @Override
    SpreadsheetContext context() {
        return SpreadsheetContexts.fake();
    }

    private SpreadsheetMetadata metadataWithDefaults() {
        return SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                LocalDateTime.of(2000, 12, 31, 12, 58, 59)));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetContextLoadMetadataHateosHandler> type() {
        return SpreadsheetContextLoadMetadataHateosHandler.class;
    }
}
