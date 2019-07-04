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

package walkingkooka.spreadsheet.hateos;

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.FakeSpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;

import java.util.Optional;

public final class SpreadsheetContextCreateMetadataHateosHandlerTest extends SpreadsheetContextHateosHandlerTestCase2<SpreadsheetContextCreateMetadataHateosHandler,
        SpreadsheetId, SpreadsheetMetadata> {

    @Test
    public void testHandleIdFails() {
        final SpreadsheetId id = this.id();
        this.handleFails(id,
                Optional.of(SpreadsheetMetadata.EMPTY),
                this.parameters(),
                UnsupportedOperationException.class);
    }

    @Test
    public void testHandleCollectionWildcard() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, this.id(),
                SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com")));

        this.handleCollectionAndCheck(this.createHandler(new FakeSpreadsheetContext() {

                    @Override
                    public SpreadsheetMetadata metadataWithDefaults() {
                        return metadata;
                    }
                }),
                Range.all(),
                Optional.empty(),
                HateosHandler.NO_PARAMETERS,
                Optional.of(metadata));
    }

    @Test
    public void testHandleCollectionIdFails() {
        this.handleCollectionFails(Range.singleton(this.id()),
                Optional.of(SpreadsheetMetadata.EMPTY),
                this.parameters(),
                IllegalArgumentException.class);
    }

    @Test
    public final void testToString() {
        final SpreadsheetContext context = this.context();
        this.toStringAndCheck(this.createHandler(context), context + " createMetadata");
    }

    @Override
    SpreadsheetContextCreateMetadataHateosHandler createHandler(final SpreadsheetContext context) {
        return SpreadsheetContextCreateMetadataHateosHandler.with(context);
    }

    @Override
    public final SpreadsheetId id() {
        return SpreadsheetId.with(123);
    }

    @Override
    public Optional<SpreadsheetMetadata> resource() {
        return Optional.empty();
    }

    @Override
    public Range<SpreadsheetId> collection() {
        return Range.singleton(this.id());
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetContextCreateMetadataHateosHandler> type() {
        return Cast.to(SpreadsheetContextCreateMetadataHateosHandler.class);
    }
}
