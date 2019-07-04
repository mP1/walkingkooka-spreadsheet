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

package walkingkooka.spreadsheet.store.meta;

import walkingkooka.spreadsheet.SpreadsheetException;

/**
 * Base class for all {@link SpreadsheetMetadataStore} exceptions.
 */
public class SpreadsheetMetadataStoreException extends SpreadsheetException {

    private static final long serialVersionUID = 1L;

    protected SpreadsheetMetadataStoreException() {
        super();
    }

    public SpreadsheetMetadataStoreException(final String message) {
        super(message);
    }

    public SpreadsheetMetadataStoreException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
