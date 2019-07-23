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
import walkingkooka.net.header.AcceptLanguage;
import walkingkooka.net.header.HttpHeaderName;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.context.FakeSpreadsheetContext;
import walkingkooka.spreadsheet.context.SpreadsheetContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.store.Store;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetContextCreateAndSaveMetadataHateosHandlerTest extends SpreadsheetContextSpreadsheetMetadataStoreHateosHandlerTestCase<SpreadsheetContextCreateAndSaveMetadataHateosHandler> {

    SpreadsheetContextCreateAndSaveMetadataHateosHandlerTest() {
        super();
    }

    // handle...........................................................................................................

    @Test
    public void testHandleIdWithoutMetadataResourceFails() {
        final Optional<SpreadsheetId> id = this.id();

        this.handleFails(id,
                Optional.empty(),
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Test
    public void testHandleWithoutIdCreatesMetadataWithLocale() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, this.spreadsheetId(),
                SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com")));

        final Locale locale = Locale.CANADA_FRENCH;

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetContext() {

                    @Override
                    public SpreadsheetMetadata metadataWithDefaults(final Optional<Locale> locale) {
                        return metadata.set(SpreadsheetMetadataPropertyName.LOCALE, locale.get());
                    }
                }),
                Optional.empty(),
                Optional.empty(),
                Maps.of(HttpHeaderName.ACCEPT_LANGUAGE, AcceptLanguage.parse("en;q=0.8, fr-CA;q=0.9")),
                Optional.of(metadata.set(SpreadsheetMetadataPropertyName.LOCALE, locale)));
    }

    @Test
    public void testHandleWithoutIdCreatesMetadataWithoutLocale() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, this.spreadsheetId(),
                SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com")));

        this.handleAndCheck(this.createHandler(new FakeSpreadsheetContext() {

                    @Override
                    public SpreadsheetMetadata metadataWithDefaults(final Optional<Locale> locale) {
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

    // toString.........................................................................................................

    @Test
    public final void testToString() {
        final SpreadsheetContext context = this.context();
        this.toStringAndCheck(this.createHandler(context), context + " saveMetadata");
    }

    // helpers..........................................................................................................

    @Override
    SpreadsheetContextCreateAndSaveMetadataHateosHandler createHandler(final SpreadsheetContext context,
                                                                       final Store<SpreadsheetId, SpreadsheetMetadata> store) {
        return SpreadsheetContextCreateAndSaveMetadataHateosHandler.with(context, store);
    }

    @Override
    SpreadsheetContext context() {
        return new FakeSpreadsheetContext() {
            @Override
            public SpreadsheetMetadata metadataWithDefaults(final Optional<Locale> locale) {
                return SpreadsheetContextCreateAndSaveMetadataHateosHandlerTest.this.metadataWithDefaults();
            }
        };
    }

    private SpreadsheetMetadata metadataWithDefaults() {
        return SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                LocalDateTime.of(2000, 12, 31, 12, 58, 59)));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetContextCreateAndSaveMetadataHateosHandler> type() {
        return SpreadsheetContextCreateAndSaveMetadataHateosHandler.class;
    }
}
