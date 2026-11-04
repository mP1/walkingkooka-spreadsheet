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
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetName;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.TreeMap;

public final class SpreadsheetMetadataStoreTreeMapTest extends SpreadsheetMetadataStoreTestCase<SpreadsheetMetadataStoreTreeMap>
    implements HashCodeEqualsDefinedTesting2<SpreadsheetMetadataStoreTreeMap> {

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58
    );

    private final static EmailAddress CREATOR = EmailAddress.parse("creator1@example.com");

    // findByName.......................................................................................................

    @Test
    public void testFindByNameWithEmpty() {
        final SpreadsheetMetadataStoreTreeMap store = this.createStore();

        final SpreadsheetMetadata metadata1 = store.save(
            createMetadata("Hello1")
        );

        final SpreadsheetMetadata metadata2 = store.save(
            createMetadata("Different2")
        );

        this.findByNameAndCheck(
            store,
            "",
            0,
            10,
            metadata1,
            metadata2
        );
    }

    @Test
    public void testFindByNameWithExactNameMatch() {
        final SpreadsheetMetadataStoreTreeMap store = this.createStore();

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
        final SpreadsheetMetadataStoreTreeMap store = this.createStore();

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
        final SpreadsheetMetadataStoreTreeMap store = this.createStore();

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
        return SpreadsheetMetadata.EMPTY.setDefaults(
            SpreadsheetMetadata.NON_LOCALE_DEFAULTS.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                Locale.forLanguageTag("EN-AU")
            )
        ).set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetName.with(name)
        ).set(
            SpreadsheetMetadataPropertyName.AUDIT_INFO,
            AuditInfo.create(
                CREATOR,
                NOW
            )
        );
    }

    @Override
    public SpreadsheetMetadataStoreTreeMap createStore() {
        return SpreadsheetMetadataStoreTreeMap.empty();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentStore() {
        final SpreadsheetMetadataStoreTreeMap store = SpreadsheetMetadataStoreTreeMap.empty();
        store.save(
            createMetadata("Different")
        );

        this.checkNotEquals(store);
    }

    @Override
    public SpreadsheetMetadataStoreTreeMap createObject() {
        return this.createStore();
    }

    // toString.........................................................................................................
    @Test
    public void testToString() {
        final SpreadsheetMetadataStoreTreeMap store = this.createStore();
        store.save(this.metadata(1, "user1@example.com"));
        store.save(this.metadata(2, "user2@example.com"));

        this.toStringAndCheck(
            store,
            "{1={\n" +
                "  \"spreadsheetId\": \"1\",\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"user1@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"locale\": \"en-AU\"\n" +
                "}, 2={\n" +
                "  \"spreadsheetId\": \"2\",\n" +
                "  \"auditInfo\": {\n" +
                "    \"createdBy\": \"user2@example.com\",\n" +
                "    \"createdTimestamp\": \"1999-12-31T12:58:59\",\n" +
                "    \"modifiedBy\": \"modified@example.com\",\n" +
                "    \"modifiedTimestamp\": \"2000-01-02T12:58:59\"\n" +
                "  },\n" +
                "  \"locale\": \"en-AU\"\n" +
                "}}"
        );
    }

    // class.........................................................................................................

    @Override
    public Class<SpreadsheetMetadataStoreTreeMap> type() {
        return SpreadsheetMetadataStoreTreeMap.class;
    }

    @Override
    public String typeNamePrefix() {
        return "";
    }

    @Override
    public String typeNameSuffix() {
        return TreeMap.class.getSimpleName();
    }
}
