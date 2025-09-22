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

import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;

import java.time.LocalDateTime;
import java.util.Locale;

public final class BasicSpreadsheetContextTest implements SpreadsheetContextTesting<BasicSpreadsheetContext> {

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58,
        59
    );

    @Override
    public BasicSpreadsheetContext createContext() {
        return BasicSpreadsheetContext.with(
            (e, l) -> SpreadsheetMetadata.EMPTY,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return this.store;
                }

                private final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap(
                    SpreadsheetMetadata.EMPTY.set(
                        SpreadsheetMetadataPropertyName.LOCALE,
                        Locale.ENGLISH
                    ),
                    () -> NOW
                );
            }
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetContext> type() {
        return BasicSpreadsheetContext.class;
    }
}
