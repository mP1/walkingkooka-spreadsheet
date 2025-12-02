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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetMetadataContextTest implements SpreadsheetMetadataContextTesting<BasicSpreadsheetMetadataContext> {

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        59
    );

    private final static BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> CREATE_METADATA =
        (e, dl) ->
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.create(
                    e,
                    NOW
                )
            ).set(
                SpreadsheetMetadataPropertyName.LOCALE,
                dl.get()
            );
    private final static SpreadsheetMetadataStore STORE = SpreadsheetMetadataStores.fake();

    @Test
    public void testWithNullCreateMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetMetadataContext.with(
                null,
                STORE
            )
        );
    }

    @Test
    public void testWithNullStoreFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetMetadataContext.with(
                CREATE_METADATA,
                null
            )
        );
    }

    // createMetadata...................................................................................................

    @Test
    public void testCreateMetadata() {
        final BasicSpreadsheetMetadataContext context = this.createContext();

        final EmailAddress user = EmailAddress.parse("user@example.com");
        final Optional<Locale> locale = Optional.of(Locale.FRENCH);

        final SpreadsheetMetadata metadata = context.createMetadata(
            user,
            locale
        );

        this.checkNotEquals(
            Optional.empty(),
            metadata.id(),
            "id"
        );

        this.checkEquals(
            user,
            metadata.getOrFail(SpreadsheetMetadataPropertyName.AUDIT_INFO)
                .createdBy(),
            "createdBy"
        );
    }

    @Override
    public BasicSpreadsheetMetadataContext createContext() {
        return BasicSpreadsheetMetadataContext.with(
            CREATE_METADATA,
            SpreadsheetMetadataStores.treeMap()
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetMetadataContext> type() {
        return BasicSpreadsheetMetadataContext.class;
    }
}
