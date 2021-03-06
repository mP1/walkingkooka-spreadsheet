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

import org.junit.jupiter.api.Test;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.store.StoreTesting;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface SpreadsheetMetadataStoreTesting<S extends SpreadsheetMetadataStore> extends StoreTesting<S, SpreadsheetId, SpreadsheetMetadata>,
        TypeNameTesting<S> {

    int ID = 1;

    @Test
    default void testSaveWithoutRequiredFails() {
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> this.createStore().save(SpreadsheetMetadata.EMPTY));

        final String message = thrown.getMessage();
        final String required = "Missing required properties: create-date-time, creator, locale, modified-by, modified-date-time";

        assertTrue(message.startsWith(required), () -> "Message doesnt start with \"" + required + "\"" + "\n" + message);
    }

    @Override
    default SpreadsheetId id() {
        return SpreadsheetId.with(ID);
    }

    @Override
    default SpreadsheetMetadata value() {
        final EmailAddress creatorEmail = EmailAddress.parse("creator@example.com");
        final LocalDateTime createDateTime = LocalDateTime.of(1999, 12, 31, 12, 58, 59);
        final EmailAddress modifiedEmail = EmailAddress.parse("modified@example.com");
        final LocalDateTime modifiedDateTime = LocalDateTime.of(2000, 1, 2, 12, 58, 59);

        return SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.SPREADSHEET_ID, this.id())
                .set(SpreadsheetMetadataPropertyName.CREATOR, creatorEmail)
                .set(SpreadsheetMetadataPropertyName.CREATE_DATE_TIME, createDateTime)
                .set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH)
                .set(SpreadsheetMetadataPropertyName.MODIFIED_BY, modifiedEmail)
                .set(SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME, modifiedDateTime);
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetMetadataStore.class.getSimpleName();
    }
}
