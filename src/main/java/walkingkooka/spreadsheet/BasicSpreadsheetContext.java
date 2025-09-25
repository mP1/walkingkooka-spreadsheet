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

package walkingkooka.spreadsheet;

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContextDelegator;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContextDelegator;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

final class BasicSpreadsheetContext implements SpreadsheetContext,
    EnvironmentContextDelegator,
    LocaleContextDelegator,
    SpreadsheetProviderDelegator {

    static BasicSpreadsheetContext with(final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                        final SpreadsheetStoreRepository storeRepository,
                                        final SpreadsheetProvider spreadsheetProvider,
                                        final EnvironmentContext environmentContext,
                                        final LocaleContext localeContext,
                                        final ProviderContext providerContext) {
        return new BasicSpreadsheetContext(
            Objects.requireNonNull(createMetadata, "createMetadata"),
            Objects.requireNonNull(storeRepository, "storeRepository"),
            Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider"),
            Objects.requireNonNull(environmentContext, "environmentContext"),
            Objects.requireNonNull(localeContext, "localeContext"),
            Objects.requireNonNull(providerContext, "providerContext")
        );
    }

    private BasicSpreadsheetContext(final BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> createMetadata,
                                    final SpreadsheetStoreRepository storeRepository,
                                    final SpreadsheetProvider spreadsheetProvider,
                                    final EnvironmentContext environmentContext,
                                    final LocaleContext localeContext,
                                    final ProviderContext providerContext) {
        super();

        this.createMetadata = createMetadata;
        this.storeRepository = storeRepository;
        this.spreadsheetProvider = spreadsheetProvider;
        
        this.environmentContext = environmentContext;
        this.localeContext = localeContext;
        this.providerContext = providerContext;
    }

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.storeRepository;
    }

    private final SpreadsheetStoreRepository storeRepository;

    // SpreadsheetContext...............................................................................................

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

        return this.storeRepository.metadatas()
            .load(id);
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        return this.storeRepository
            .metadatas()
            .save(metadata);
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        this.storeRepository
            .metadatas()
            .delete(id);
    }

    @Override
    public List<SpreadsheetMetadata> findMetadataBySpreadsheetName(final String name,
                                                                   final int offset,
                                                                   final int count) {
        return this.storeRepository
            .metadatas()
            .findByName(
                name,
                offset,
                count
            );
    }

    // EnvironmentContextDelegator......................................................................................

    @Override
    public SpreadsheetContext cloneEnvironment() {
        final EnvironmentContext before = this.environmentContext;
        final EnvironmentContext cloned = before.cloneEnvironment();

        return before.equals(cloned) ?
            this :
            with(
                this.createMetadata,
                this.storeRepository,
                this.spreadsheetProvider,
                cloned,
                this.localeContext,
                this.providerContext
            );
    }

    @Override
    public <T> SpreadsheetContext setEnvironmentValue(final EnvironmentValueName<T> name,
                                                      final T value) {
        this.environmentContext.setEnvironmentValue(
            name,
            value
        );
        return this;
    }

    @Override
    public SpreadsheetContext removeEnvironmentValue(final EnvironmentValueName<?> name) {
        this.environmentContext.removeEnvironmentValue(name);
        return this;
    }

    @Override
    public Locale locale() {
        return this.environmentContext.locale();
    }

    @Override
    public SpreadsheetContext setLocale(final Locale locale) {
        this.environmentContext.setLocale(locale);

        return this;
    }

    @Override
    public EnvironmentContext environmentContext() {
        return this.environmentContext;
    }

    private final EnvironmentContext environmentContext;

    // LocaleContext....................................................................................................

    @Override
    public LocaleContext localeContext() {
        return this.localeContext;
    }

    private LocaleContext localeContext;

    // HasProviderContext...............................................................................................

    @Override
    public ProviderContext providerContext() {
        return this.providerContext;
    }

    private final ProviderContext providerContext;

    // HasSpreadsheetProvider...........................................................................................

    @Override
    public SpreadsheetProvider spreadsheetProvider() {
        return this.spreadsheetProvider;
    }

    private final SpreadsheetProvider spreadsheetProvider;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.storeRepository.toString();
    }
}
