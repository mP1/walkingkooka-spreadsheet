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
import walkingkooka.spreadsheet.SpreadsheetId;
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

    @Test
    public void testCreate() {
        final EmailAddress creator = EmailAddress.parse("creator1@example.com");
        final Optional<Locale> locale = Optional.of(
                Locale.forLanguageTag("EN-AU")
        );

        this.checkEquals(
                CREATE_TEMPLATE.set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                        SpreadsheetId.with(1)
                ).set(
                        SpreadsheetMetadataPropertyName.CREATOR,
                        creator
                ).set(
                        SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                        NOW
                ).set(
                        SpreadsheetMetadataPropertyName.MODIFIED_BY,
                        creator
                ).set(
                        SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME,
                        NOW
                ).set(
                        SpreadsheetMetadataPropertyName.LOCALE,
                        locale.get()
                ),
                this.createStore()
                        .create(
                                creator,
                                locale
                        )
        );
    }

    @Test
    public void testCreateWithoutLocale() {
        final EmailAddress creator = EmailAddress.parse("creator1@example.com");
        final Optional<Locale> locale = Optional.empty();

        this.checkEquals(
                CREATE_TEMPLATE.set(
                        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                        SpreadsheetId.with(1)
                ).set(
                        SpreadsheetMetadataPropertyName.CREATOR,
                        creator
                ).set(
                        SpreadsheetMetadataPropertyName.CREATE_DATE_TIME,
                        NOW
                ).set(
                        SpreadsheetMetadataPropertyName.MODIFIED_BY,
                        creator
                ).set(
                        SpreadsheetMetadataPropertyName.MODIFIED_DATE_TIME,
                        NOW
                ),
                this.createStore()
                        .create(
                                creator,
                                locale
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

        this.toStringAndCheck(store, "[{\n" +
                "  \"spreadsheet-id\": \"1\",\n" +
                "  \"create-date-time\": \"1999-12-31T12:58:59\",\n" +
                "  \"creator\": \"user1@example.com\",\n" +
                "  \"locale\": \"en-AU\",\n" +
                "  \"modified-by\": \"modified@example.com\",\n" +
                "  \"modified-date-time\": \"2000-01-02T12:58:59\"\n" +
                "}, {\n" +
                "  \"spreadsheet-id\": \"2\",\n" +
                "  \"create-date-time\": \"1999-12-31T12:58:59\",\n" +
                "  \"creator\": \"user2@example.com\",\n" +
                "  \"locale\": \"en-AU\",\n" +
                "  \"modified-by\": \"modified@example.com\",\n" +
                "  \"modified-date-time\": \"2000-01-02T12:58:59\"\n" +
                "}]");
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
