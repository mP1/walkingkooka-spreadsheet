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

package walkingkooka.spreadsheet.meta.store;

import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.store.Store;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * A {@link Store} that holds all spreadsheet to metadata.
 */
public interface SpreadsheetMetadataStore extends Store<SpreadsheetId, SpreadsheetMetadata> {

    /**
     * The preferred way to create and save a new {@link SpreadsheetMetadata}, populating required fields,
     * including setting of numerous defaults.
     */
    SpreadsheetMetadata create(final EmailAddress user,
                               final Optional<Locale> locale);


    /**
     * Finds all {@link SpreadsheetMetadata} using the text to match {@link SpreadsheetName}.
     */
    List<SpreadsheetMetadata> findByName(final String name,
                                         final int offset,
                                         final int count);
}