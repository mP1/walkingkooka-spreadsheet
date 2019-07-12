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

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.store.SpreadsheetStore;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HateosHandler} that invokes {@link SpreadsheetContext#metadataWithDefaults()}
 */
final class SpreadsheetContextCreateAndSaveMetadataHateosHandler extends SpreadsheetContextHateosHandler<SpreadsheetId,
        SpreadsheetMetadata,
        HateosResource<Range<SpreadsheetId>>> {

    static SpreadsheetContextCreateAndSaveMetadataHateosHandler with(final SpreadsheetContext context,
                                                                     final SpreadsheetStore<SpreadsheetId, SpreadsheetMetadata> store) {
        checkContext(context);
        Objects.requireNonNull(store, "store");

        return new SpreadsheetContextCreateAndSaveMetadataHateosHandler(context, store);
    }

    private SpreadsheetContextCreateAndSaveMetadataHateosHandler(final SpreadsheetContext context,
                                                                 final SpreadsheetStore<SpreadsheetId, SpreadsheetMetadata> store) {
        super(context);
        this.store = store;
    }

    @Override
    public Optional<SpreadsheetMetadata> handle(final Optional<SpreadsheetId> id,
                                                final Optional<SpreadsheetMetadata> resource,
                                                final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIdNotNull(id);
        checkResource(resource);
        checkParameters(parameters);

        return id.map(i -> this.saveMetadata(i, this.checkResourceNotEmpty(resource)))
                .or(() -> this.createMetadata(resource));
    }

    private SpreadsheetMetadata saveMetadata(final SpreadsheetId id,
                                             final SpreadsheetMetadata metadata) {
        return this.store.save(metadata);
    }

    private Optional<SpreadsheetMetadata> createMetadata(final Optional<SpreadsheetMetadata> metadata) {
        checkResourceEmpty(metadata);

        return Optional.of(this.context.metadataWithDefaults());
    }

    private final SpreadsheetStore<SpreadsheetId, SpreadsheetMetadata> store;

    @Override
    public final Optional<HateosResource<Range<SpreadsheetId>>> handleCollection(final Range<SpreadsheetId> ids,
                                                                                 final Optional<HateosResource<Range<SpreadsheetId>>> value,
                                                                                 final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkRangeNotNull(ids);
        checkResource(value);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    String operation() {
        return "metadata";
    }
}
