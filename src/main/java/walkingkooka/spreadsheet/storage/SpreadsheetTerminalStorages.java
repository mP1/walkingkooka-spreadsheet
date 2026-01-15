/*
 * Copyright 2025 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.storage.Storage;

/**
 * A collection of {@link Storage} for a spreadsheet terminal.
 */
public final class SpreadsheetTerminalStorages implements PublicStaticHelper {

    /**
     * {@see SpreadsheetTerminalStorageSpreadsheetCell}
     */
    public static Storage<SpreadsheetStorageContext> cell() {
        return SpreadsheetTerminalStorageSpreadsheetCell.INSTANCE;
    }

    /**
     * {@see SpreadsheetTerminalStorageSpreadsheetLabel}
     */
    public static Storage<SpreadsheetStorageContext> label() {
        return SpreadsheetTerminalStorageSpreadsheetLabel.INSTANCE;
    }

    /**
     * {@see SpreadsheetTerminalStorageSpreadsheetMetadata}
     */
    public static Storage<SpreadsheetStorageContext> metadata() {
        return SpreadsheetTerminalStorageSpreadsheetMetadata.INSTANCE;
    }

    /**
     * {@see SpreadsheetTerminalStorageRouter}
     */
    public static Storage<SpreadsheetStorageContext> router(final Storage<SpreadsheetStorageContext> cells,
                                                            final Storage<SpreadsheetStorageContext> labels,
                                                            final Storage<SpreadsheetStorageContext> metadatas,
                                                            final Storage<SpreadsheetStorageContext> other) {
        return SpreadsheetTerminalStorageRouter.with(
            cells,
            labels,
            metadatas,
            other
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetTerminalStorages() {
        throw new UnsupportedOperationException();
    }
}
