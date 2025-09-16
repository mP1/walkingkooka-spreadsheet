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
import walkingkooka.collect.list.Lists;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.store.StoreTesting;
import walkingkooka.text.CharSequences;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetMetadataStoreTesting<S extends SpreadsheetMetadataStore> extends StoreTesting<S, SpreadsheetId, SpreadsheetMetadata>,
    TypeNameTesting<S> {

    SpreadsheetMetadata CREATE_TEMPLATE = SpreadsheetMetadata.EMPTY.setDefaults(
        SpreadsheetMetadata.NON_LOCALE_DEFAULTS.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.forLanguageTag("EN-AU")
        )
    );

    // create.............................................................................................................

    @Test
    default void testCreateWithNullCreatorFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .create(
                    null,
                    Optional.empty()
                )
        );
    }

    @Test
    default void testCreateWithNullLocaleFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .create(
                    EmailAddress.parse("creator@example.com"),
                    null
                )
        );
    }

    // findByName.......................................................................................................

    @Test
    default void testFindByNameWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createStore()
                .findByName(
                    null,
                    0, // offset
                    1 // count
                )
        );
    }

    @Test
    default void testFindByNameWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findByName(
                    "Hello",
                    -1, // offset
                    1 // count
                )
        );
    }

    @Test
    default void testFindByNameWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .findByName(
                    "Hello",
                    0, // offset
                    -1 // count
                )
        );
    }

    default void findByNameAndCheck(final SpreadsheetMetadataStore store,
                                    final String name,
                                    final int offset,
                                    final int count,
                                    final SpreadsheetMetadata ... expected) {
        this.findByNameAndCheck(
            store,
            name,
            offset,
            count,
            Lists.of(expected)
        );
    }

    default void findByNameAndCheck(final SpreadsheetMetadataStore store,
                                    final String name,
                                    final int offset,
                                    final int count,
                                    final List<SpreadsheetMetadata> expected) {
        this.checkEquals(
            expected,
            store.findByName(
                name,
                offset,
                count
            ),
            () -> "findByName " + CharSequences.quoteAndEscape(name) + " offset=" + offset + " count=" + count
        );
    }

    // save.............................................................................................................

    int ID = 1;

    @Test
    default void testSaveWithoutRequiredFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> this.createStore()
                .save(SpreadsheetMetadata.EMPTY)
        );

        this.checkEquals(
            "Metadata missing required properties: auditInfo, locale",
            thrown.getMessage()
        );
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
            .set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                this.id()
            ).set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    creatorEmail,
                    createDateTime,
                    modifiedEmail,
                    modifiedDateTime
                )
            ).set(SpreadsheetMetadataPropertyName.LOCALE, Locale.ENGLISH);
    }

    // TypeNameTesting..................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetMetadataStore.class.getSimpleName();
    }
}
