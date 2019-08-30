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

import walkingkooka.net.http.server.HttpRequestAttribute;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.context.SpreadsheetContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.store.Store;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link HateosHandler} that invokes {@link SpreadsheetContext#metadataWithDefaults(Optional)}}
 */
final class SpreadsheetContextLoadMetadataHateosHandler extends SpreadsheetContextSpreadsheetMetadataStoreHateosHandler {

    static SpreadsheetContextLoadMetadataHateosHandler with(final SpreadsheetContext context,
                                                            final Store<SpreadsheetId, SpreadsheetMetadata> store) {
        checkContext(context);
        Objects.requireNonNull(store, "store");

        return new SpreadsheetContextLoadMetadataHateosHandler(context, store);
    }

    private SpreadsheetContextLoadMetadataHateosHandler(final SpreadsheetContext context,
                                                        final Store<SpreadsheetId, SpreadsheetMetadata> store) {
        super(context, store);
    }

    @Override
    public Optional<SpreadsheetMetadata> handle(final Optional<SpreadsheetId> id,
                                                final Optional<SpreadsheetMetadata> resource,
                                                final Map<HttpRequestAttribute<?>, Object> parameters) {
        final SpreadsheetId spreadsheetId = this.checkIdRequired(id);
        checkResourceEmpty(resource);
        checkParameters(parameters);

        return this.store.load(spreadsheetId);
    }

    @Override
    String operation() {
        return "loadMetadata";
    }
}
