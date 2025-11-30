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
import walkingkooka.environment.EnvironmentContextTesting2;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetName;
import walkingkooka.text.LineEnding;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataEnvironmentContextTest implements EnvironmentContextTesting2<SpreadsheetMetadataEnvironmentContext>,
    ToStringTesting<SpreadsheetMetadataEnvironmentContext> {

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58
    );

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

    private final static EnvironmentContext CONTEXT = EnvironmentContexts.empty(
        LineEnding.NL,
        Locale.FRENCH,
        () -> NOW,
        Optional.of(USER)
    );

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataEnvironmentContext.with(
                null,
                CONTEXT
            )
        );
    }

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataEnvironmentContext.with(
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
        final EnvironmentContext environmentContext = EnvironmentContexts.fake();

        final SpreadsheetMetadataEnvironmentContext wrap = SpreadsheetMetadataEnvironmentContext.with(
            spreadsheetMetadata,
            environmentContext
        );

        final SpreadsheetMetadataEnvironmentContext spreadsheetMetadataEnvironmentContext = SpreadsheetMetadataEnvironmentContext.with(
            spreadsheetMetadata,
            wrap
        );

        this.checkEquals(
            wrap,
            spreadsheetMetadataEnvironmentContext
        );
    }

    @Test
    public void testWithSpreadsheetMetadataEnvironmentContextDifferentSpreadsheetMetadata() {
        final SpreadsheetMetadata spreadsheetMetadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.FRENCH
        );
        final EnvironmentContext environmentContext = EnvironmentContexts.fake();

        final SpreadsheetMetadataEnvironmentContext wrap = SpreadsheetMetadataEnvironmentContext.with(
            spreadsheetMetadata,
            environmentContext
        );

        final SpreadsheetMetadata spreadsheetMetadata2 = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.GERMANY
        );

        this.checkEquals(
            SpreadsheetMetadataEnvironmentContext.with(
                spreadsheetMetadata2,
                environmentContext
            ),
            SpreadsheetMetadataEnvironmentContext.with(
                spreadsheetMetadata2,
                wrap
            )
        );
    }

    // environmentValue.................................................................................................

    @Test
    public void testEnvironmentValueWithPrefixButUnknown() {
        this.environmentValueAndCheck(
            EnvironmentValueName.with("missing")
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
            SpreadsheetMetadataTesting.METADATA_EN_AU.getOrFail(property)
        );
    }

    @Test
    public void testEnvironmentValueNames() {
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
                ).environmentContext(
                    EnvironmentContexts.map(CONTEXT)
                ),
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME.toEnvironmentValueName(),
            SpreadsheetMetadataPropertyName.LOCALE.toEnvironmentValueName(),
            SpreadsheetMetadataPropertyName.ROUNDING_MODE.toEnvironmentValueName()
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
    public SpreadsheetMetadataEnvironmentContext createContext() {
        return SpreadsheetMetadataEnvironmentContext.with(
            SpreadsheetMetadataTesting.METADATA_EN_AU,
            EnvironmentContexts.map(CONTEXT)
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            SpreadsheetMetadataTesting.METADATA_EN_AU.toString()
        );
    }

    // TypeName.........................................................................................................

    @Override
    public String typeNameSuffix() {
        return EnvironmentContext.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataEnvironmentContext> type() {
        return SpreadsheetMetadataEnvironmentContext.class;
    }
}
