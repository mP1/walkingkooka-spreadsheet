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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.store.SpreadsheetStoreTesting;
import walkingkooka.test.TypeNameTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetMetadataStoreTesting<S extends SpreadsheetMetadataStore> extends SpreadsheetStoreTesting<S, SpreadsheetId, SpreadsheetMetadata>,
        TypeNameTesting<S> {

    int ID = 1;

    @Test
    default void testSaveWithoutIdFails() {
        final S store = this.createStore();

        assertThrows(SpreadsheetMetadataStoreException.class, () -> {
            store.save(SpreadsheetMetadata.with(Maps.of(SpreadsheetMetadataPropertyName.CREATOR, EmailAddress.parse("user@example.com"))));
        });
    }

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
