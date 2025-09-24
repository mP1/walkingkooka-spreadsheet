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

import org.junit.jupiter.api.Test;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStore;
import walkingkooka.spreadsheet.meta.store.SpreadsheetMetadataStores;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.store.repo.FakeSpreadsheetStoreRepository;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepositories;
import walkingkooka.spreadsheet.store.repo.SpreadsheetStoreRepository;

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

    private final static BiFunction<EmailAddress, Optional<Locale>, SpreadsheetMetadata> CREATE_METADATA =
        (e, dl) ->
            SpreadsheetMetadata.EMPTY.set(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                AuditInfo.with(
                    e,
                    NOW,
                    e,
                    NOW
                )
            ).set(
                SpreadsheetMetadataPropertyName.LOCALE,
                dl.get()
            );
    private final static SpreadsheetStoreRepository REPO = SpreadsheetStoreRepositories.fake();

    private final static SpreadsheetProvider SPREADSHEET_PROVIDER = SpreadsheetProviders.fake();

    private final static EnvironmentContext ENVIRONMENT_CONTEXT = EnvironmentContexts.map(
        EnvironmentContexts.empty(
            Locale.forLanguageTag("en-AU"),
            LocalDateTime::now,
            Optional.empty() // no user
        )
    );
    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(Locale.ENGLISH);

    private final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    @Test
    public void testWithNullCreateMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                null,
                REPO,
                SPREADSHEET_PROVIDER,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullStoreRepositoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                CREATE_METADATA,
                null,
                SPREADSHEET_PROVIDER,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                CREATE_METADATA,
                REPO,
                null,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                CREATE_METADATA,
                REPO,
                SPREADSHEET_PROVIDER,
                null,
                LOCALE_CONTEXT,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                CREATE_METADATA,
                REPO,
                SPREADSHEET_PROVIDER,
                ENVIRONMENT_CONTEXT,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullProviderontextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetContext.with(
                CREATE_METADATA,
                REPO,
                SPREADSHEET_PROVIDER,
                ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                null
            )
        );
    }

    // createMetadata...................................................................................................

    @Test
    public void testCreateMetadata() {
        final BasicSpreadsheetContext context = this.createContext();

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

        this.loadMetadataAndCheck(
            context,
            metadata.getOrFail(SpreadsheetMetadataPropertyName.SPREADSHEET_ID),
            metadata
        );
    }

    @Override
    public BasicSpreadsheetContext createContext() {
        return BasicSpreadsheetContext.with(
            CREATE_METADATA,
            new FakeSpreadsheetStoreRepository() {

                @Override
                public SpreadsheetMetadataStore metadatas() {
                    return this.store;
                }

                private final SpreadsheetMetadataStore store = SpreadsheetMetadataStores.treeMap();
            },
            SPREADSHEET_PROVIDER,
            ENVIRONMENT_CONTEXT,
            LOCALE_CONTEXT,
            PROVIDER_CONTEXT
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetContext> type() {
        return BasicSpreadsheetContext.class;
    }
}
