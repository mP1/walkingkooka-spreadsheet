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
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.TreeMap;

public final class TreeMapSpreadsheetMetadataStoreTest extends SpreadsheetMetadataStoreTestCase<TreeMapSpreadsheetMetadataStore> {

    private final static Locale DEFAULT_LOCALE = Locale.forLanguageTag("FR");

    private final static SpreadsheetMetadata CREATE_TEMPLATE = SpreadsheetMetadata.EMPTY.setDefaults(
        SpreadsheetMetadata.NON_LOCALE_DEFAULTS.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            DEFAULT_LOCALE
        )
    );

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58
    );

    private final static EmailAddress CREATOR = EmailAddress.parse("creator1@example.com");
    private final static Optional<Locale> LOCALE = Optional.of(
        Locale.forLanguageTag("EN-AU")
    );

    @Test
    public void testCreate() {
        this.checkEquals(
            CREATE_TEMPLATE.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SpreadsheetId.with(1)
            ).set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    CREATOR,
                    NOW,
                    CREATOR,
                    NOW
                )
            ).set(
                SpreadsheetMetadataPropertyName.LOCALE,
                LOCALE.get()
            ),
            this.createStore()
                .create(
                    CREATOR,
                    LOCALE
                )
        );
    }

    @Test
    public void testCreateWithoutLocale() {
        final Optional<Locale> locale = Optional.empty();

        this.checkEquals(
            CREATE_TEMPLATE.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                SpreadsheetId.with(1)
            ).set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    CREATOR,
                    NOW,
                    CREATOR,
                    NOW
                )
            ),
            this.createStore()
                .create(
                    CREATOR,
                    locale
                )
        );
    }

    // findByName.......................................................................................................

    @Test
    public void testFindByNameWithExactNameMatch() {
        final TreeMapSpreadsheetMetadataStore store = this.createStore();

        final SpreadsheetMetadata metadata1 = store.save(
            createMetadata("Hello1")
        );

        final SpreadsheetMetadata metadata2 = store.save(
            createMetadata("Different2")
        );

        this.findByNameAndCheck(
            store,
            "Hello1",
            0,
            10,
            metadata1
        );
    }

    @Test
    public void testFindByNameWithMultipleCommonPrefix() {
        final TreeMapSpreadsheetMetadataStore store = this.createStore();

        final SpreadsheetMetadata metadata1 = store.save(
            createMetadata("Hello1")
        );

        final SpreadsheetMetadata metadata2 = store.save(
            createMetadata("Different2")
        );

        final SpreadsheetMetadata metadata3 = store.save(
            createMetadata("Hello2")
        );

        final SpreadsheetMetadata metadata4 = store.save(
            createMetadata("Hello3")
        );

        this.findByNameAndCheck(
            store,
            "Hello",
            0,
            10,
            metadata1,
            metadata3,
            metadata4
        );
    }

    @Test
    public void testFindByNameWithMultipleCommonPrefixAndOffsetAndCount() {
        final TreeMapSpreadsheetMetadataStore store = this.createStore();

        final SpreadsheetMetadata metadata1 = store.save(
            createMetadata("Hello1")
        );

        final SpreadsheetMetadata metadata2 = store.save(
            createMetadata("Different2")
        );

        final SpreadsheetMetadata metadata3 = store.save(
            createMetadata("Hello2")
        );

        final SpreadsheetMetadata metadata4 = store.save(
            createMetadata("Hello3")
        );

        this.findByNameAndCheck(
            store,
            "Hello",
            1,
            1,
            //metadata1,
            metadata3
            //metadata4
        );
    }

    private static SpreadsheetMetadata createMetadata(final String name) {
        return CREATE_TEMPLATE.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetName.with(name)
        ).set(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.with(
                CREATOR,
                NOW,
                CREATOR,
                NOW
            )
        );
    }

    @Override
    public TreeMapSpreadsheetMetadataStore createStore() {
        return TreeMapSpreadsheetMetadataStore.with(
            CREATE_TEMPLATE,
            () -> NOW
        );
    }

    // toString.........................................................................................................
    @Test
    public void testToString() {
        final TreeMapSpreadsheetMetadataStore store = this.createStore();
        store.save(this.metadata(1, "user1@example.com"));
        store.save(this.metadata(2, "user2@example.com"));

        this.toStringAndCheck(
            store,
            "[{\n" +
                "  \"spreadsheetId\": \"1\",\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"user1@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"locale\": \"en-AU\"\n" +
                "}, {\n" +
                "  \"spreadsheetId\": \"2\",\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"user2@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"locale\": \"en-AU\"\n" +
                "}]"
        );
    }

    // class.........................................................................................................

    @Override
    public Class<TreeMapSpreadsheetMetadataStore> type() {
        return TreeMapSpreadsheetMetadataStore.class;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}
