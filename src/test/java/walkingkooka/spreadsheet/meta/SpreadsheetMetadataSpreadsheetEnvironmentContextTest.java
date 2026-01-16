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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.text.LineEnding;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataSpreadsheetEnvironmentContextTest implements SpreadsheetEnvironmentContextTesting2<SpreadsheetMetadataSpreadsheetEnvironmentContext>,
    ToStringTesting<SpreadsheetMetadataSpreadsheetEnvironmentContext> {

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58
    );

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

    static {
        final EnvironmentContext context = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                LineEnding.NL,
                Locale.FRENCH,
                () -> NOW,
                Optional.of(USER)
            )
        );
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );
        CONTEXT = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.readOnly(context)
        );
    }

    private final static SpreadsheetEnvironmentContext CONTEXT;

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadataTesting.METADATA_EN_AU.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                null,
                CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                SpreadsheetMetadata.EMPTY,
                null
            )
        );
    }

    @Test
    public void testWithUnwrapsSpreadsheetMetadataEnvironmentContext() {
        final SpreadsheetMetadata spreadsheetMetadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.FRENCH
        );
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.fake();

        final SpreadsheetMetadataSpreadsheetEnvironmentContext wrap = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            spreadsheetMetadata,
            spreadsheetEnvironmentContext
        );

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            spreadsheetMetadata,
            wrap
        );

        this.checkEquals(
            wrap,
            spreadsheetMetadataSpreadsheetEnvironmentContext
        );
    }

    @Test
    public void testWithSpreadsheetMetadataEnvironmentContextDifferentSpreadsheetMetadata() {
        final SpreadsheetMetadata spreadsheetMetadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.FRENCH
        );
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.fake();

        final SpreadsheetMetadataSpreadsheetEnvironmentContext wrap = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            spreadsheetMetadata,
            spreadsheetEnvironmentContext
        );

        final SpreadsheetMetadata spreadsheetMetadata2 = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.GERMANY
        );

        this.checkEquals(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                spreadsheetMetadata2,
                spreadsheetEnvironmentContext
            ),
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                spreadsheetMetadata2,
                wrap
            )
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSame() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.map(CONTEXT)
        );
        final SpreadsheetMetadata metadata = SpreadsheetMetadataTesting.METADATA_EN_AU;

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            metadata,
            spreadsheetEnvironmentContext
        );

        final SpreadsheetEnvironmentContext afterSet = spreadsheetMetadataSpreadsheetEnvironmentContext.setEnvironmentContext(spreadsheetEnvironmentContext);
        assertSame(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            afterSet
        );
    }

    @Test
    public void testSetEnvironmentContextWithDifferent() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(CONTEXT);
        final SpreadsheetMetadata metadata = METADATA;

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            metadata,
            SpreadsheetEnvironmentContexts.basic(environmentContext)
        );

        final EnvironmentContext differentEnvironmentContext = EnvironmentContexts.map(
            CONTEXT.cloneEnvironment()
        );
        differentEnvironmentContext.setLocale(Locale.GERMAN);

        this.checkNotEquals(
            environmentContext,
            differentEnvironmentContext
        );

        final EnvironmentContext afterSet = spreadsheetMetadataSpreadsheetEnvironmentContext.setEnvironmentContext(differentEnvironmentContext);
        assertNotSame(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            afterSet
        );

        this.checkEquals(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                metadata,
                SpreadsheetEnvironmentContexts.basic(differentEnvironmentContext)
            ),
            afterSet
        );
    }

    // environmentValueNames............................................................................................

    @Test
    public void testEnvironmentValueNamesWithEmptySpreadsheetMetadata() {
        this.environmentValueNamesAndCheck(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                SpreadsheetMetadata.EMPTY,
                SpreadsheetEnvironmentContexts.basic(
                    EnvironmentContexts.empty(
                        LineEnding.NL,
                        Locale.FRENCH,
                        () -> NOW,
                        Optional.of(USER)
                    )
                )
            ),
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetEnvironmentContext.USER
        );
    }

    @Test
    public void testEnvironmentValueNames2() {
        this.environmentValueNamesAndCheck(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    SpreadsheetId.with(1)
                ),
                SpreadsheetEnvironmentContexts.basic(
                    EnvironmentContexts.empty(
                        LineEnding.NL,
                        Locale.FRENCH,
                        () -> NOW,
                        Optional.of(USER)
                    )
                )
            ),
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SpreadsheetEnvironmentContext.USER
        );
    }

    @Test
    public void testEnvironmentValueNames3() {
        final Locale locale = Locale.GERMAN;

        this.environmentValueNamesAndCheck(
            SpreadsheetMetadata.EMPTY
                .set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    SpreadsheetId.with(1)
                ).set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                    SpreadsheetName.with("Hello-spreadsheet-123")
                ).set(
                    SpreadsheetMetadataPropertyName.LOCALE,
                    locale
                ).setDefaults(
                    SpreadsheetMetadata.EMPTY
                        .set(
                            SpreadsheetMetadataPropertyName.LOCALE,
                            locale
                        ).set(
                            SpreadsheetMetadataPropertyName.ROUNDING_MODE,
                            RoundingMode.FLOOR
                        )
                ).spreadsheetEnvironmentContext(
                    SpreadsheetEnvironmentContexts.basic(
                        EnvironmentContexts.map(CONTEXT)
                    )
                ),
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetEnvironmentContext.USER,
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME.toEnvironmentValueName(),
            SpreadsheetMetadataPropertyName.LOCALE.toEnvironmentValueName(),
            SpreadsheetMetadataPropertyName.ROUNDING_MODE.toEnvironmentValueName()
        );
    }

    // environmentValue.................................................................................................

    @Test
    public void testEnvironmentValueWithPrefixButUnknown() {
        this.environmentValueAndCheck(
            EnvironmentValueName.with(
                "missing",
                Void.class
            )
        );
    }

    @Test
    public void testEnvironmentValueWrappedContextFirst() {
        final EnvironmentValueName<Locale> name = EnvironmentValueName.LOCALE;

        this.environmentValueAndCheck(
            name,
            CONTEXT.locale()
        );
    }

    @Test
    public void testEnvironmentValueDefaultsToSpreadsheetMetadata() {
        final SpreadsheetMetadataPropertyName<Integer> property = SpreadsheetMetadataPropertyName.PRECISION;

        this.environmentValueAndCheck(
            property.toEnvironmentValueName(),
            METADATA.getOrFail(property)
        );
    }

    @Test
    public void testLocale() {
        final Locale metadataLocale = Locale.ENGLISH;

        final SpreadsheetMetadataSpreadsheetEnvironmentContext context = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                metadataLocale
            ),
            CONTEXT.cloneEnvironment()
        );

        this.checkNotEquals(
            metadataLocale,
            CONTEXT.locale(),
            "locale"
        );

        this.localeAndCheck(
            context,
            CONTEXT.locale()
        );
    }


    @Test
    public void testSetLocaleUpdatesContext() {
        final Locale metadataLocale = Locale.ENGLISH;

        final SpreadsheetMetadataSpreadsheetEnvironmentContext context = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                metadataLocale
            ),
            CONTEXT.cloneEnvironment()
        );

        this.checkNotEquals(
            metadataLocale,
            CONTEXT.locale(),
            "locale"
        );

        this.localeAndCheck(
            context,
            CONTEXT.locale()
        );

        this.setLocaleAndCheck(
            context,
            Locale.GERMANY
        );
    }

    @Test
    public void testServerUrl() {
        this.serverUrlAndCheck(
            this.createContext(),
            SERVER_URL
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    public void testSpreadsheetIdMissingFromWrappedEnvironmentContext() {
        final SpreadsheetId metadataSpreadsheetId = SpreadsheetId.with(2);

        this.checkNotEquals(
            SPREADSHEET_ID,
            metadataSpreadsheetId
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = CONTEXT.cloneEnvironment();
        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName()
        );

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                metadataSpreadsheetId
            ),
            spreadsheetEnvironmentContext
        );

        this.spreadsheetIdAndCheck(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            metadataSpreadsheetId
        );
    }

    @Test
    public void testSpreadsheetIdInEnvironmentContext() {
        final SpreadsheetId metadataSpreadsheetId = SpreadsheetId.with(2);

        this.checkNotEquals(
            SPREADSHEET_ID,
            metadataSpreadsheetId
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setEnvironmentValue(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
                SPREADSHEET_ID
            );

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            SPREADSHEET_ID
        );
    }

    @Test
    public void testSetSpreadsheetIdUpdatesContext() {
        final SpreadsheetId metadataSpreadsheetId = SpreadsheetId.with(2);
        final SpreadsheetId environmentSpreadsheetId = SpreadsheetId.with(3);

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            environmentSpreadsheetId
        );

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                metadataSpreadsheetId
            ),
            spreadsheetEnvironmentContext
        );

        this.checkNotEquals(
            metadataSpreadsheetId,
            CONTEXT.environmentValue(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName()
            ).orElse(null),
            "spreadsheetId"
        );

        this.spreadsheetIdAndCheck(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            environmentSpreadsheetId
        );

        final SpreadsheetId differentSpreadsheetId = SpreadsheetId.with(4);

        this.setSpreadsheetIdAndCheck(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            differentSpreadsheetId
        );

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            differentSpreadsheetId
        );
    }

    @Test
    public void testUser() {
        this.userAndCheck(USER);
    }

    @Test
    public void testNow() {
        this.checkEquals(
            this.createContext().now(),
            NOW
        );
    }

    @Override
    public SpreadsheetMetadataSpreadsheetEnvironmentContext createContext() {
        return SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA,
            SpreadsheetEnvironmentContexts.basic(
                EnvironmentContexts.map(
                    CONTEXT.cloneEnvironment()
                )
            )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    SpreadsheetId.with(1)
                ),
                SpreadsheetEnvironmentContexts.basic(
                    EnvironmentContexts.map(CONTEXT)
                )
            ),
            "{lineEnding=\\n, locale=fr, now=1999-12-31T12:58, spreadsheetId=1, user=user@example.com}"
        );
    }

    // TypeName.........................................................................................................

    @Override
    public String typeNameSuffix() {
        return EnvironmentContext.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataSpreadsheetEnvironmentContext> type() {
        return SpreadsheetMetadataSpreadsheetEnvironmentContext.class;
    }
}
