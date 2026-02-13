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
import walkingkooka.ToStringTesting;
import walkingkooka.convert.provider.ConverterProviders;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.AuditInfo;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.spreadsheet.compare.provider.SpreadsheetComparatorProviders;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;
import walkingkooka.spreadsheet.engine.SpreadsheetEngines;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.export.provider.SpreadsheetExporterProviders;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionFunctions;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterProviders;
import walkingkooka.spreadsheet.importer.provider.SpreadsheetImporterProviders;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserProviders;
import walkingkooka.spreadsheet.provider.SpreadsheetProvider;
import walkingkooka.spreadsheet.provider.SpreadsheetProviders;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.Storages;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetContextSharedTestCase<C extends SpreadsheetContextShared> implements SpreadsheetContextTesting<C>,
    ToStringTesting<C> {

    final static SpreadsheetEngine SPREADSHEET_ENGINE = SpreadsheetEngines.fake();

    final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    final static Currency CURRENCY = Currency.getInstance("AUD");

    final static StoragePath CURRENT_WORKING_DIRECTORY = StoragePath.parse("/current1/working2/directory3");

    final static Indentation INDENTATION = Indentation.SPACES4;

    final static LineEnding LINE_ENDING = LineEnding.NL;

    final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    final static AuditInfo AUDIT_INFO = AuditInfo.create(
        EmailAddress.parse("creator@example.com"),
        LocalDateTime.MIN
    );

    final static HasNow HAS_NOW = () -> LocalDateTime.MIN;

    private static EnvironmentContext spreadsheetEnvironmentContextEnvironmentContext() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                CURRENCY,
                INDENTATION,
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                Optional.empty() // no user
            )
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            CURRENT_WORKING_DIRECTORY
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID
        );

        return environmentContext;
    }

    final static Storage<SpreadsheetStorageContext> STORAGE = Storages.fake();

    final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT = SpreadsheetEnvironmentContexts.readOnly(
        SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            spreadsheetEnvironmentContextEnvironmentContext()
        )
    );

    final static LocaleContext LOCALE_CONTEXT = LocaleContexts.readOnly(
        LocaleContexts.jre(Locale.ENGLISH)
    );

    final static SpreadsheetProvider SPREADSHEET_PROVIDER = SpreadsheetProviders.basic(
        ConverterProviders.empty(),
        ExpressionFunctionProviders.empty(
            SpreadsheetExpressionFunctions.NAME_CASE_SENSITIVITY
        ),
        SpreadsheetComparatorProviders.empty(),
        SpreadsheetExporterProviders.empty(),
        SpreadsheetFormatterProviders.empty(),
        FormHandlerProviders.empty(),
        SpreadsheetImporterProviders.empty(),
        SpreadsheetParserProviders.empty(),
        ValidatorProviders.empty()
    );

    final static ProviderContext PROVIDER_CONTEXT = ProviderContexts.fake();

    // with.............................................................................................................

    @Test
    public final void testWithNullSpreadsheetEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext(
                null,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public final void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext(
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                null,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public final void testWithNullSpreadsheetProviderFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext(
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                null,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public final void testWithNullProviderContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext(
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                null
            )
        );
    }

    // spreadsheetEngine................................................................................................

    @Test
    public final void testSpreadsheetEngine() {
        this.spreadsheetEngineAndCheck(
            this.createContext(),
            SPREADSHEET_ENGINE
        );
    }


    // cloneEnvironmentContext..........................................................................................

    @Test
    public final void testCloneEnvironmentDifferentInstance() {
        final C context = this.createContext();
        assertNotSame(
            context,
            context.cloneEnvironment()
        );
    }

    // setEnvironmentContext............................................................................................

    @Override
    public final void testSetEnvironmentContextWithEqualEnvironmentContext() {
        throw new UnsupportedOperationException();
    }

    @Test
    public final void testSetEnvironmentContextWithThis() {
        final C context = this.createContext();
        assertSame(
            context,
            context.setEnvironmentContext(context)
        );
    }

    @Test
    public final void testSetEnvironmentContextWithSame() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                CURRENCY,
                INDENTATION,
                LineEnding.NL,
                Locale.ENGLISH,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID
        );
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            environmentContext
        );

        final C context = this.createContext(spreadsheetEnvironmentContext);
        assertSame(
            context,
            context.setEnvironmentContext(spreadsheetEnvironmentContext)
        );
    }

    @Test
    public final void testSetEnvironmentContext() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                CURRENCY,
                INDENTATION,
                LineEnding.NL,
                Locale.ENGLISH,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID
        );
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            environmentContext
        );

        final EnvironmentContext differentEnvironmentContext = spreadsheetEnvironmentContext.cloneEnvironment();
        differentEnvironmentContext.setLocale(Locale.FRANCE);

        this.checkNotEquals(
            spreadsheetEnvironmentContext,
            differentEnvironmentContext
        );

        final C basicSpreadsheetContext = this.createContext(spreadsheetEnvironmentContext);
        final SpreadsheetContext afterSet = basicSpreadsheetContext.setEnvironmentContext(differentEnvironmentContext);

        assertNotSame(
            basicSpreadsheetContext,
            afterSet
        );

        this.checkNotEquals(
            basicSpreadsheetContext,
            afterSet
        );
    }

    // setEnvironment...................................................................................................

    @Test
    public final void testSetEnvironmentLocaleDifferent() {
        final C context = this.createContext();

        final Locale locale = Locale.forLanguageTag("FR");

        context.setEnvironmentValue(
            EnvironmentValueName.LOCALE,
            locale
        );

        this.localeAndCheck(
            context,
            locale
        );

        this.environmentValueAndCheck(
            context,
            EnvironmentValueName.LOCALE,
            locale
        );
    }

    // setLocale........................................................................................................

    @Test
    public final void testSetLocaleDifferent() {
        final C context = this.createContext();

        final Locale locale = Locale.forLanguageTag("FR");
        context.setLocale(locale);

        this.localeAndCheck(
            context,
            locale
        );
    }

    // currentWorkingDirectory..........................................................................................

    @Test
    public final void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    // setCurrentWorkingDirectory.......................................................................................

    @Test
    public final void testSetCurrentWorkingDirectory() {
        final StoragePath different = StoragePath.parse("/different");
        this.checkNotEquals(
            CURRENT_WORKING_DIRECTORY,
            different
        );

        this.setCurrentWorkingDirectoryAndCheck(
            this.createContext(),
            different
        );
    }

    // indentation........................................................................................................

    @Test
    public final void testIndentation() {
        this.indentationAndCheck(
            this.createContext(),
            INDENTATION
        );
    }
    
    // serverUrl........................................................................................................

    @Test
    public final void testServerUrl() {
        this.serverUrlAndCheck(
            this.createContext(),
            SERVER_URL
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    public final void testSpreadsheetId() {
        this.spreadsheetIdAndCheck(
            this.createContext(),
            SPREADSHEET_ID
        );
    }

    // storage..........................................................................................................

    @Test
    public final void testStorage() {
        this.storageAndCheck(
            this.createContext(),
            STORAGE
        );
    }

    // spreadsheetProvider..............................................................................................

    @Test
    public final void testSpreadsheetProvider() {
        final C context = this.createContext();

        final SpreadsheetProvider spreadsheetProvider = context.spreadsheetProvider();
        assertSame(
            spreadsheetProvider,
            context.spreadsheetProvider()
        );
    }

    // createContext....................................................................................................

    abstract C createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext);

    abstract C createContext(final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                             final LocaleContext localeContext,
                             final SpreadsheetProvider spreadsheetProvider,
                             final ProviderContext providerContext);

    // toString.........................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{currency=\"AUD\", currentWorkingDirectory=/current1/working2/directory3, indentation=\"    \", lineEnding=\"\\n\", locale=en_AU, serverUrl=https://example.com, spreadsheetId=1, timeOffset=Z}"
        );
    }

    // class............................................................................................................

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetContext.class.getSimpleName();
    }

    @Override
    public final String typeNameSuffix() {
        return "";
    }
}
