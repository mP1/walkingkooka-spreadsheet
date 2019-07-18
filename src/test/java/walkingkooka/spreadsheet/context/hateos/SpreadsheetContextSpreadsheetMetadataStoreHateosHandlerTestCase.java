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
import walkingkooka.compare.Range;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.context.SpreadsheetContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.store.Store;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetContextSpreadsheetMetadataStoreHateosHandlerTestCase<H extends SpreadsheetContextSpreadsheetMetadataStoreHateosHandler>
        extends SpreadsheetContextHateosHandlerTestCase2<H,
        SpreadsheetId,
        SpreadsheetMetadata,
        HateosResource<Range<SpreadsheetId>>> {

    @Test
    public final void testWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createHandler(this.context(), null);
        });
    }

    // handle...........................................................................................................

    @Test
    public final void testHandleNullParametersFails() {
        this.handleFails(this.id(),
                Optional.empty(),
                null,
                NullPointerException.class);
    }

    // handleCollection.................................................................................................

    @Test
    public final void testHandleCollectionIdFails() {
        this.handleCollectionFails(this.collection(),
                this.collectionResource(),
                this.parameters(),
                UnsupportedOperationException.class);
    }

    // helpers..........................................................................................................

    @Override
    final H createHandler(final SpreadsheetContext context) {
        return this.createHandler(context, this.store());
    }

    abstract H createHandler(final SpreadsheetContext context,
                             final Store<SpreadsheetId, SpreadsheetMetadata> store);

    private SpreadsheetMetadata metadataWithDefaults() {
        return SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                LocalDateTime.of(2000, 12, 31, 12, 58, 59)));
    }

    private SpreadsheetMetadataStore store() {
        return SpreadsheetMetadataStores.treeMap();
    }

    @Override
    public final Optional<SpreadsheetId> id() {
        return Optional.of(this.spreadsheetId());
    }

    @Override
    public final Range<SpreadsheetId> collection() {
        return Range.singleton(this.spreadsheetId());
    }

    final SpreadsheetMetadata metadata() {
        return SpreadsheetMetadata.with(Map.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, this.spreadsheetId(),
                SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com")));
    }

    final SpreadsheetId spreadsheetId() {
        return SpreadsheetId.with(0x1234);
    }

    @Override
    public final Optional<SpreadsheetMetadata> resource() {
        return Optional.empty();
    }

    @Override
    public final Optional<HateosResource<Range<SpreadsheetId>>> collectionResource() {
        return Optional.empty();
    }

    @Override
    public final Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }
}
