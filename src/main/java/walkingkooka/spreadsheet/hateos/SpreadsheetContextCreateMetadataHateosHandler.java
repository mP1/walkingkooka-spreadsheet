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
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;

import java.util.Map;
import java.util.Optional;

/**
 * A {@link HateosHandler} that invokes {@link SpreadsheetContext#metadataWithDefaults()}
 */
final class SpreadsheetContextCreateMetadataHateosHandler extends SpreadsheetContextHateosHandler<SpreadsheetId, SpreadsheetMetadata> {

    static SpreadsheetContextCreateMetadataHateosHandler with(final SpreadsheetContext context) {
        checkContext(context);

        return new SpreadsheetContextCreateMetadataHateosHandler(context);
    }

    private SpreadsheetContextCreateMetadataHateosHandler(final SpreadsheetContext context) {
        super(context);
    }

    @Override
    public Optional<SpreadsheetMetadata> handle(final SpreadsheetId id,
                                                final Optional<SpreadsheetMetadata> metadata,
                                                final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkId(id);
        checkResource(metadata);
        checkParameters(parameters);

        throw new UnsupportedOperationException();
    }

    @Override
    public final Optional<SpreadsheetMetadata> handleCollection(final Range<SpreadsheetId> ids,
                                                                final Optional<SpreadsheetMetadata> metadata,
                                                                final Map<HttpRequestAttribute<?>, Object> parameters) {
        checkIds(ids, "ids");
        checkResourceEmpty(metadata);
        checkParameters(parameters);

        return Optional.of(this.context.metadataWithDefaults());
    }

    @Override
    String operation() {
        return "createMetadata";
    }
}
