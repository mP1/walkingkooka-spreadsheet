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

import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetId;

import java.util.Locale;
import java.util.Optional;

public interface SpreadsheetContextDelegator extends SpreadsheetContext {

    @Override
    default SpreadsheetMetadata createMetadata(final EmailAddress user,
                                               final Optional<Locale> locale) {
        return this.spreadsheetContext()
            .createMetadata(
                user,
                locale
            );
    }

    @Override
    default Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        return this.spreadsheetContext()
            .loadMetadata(id);
    }

    @Override
    default SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        return this.spreadsheetContext()
            .saveMetadata(metadata);
    }

    @Override
    default void deleteMetadata(final SpreadsheetId id) {
        this.spreadsheetContext()
            .deleteMetadata(id);
    }

    // HasProviderContext...............................................................................................

    @Override
    default ProviderContext providerContext() {
        return this.spreadsheetContext()
            .providerContext();
    }

    SpreadsheetContext spreadsheetContext();
}
