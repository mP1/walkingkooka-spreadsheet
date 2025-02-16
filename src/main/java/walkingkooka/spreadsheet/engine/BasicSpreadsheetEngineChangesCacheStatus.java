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

package walkingkooka.spreadsheet.engine;

/**
 * Tracks the status of a {@link BasicSpreadsheetEngineChangesCache}
 */
enum BasicSpreadsheetEngineChangesCacheStatus {

    LOAD,

    SAVE,

    DELETE,

    /**
     * A references that was loaded only because it is a reference and not selected.
     */
    LOAD_REFERENCE,

    MISSING;

    void setValue() {
        if(this == DELETE || this == MISSING) {
            throw new IllegalStateException("Cannot set value for " + this);
        }
    }

    void value() {
        if(this == DELETE || this == MISSING || this == LOAD_REFERENCE) {
            throw new IllegalStateException("Cannot get value for " + this);
        }
    }
}
