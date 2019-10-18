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

import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.store.StoreTesting;

public interface SpreadsheetMetadataStoreTesting<S extends SpreadsheetMetadataStore> extends StoreTesting<S, SpreadsheetId, SpreadsheetMetadata>,
        TypeNameTesting<S> {

    int ID = 1;

    @Override
    default SpreadsheetId id() {
        return SpreadsheetId.with(ID);
    }

    @Override
    default SpreadsheetMetadata value() {
        return SpreadsheetMetadata.EMPTY.set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, this.id());
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetMetadataStore.class.getSimpleName();
    }
}
