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
import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.context.FakeSpreadsheetContext;
import walkingkooka.spreadsheet.context.SpreadsheetContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetContextCreateAndSaveMetadataHateosHandlerTest extends SpreadsheetContextHateosHandlerTestCase2<SpreadsheetContextCreateAndSaveMetadataHateosHandler,
        SpreadsheetId,
        SpreadsheetMetadata,
        HateosResource<Range<SpreadsheetId>>> {

    @Test
    public void testWithNullStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetContextCreateAndSaveMetadataHateosHandler.with(this.context(), null);
        });
    }

    // handle...........................................................................................................

    @Test
    public void testHandleNullParametersFails() {
        this.handleFails(this.id(),
                Optional.of(this.metadata()),
                null,
                NullPointerException.class);
    }

    @Test
    public void testHandleIdWithoutMetadataResourceFails() {
        final Optional<SpreadsheetId> id = this.id();

        this.handleFails(id,
                Optional.empty(),
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Test
    public void testHandleWithoutIdCreatesMetadata() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, this.spreadsheetId(),
                SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com")));

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetContext() {

                    @Override
                    public SpreadsheetMetadata metadataWithDefaults() {
                        return metadata;
                    }
                }),
                Optional.empty(),
                Optional.empty(),
                HateosHandler.NO_PARAMETERS,
                Optional.of(metadata));
    }

    @Test
    public void testHandleIdWithMetadataSaves() {
        final Optional<SpreadsheetId> id = this.id();

        final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
        final SpreadsheetContextCreateAndSaveMetadataHateosHandler handler = SpreadsheetContextCreateAndSaveMetadataHateosHandler.with(this.context(), store);

        final SpreadsheetMetadata metadata = this.metadata();

        this.handleAndCheck(handler,
                id,
                Optional.of(metadata),
                this.parameters(),
                Optional.of(metadata));

        assertEquals(Optional.of(metadata), store.load(id.get()), () -> "store missing id=" + id);
    }

    // handleCollection.................................................................................................

    @Test
    public void testHandleCollectionIdFails() {
        this.handleCollectionFails(this.collection(),
                this.collectionResource(),
                this.parameters(),
                UnsupportedOperationException.class);
    }

    @Test
    public final void testToString() {
        final SpreadsheetContext context = this.context();
        this.toStringAndCheck(this.createHandler(context), context + " metadata");
    }

    @Override
    SpreadsheetContextCreateAndSaveMetadataHateosHandler createHandler(final SpreadsheetContext context) {
        return SpreadsheetContextCreateAndSaveMetadataHateosHandler.with(context, this.store());
    }

    @Override
    SpreadsheetContext context() {
        return new FakeSpreadsheetContext() {
            @Override
            public SpreadsheetMetadata metadataWithDefaults() {
                return SpreadsheetContextCreateAndSaveMetadataHateosHandlerTest.this.metadataWithDefaults();
            }
        };
    }

    private SpreadsheetMetadata metadataWithDefaults() {
        return SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                LocalDateTime.of(2000, 12, 31, 12, 58, 59)));
    }

    private SpreadsheetMetadataStore store() {
        return SpreadsheetMetadataStores.treeMap();
    }

    @Override
    public Optional<SpreadsheetId> id() {
        return Optional.of(this.spreadsheetId());
    }

    @Override
    public Range<SpreadsheetId> collection() {
        return Range.singleton(this.spreadsheetId());
    }

    private SpreadsheetMetadata metadata() {
        return SpreadsheetMetadata.with(Map.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, this.spreadsheetId(),
                SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com")));
    }

    private SpreadsheetId spreadsheetId() {
        return SpreadsheetId.with(0x1234);
    }

    @Override
    public Optional<SpreadsheetMetadata> resource() {
        return Optional.empty();
    }

    @Override
    public Optional collectionResource() {
        return Optional.empty();
    }

    @Override
    public Map<HttpRequestAttribute<?>, Object> parameters() {
        return HateosHandler.NO_PARAMETERS;
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetContextCreateAndSaveMetadataHateosHandler> type() {
        return Cast.to(SpreadsheetContextCreateAndSaveMetadataHateosHandler.class);
    }
}
