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

import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

final class BasicSpreadsheetContext implements SpreadsheetContext,
    LocaleContextDelegator {

    static BasicSpreadsheetContext with(final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                        final SpreadsheetMetadataStore store,
                                        final LocaleContext localeContext,
                                        final ProviderContext providerContext) {
        return new BasicSpreadsheetContext(
            Objects.requireNonNull(createMetadata, "createMetadata"),
            Objects.requireNonNull(store, "store"),
            Objects.requireNonNull(localeContext, "localeContext"),
            Objects.requireNonNull(providerContext, "providerContext")
        );
    }

    private BasicSpreadsheetContext(final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                    final SpreadsheetMetadataStore store,
                                    final LocaleContext localeContext,
                                    final ProviderContext providerContext) {
        super();

        this.createMetadata = createMetadata;
        this.store = store;
        this.localeContext = localeContext;
        this.providerContext = providerContext;
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

    private final SpreadsheetMetadataStore store;

    // LocaleContext....................................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.localeContext;
    }

    private final LocaleContext localeContext;

    @Override
    public SpreadsheetContext setLocale(final Locale locale) {
        final LocaleContext before = this.localeContext;
        final LocaleContext after = before.setLocale(locale);

        return before.equals(after) ?
            this :
            new BasicSpreadsheetContext(
                this.createMetadata,
                this.store,
                after,
                this.providerContext
            );
    }

    // HasProviderContext...............................................................................................

    @Override
    public ProviderContext providerContext() {
        return this.providerContext;
    }

    private final ProviderContext providerContext;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.store.toString();
    }
}
