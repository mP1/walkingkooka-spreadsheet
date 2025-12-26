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

import walkingkooka.Cast;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

final class BasicSpreadsheetMetadataContext implements SpreadsheetMetadataContext {

    static BasicSpreadsheetMetadataContext with(final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                                final SpreadsheetMetadataStore store) {
        return new BasicSpreadsheetMetadataContext(
            Objects.requireNonNull(createMetadata, "createMetadata"),
            Objects.requireNonNull(store, "store")
        );
    }

    private BasicSpreadsheetMetadataContext(final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                            final SpreadsheetMetadataStore store) {
        super();

        this.createMetadata = createMetadata;
        this.store = store;
    }

    @Override
    public SpreadsheetMetadata createMetadata(final EmailAddress user,
                                              final Optional<Locale> locale) {
        Objects.requireNonNull(user, "user");
        Objects.requireNonNull(locale, "locale");

        return this.saveMetadata(
            this.createMetadata.apply(
                user,
                locale
            )
        );
    }

    private final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata;

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        return this.store.load(id);
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        return this.store.save(metadata);
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        this.store.delete(id);
    }

    @Override
    public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                   final int offset,
                                                                   final int count) {
        return this.store.findByName(
            name,
            offset,
            count
        );
    }

    private final SpreadsheetMetadataStore store;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
            this.createMetadata,
            this.store
        );
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof BasicSpreadsheetMetadataContext &&
                this.equals0(Cast.to(other));
    }

    private boolean equals0(final BasicSpreadsheetMetadataContext other) {
        return this.createMetadata.equals(other.createMetadata) &&
            this.store.equals(other.store);
    }

    @Override
    public String toString() {
        return this.store.toString();
    }
}
