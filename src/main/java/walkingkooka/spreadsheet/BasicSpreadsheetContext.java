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
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderDelegator;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

final class BasicSpreadsheetContext implements SpreadsheetContext,
    EnvironmentContextDelegator,
    LocaleContextDelegator,
    SpreadsheetProviderDelegator {

    static BasicSpreadsheetContext with(final SpreadsheetId spreadsheetId,
                                        final SpreadsheetStoreRepository storeRepository,
                                        final SpreadsheetProvider spreadsheetProvider,
                                        final EnvironmentContext environmentContext,
                                        final LocaleContext localeContext,
                                        final ProviderContext providerContext) {
        return new BasicSpreadsheetContext(
            Objects.requireNonNull(spreadsheetId, "spreadsheetId"),
            Objects.requireNonNull(storeRepository, "storeRepository"),
            Objects.requireNonNull(spreadsheetProvider, "spreadsheetProvider"),
            Objects.requireNonNull(environmentContext, "environmentContext"),
            Objects.requireNonNull(localeContext, "localeContext"),
            Objects.requireNonNull(providerContext, "providerContext")
        );
    }

    private BasicSpreadsheetContext(final SpreadsheetId spreadsheetId,
                                    final SpreadsheetStoreRepository storeRepository,
                                    final SpreadsheetProvider spreadsheetProvider,
                                    final EnvironmentContext environmentContext,
                                    final LocaleContext localeContext,
                                    final ProviderContext providerContext) {
        super();

        this.spreadsheetId = spreadsheetId;

        this.storeRepository = storeRepository;
        this.spreadsheetProvider = spreadsheetProvider;
        
        this.environmentContext = environmentContext;
        this.localeContext = LocaleContexts.readOnly(localeContext);
        this.providerContext = providerContext;

        this.metadata = this.loadMetadataOrFail(spreadsheetId);
    }

    @Override
    public SpreadsheetId spreadsheetId() {
        return this.spreadsheetId;
    }

    private final SpreadsheetId spreadsheetId;

    @Override
    public SpreadsheetStoreRepository storeRepository() {
        return this.storeRepository;
    }

    private final SpreadsheetStoreRepository storeRepository;

    // HasSpreadsheetMetadata...........................................................................................

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        return this.loadMetadataOrFail(this.spreadsheetId);
    }

    // SpreadsheetContext...............................................................................................

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        return this.storeRepository.metadatas()
            .load(id);
    }

    @Override
    public SpreadsheetMetadata saveMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");

        final SpreadsheetMetadata saved = this.storeRepository
            .metadatas()
            .save(metadata);
        // sync Locale
        final SpreadsheetId id = saved.id()
            .orElse(null);
        if(this.spreadsheetId().equals(id)) {
            this.setSpreadsheetMetadata(
                id,
                saved
            );
        }

        return saved;
    }

    @Override
    public void deleteMetadata(final SpreadsheetId id) {
        Objects.requireNonNull(id, "id");

        this.storeRepository
            .metadatas()
            .delete(id);

        this.setSpreadsheetMetadata(
            id,
            null
        );
    }

    private void setSpreadsheetMetadata(final SpreadsheetId id,
                                        final SpreadsheetMetadata metadata) {
        if(this.spreadsheetId().equals(id)) {
            this.metadata = metadata;

            if(null != metadata) {
                this.setLocale(metadata.locale());
            }
        }
    }

    private SpreadsheetMetadata metadata;

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
                this.spreadsheetId,
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
    public SpreadsheetContext setUser(final Optional<EmailAddress> user) {
        this.environmentContext.setUser(user);
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
        if(null == this.metadataSpreadsheetProvider) {
            this.setSpreadsheetProvider(this.metadata);

            if(null == this.metadataSpreadsheetProvider) {
                throw new IllegalStateException("SpreadsheetMetadata " + this.spreadsheetId + " deleted");
            }
        }
        return this.metadataSpreadsheetProvider;
    }

    private final SpreadsheetProvider spreadsheetProvider;

    private void setSpreadsheetProvider(final SpreadsheetMetadata metadata) {
        this.metadataSpreadsheetProvider = null != metadata ?
            metadata.spreadsheetProvider(this.spreadsheetProvider) :
            null;
    }

    private SpreadsheetProvider metadataSpreadsheetProvider;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return SpreadsheetMetadataPropertyName.SPREADSHEET_ID + "=" + this.spreadsheetId.toString();
    }
}
