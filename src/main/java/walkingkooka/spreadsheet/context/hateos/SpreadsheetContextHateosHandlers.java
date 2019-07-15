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

import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HateosHandler;
import walkingkooka.net.http.server.hateos.HateosResource;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.context.SpreadsheetContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.store.SpreadsheetStore;
import walkingkooka.type.PublicStaticHelper;

/**
 * A collection of factory methods to create various {@link HateosHandler}.
 */
public final class SpreadsheetContextHateosHandlers implements PublicStaticHelper {


    /**
     * {@see SpreadsheetContextCreateAndSaveMetadataHateosHandler}
     */
    public static HateosHandler<SpreadsheetId, SpreadsheetMetadata, HateosResource<Range<SpreadsheetId>>> createAndSaveMetadata(final SpreadsheetContext context,
                                                                                                                                final SpreadsheetStore<SpreadsheetId, SpreadsheetMetadata> store) {
        return SpreadsheetContextCreateAndSaveMetadataHateosHandler.with(context, store);
    }

    /**
     * Stop creation.
     */
    private SpreadsheetContextHateosHandlers() {
        throw new UnsupportedOperationException();
    }
}
