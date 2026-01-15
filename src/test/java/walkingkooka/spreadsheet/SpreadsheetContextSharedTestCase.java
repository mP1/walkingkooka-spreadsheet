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
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
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
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.function.provider.ExpressionFunctionProviders;
import walkingkooka.validation.form.provider.FormHandlerProviders;
import walkingkooka.validation.provider.ValidatorProviders;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetContextSharedTestCase<C extends SpreadsheetContextShared> implements SpreadsheetContextTesting<C>,
    ToStringTesting<C> {

    final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    final Function<SpreadsheetContext, SpreadsheetEngineContext> SPREADSHEET_ENGINE_CONTEXT_FACTORY = (SpreadsheetContext c) -> {
        throw new UnsupportedOperationException();
    };

    final static LineEnding LINE_ENDING = LineEnding.NL;

    final static Locale LOCALE = Locale.forLanguageTag("en-AU");

    final static AuditInfo AUDIT_INFO = AuditInfo.create(
        EmailAddress.parse("creator@example.com"),
        LocalDateTime.MIN
    );

    final static HasNow HAS_NOW = () -> LocalDateTime.MIN;

    final static SpreadsheetEnvironmentContext SPREADSHEET_ENVIRONMENT_CONTEXT = SpreadsheetEnvironmentContexts.readOnly(
        SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LINE_ENDING,
                    LOCALE,
                    HAS_NOW,
                    Optional.empty() // no user
                )
            ).setEnvironmentValue(
                SpreadsheetEnvironmentContext.SERVER_URL,
                SERVER_URL
            ).setEnvironmentValue(
                SpreadsheetEnvironmentContext.SPREADSHEET_ID,
                SPREADSHEET_ID
            )
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
    public final void testWithNullSpreadsheetEngineContextFactoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext(
                null,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                PROVIDER_CONTEXT
            )
        );
    }

    @Test
    public final void testWithNullSpreadsheetEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext(
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
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
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
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
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
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
                SPREADSHEET_ENGINE_CONTEXT_FACTORY,
                SPREADSHEET_ENVIRONMENT_CONTEXT,
                LOCALE_CONTEXT,
                SPREADSHEET_PROVIDER,
                null
            )
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
        final SpreadsheetEnvironmentContext environmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LineEnding.NL,
                    Locale.ENGLISH,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            ).setEnvironmentValue(
                SpreadsheetEnvironmentContext.SPREADSHEET_ID,
                SPREADSHEET_ID
            )
        );

        final C context = this.createContext(environmentContext);
        assertSame(
            context,
            context.setEnvironmentContext(environmentContext)
        );
    }

    @Test
    public final void testSetEnvironmentContext() {
        final SpreadsheetEnvironmentContext environmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    LineEnding.NL,
                    Locale.ENGLISH,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            ).setEnvironmentValue(
                SpreadsheetEnvironmentContext.SPREADSHEET_ID,
                SPREADSHEET_ID
            )
        );

        final EnvironmentContext differentEnvironmentContext = environmentContext.cloneEnvironment();
        differentEnvironmentContext.setLocale(Locale.FRANCE);

        this.checkNotEquals(
            environmentContext,
            differentEnvironmentContext
        );

        final C basicSpreadsheetContext = this.createContext(environmentContext);
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
        this.localeAndCheck(
            context.setEnvironmentValue(
                EnvironmentValueName.LOCALE,
                locale
            ),
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

    abstract C createContext(final Function<SpreadsheetContext, SpreadsheetEngineContext> spreadsheetEngineContextFactory,
                             final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext,
                             final LocaleContext localeContext,
                             final SpreadsheetProvider spreadsheetProvider,
                             final ProviderContext providerContext);

    // toString.........................................................................................................

    @Test
    public final void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{lineEnding=\"\\n\", locale=en_AU, serverUrl=https://example.com, spreadsheetId=1}"
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
