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

package walkingkooka.spreadsheet.storage;

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContext;
import walkingkooka.storage.StorageContext;

import java.util.Optional;
import java.util.function.Function;

public final class SpreadsheetStorageContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetStorageContext}
     */
    public static SpreadsheetStorageContext basic(final SpreadsheetEngine spreadsheetEngine,
                                                  final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                                                  final Function<SpreadsheetId, Optional<SpreadsheetEngineContext>> spreadsheetIdToSpreadsheetEngineContext,
                                                  final SpreadsheetMetadataContext spreadsheetMetadataContext,
                                                  final StorageContext storageContext) {
        return BasicSpreadsheetStorageContext.with(
            spreadsheetEngine,
            spreadsheetEnvironmentContext,
            spreadsheetIdToSpreadsheetEngineContext,
            spreadsheetMetadataContext,
            storageContext
        );
    }

    /**
     * {@see FakeSpreadsheetStorageContext}
     */
    public static FakeSpreadsheetStorageContext fake() {
        return new FakeSpreadsheetStorageContext();
    }

    /**
     * {@see SpreadsheetContextSpreadsheetStorageContext}
     */
    public static SpreadsheetStorageContext spreadsheetContext(final SpreadsheetContext spreadsheetContext) {
        return SpreadsheetContextSpreadsheetStorageContext.with(spreadsheetContext);
    }

    /**
     * Stop creation
     */
    private SpreadsheetStorageContexts() {
        throw new UnsupportedOperationException();
    }
}
