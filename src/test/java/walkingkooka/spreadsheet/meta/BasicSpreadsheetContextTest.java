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
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetContextTest implements SpreadsheetContextTesting<BasicSpreadsheetContext> {

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        59
    );

    private final static BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> CREATE_METADATA = (e, dl) -> SpreadsheetMetadata.EMPTY;
    private final static SpreadsheetMetadataStore STORE = SpreadsheetMetadataStores.fake();
    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullCreateMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                null,
                STORE,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStoreFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                CREATE_METADATA,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                CREATE_METADATA,
                STORE,
                null
            )
        );
    }

    @Override
    public BasicSpreadsheetContext createContext() {
        return BasicSpreadsheetContext.with(
            CREATE_METADATA,
            SpreadsheetMetadataStores.treeMap(
                SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.LOCALE,
                    Locale.ENGLISH
                ),
                () -> NOW
            ),
            PROVIDER_CONTEXT
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetContext> type() {
        return BasicSpreadsheetContext.class;
    }
}
