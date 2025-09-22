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
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

final class BasicSpreadsheetContext implements SpreadsheetContext {

    static BasicSpreadsheetContext with(final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                        final SpreadsheetStoreRepository repository) {
        return new BasicSpreadsheetContext(
            Objects.requireNonNull(createMetadata, "createMetadata"),
            Objects.requireNonNull(repository, "repository")
        );
    }

    private BasicSpreadsheetContext(final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                    final SpreadsheetStoreRepository repository) {
        super();

        this.createMetadata = createMetadata;
        this.repository = repository;
    }

    @Override
    public SpreadsheetMetadata createMetadata(final EmailAddress user,
                                              final Optional<Locale> locale) {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(locale, "locale");

        return this.createMetadata.apply(
            user,
            locale
        );
    }

    private final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata;

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        return this.repository.metadatas()
            .load(id);
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        return this.repository.metadatas()
            .save(metadata);
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        this.repository.metadatas()
            .delete(id);
    }

    private final SpreadsheetStoreRepository repository;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.repository.toString();
    }
}
