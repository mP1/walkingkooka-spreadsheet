/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
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

package walkingkooka.spreadsheet.environment;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ReadOnlySpreadsheetEnvironmentContextTest implements SpreadsheetEnvironmentContextTesting2<ReadOnlySpreadsheetEnvironmentContext>,
    ToStringTesting<ReadOnlySpreadsheetEnvironmentContext> {

    private final static LineEnding LINE_ENDING = LineEnding.NL;
    private final static Locale LOCALE = Locale.GERMAN;
    private final static LocalDateTime NOW = LocalDateTime.MIN;
    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");
    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(123);
    private final static EmailAddress USER = EmailAddress.parse("user123@example.com");

    @Test
    public void testWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> ReadOnlySpreadsheetEnvironmentContext.with(null)
        );
    }

    @Test
    public void testWithUnwraps() {
        final ReadOnlySpreadsheetEnvironmentContext context = ReadOnlySpreadsheetEnvironmentContext.with(
            SpreadsheetEnvironmentContexts.fake()
        );

        assertSame(
            context,
            ReadOnlySpreadsheetEnvironmentContext.with(context)
        );
    }

    // cloneEnvironment.................................................................................................

    @Test
    public void testCloneEnvironment() {
        final ReadOnlySpreadsheetEnvironmentContext context = this.createContext();

        assertNotSame(
            context,
            context.cloneEnvironment()
        );
    }

    @Test
    public void testCloneEnvironmentNotReadOnly() {
        final ReadOnlySpreadsheetEnvironmentContext context = this.createContext();

        final SpreadsheetEnvironmentContext cloned = context.cloneEnvironment();
        assertNotSame(
            context,
            cloned
        );

        final EnvironmentValueName<String> name = EnvironmentValueName.with("hello");
        final String value = "World123";

        this.environmentValueAndCheck(
            cloned.setEnvironmentValue(
                name,
                value
            ),
            name,
            value
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetSpreadsheetEnvironmentContextWithSame() {
        final SpreadsheetEnvironmentContext empty = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.empty(
                LineEnding.NL,
                Locale.FRENCH,
                LocalDateTime::now,
                Optional.of(
                    EmailAddress.parse("user123@example.com")
                )
            )
        );
        final ReadOnlySpreadsheetEnvironmentContext readOnly = ReadOnlySpreadsheetEnvironmentContext.with(empty);

        assertSame(
            readOnly.setEnvironmentContext(empty),
            empty
        );
    }

    @Test
    public void testSetSpreadsheetEnvironmentContext() {
        final HasNow hasNow = () -> NOW;

        final SpreadsheetEnvironmentContext empty = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.empty(
                LineEnding.NL,
                Locale.FRENCH,
                hasNow,
                Optional.of(
                    EmailAddress.parse("user123@example.com")
                )
            )
        );
        final ReadOnlySpreadsheetEnvironmentContext readOnly = ReadOnlySpreadsheetEnvironmentContext.with(empty);

        final SpreadsheetEnvironmentContext different = SpreadsheetEnvironmentContexts.basic(
            EnvironmentContexts.empty(
                LineEnding.CRNL,
                Locale.GERMAN,
                hasNow,
                Optional.of(
                    EmailAddress.parse("user123@example.com")
                )
            )
        );

        this.checkNotEquals(
            empty,
            different
        );

        final SpreadsheetEnvironmentContext set = readOnly.setEnvironmentContext(different);

        assertSame(
            different,
            set
        );
    }

    // lineEnding.......................................................................................................

    @Test
    public void testLineEnding() {
        this.lineEndingAndCheck(
            this.createContext(),
            LINE_ENDING
        );
    }

    // setLineEnding....................................................................................................

    @Test
    public void testSetLineEndingFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .setLineEnding(LINE_ENDING)
        );
    }


    @Override
    public void testSetLineEndingWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    // locale...........................................................................................................

    @Test
    public void testLocale() {
        this.localeAndCheck(
            this.createContext(),
            LOCALE
        );
    }

    // setLocale........................................................................................................

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testSetLocaleFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .setLocale(LOCALE)
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    public void testSpreadsheetId() {
        this.spreadsheetIdAndCheck(
            this.createContext(),
            SPREADSHEET_ID
        );
    }

    // setSpreadsheetId.................................................................................................

    @Override
    public void testSetSpreadsheetIdWithSame() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testSetSpreadsheetIdFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .setSpreadsheetId(SPREADSHEET_ID)
        );
    }

    // user.............................................................................................................

    @Test
    public void testUser() {
        this.userAndCheck(
            this.createContext(),
            USER
        );
    }

    // setUser..........................................................................................................

    @Test
    public void testSetUserFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> this.createContext()
                .setUser(
                    Optional.of(USER)
                )
        );
    }

    @Override
    public void testSetUserWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    // environmentValue.................................................................................................

    @Test
    public void testEnvironmentalValue() {
        this.environmentValueAndCheck(
            this.createContext(),
            EnvironmentValueName.with("Hello123")
        );
    }

    @Test
    public void testEnvironmentValueWithLocale() {
        this.environmentValueAndCheck(
            this.createContext(),
            EnvironmentValueName.LOCALE,
            LOCALE
        );
    }

    @Override
    public ReadOnlySpreadsheetEnvironmentContext createContext() {
        return ReadOnlySpreadsheetEnvironmentContext.with(
            SpreadsheetEnvironmentContexts.basic(
                EnvironmentContexts.map(
                        EnvironmentContexts.empty(
                            LINE_ENDING,
                            Locale.FRANCE,
                            () -> NOW,
                            Optional.of(USER)
                        )
                    ).setLocale(LOCALE)
                    .setEnvironmentValue(
                        SpreadsheetEnvironmentContext.SERVER_URL,
                        SERVER_URL
                    )
            ).setSpreadsheetId(SPREADSHEET_ID)
        );
    }

    // environmentValueNames............................................................................................

    @Test
    public void testEnvironmentalValueNames() {
        this.environmentValueNamesAndCheck(
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.SERVER_URL,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SpreadsheetEnvironmentContext.USER
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{lineEnding=\"\\n\", locale=de, serverUrl=https://example.com, spreadsheetId=7b, user=user123@example.com}"
        );
    }

    // type naming......................................................................................................

    @Override
    public String typeNameSuffix() {
        return SpreadsheetEnvironmentContext.class.getSimpleName();
    }

    // class............................................................................................................

    @Override
    public Class<ReadOnlySpreadsheetEnvironmentContext> type() {
        return ReadOnlySpreadsheetEnvironmentContext.class;
    }
}
