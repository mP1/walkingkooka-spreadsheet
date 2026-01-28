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

package walkingkooka.spreadsheet.environment;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.datetime.HasNow;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.FakeStorage;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.Storages;
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

    private final static StoragePath CURRENT_WORKING_DIRECTORY = StoragePath.parse("/current1/working2/directory3");

    private final static Storage<SpreadsheetStorageContext> STORAGE = new FakeStorage<>() {
        @Override
        public String toString() {
            return FakeStorage.class.getSimpleName();
        }
    };

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

        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "hello",
            String.class
        );
        final String value = "World123";

        this.setEnvironmentValueAndCheck(
            cloned,
            name,
            value
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetSpreadsheetEnvironmentContextWithSame() {
        final SpreadsheetEnvironmentContext empty = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            EnvironmentContexts.empty(
                INDENTATION,
                LineEnding.NL,
                Locale.FRENCH,
                () -> LocalDateTime.MIN,
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

        final Storage<SpreadsheetStorageContext> storage = Storages.fake();

        final SpreadsheetEnvironmentContext empty = SpreadsheetEnvironmentContexts.basic(
            storage,
            EnvironmentContexts.empty(
                INDENTATION,
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
            storage,
            EnvironmentContexts.empty(
                INDENTATION,
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

    // currentWorkingDirectory..........................................................................................

    @Test
    public void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    // setCurrentWorkingDirectory.......................................................................................

    @Test
    public void testSetCurrentWorkingDirectoryFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setCurrentWorkingDirectory(
                    Optional.of(CURRENT_WORKING_DIRECTORY)
                )
        );
    }

    // indentation......................................................................................................

    @Test
    public void testIndentation() {
        this.indentationAndCheck(
            this.createContext(),
            INDENTATION
        );
    }

    // setIndentation...................................................................................................

    @Test
    public void testSetIndentationFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setIndentation(INDENTATION)
        );
    }

    @Override
    public void testSetIndentationWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
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
            ReadOnlyEnvironmentValueException.class,
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
            ReadOnlyEnvironmentValueException.class,
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

    @Test
    public void testSetSpreadsheetIdFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setSpreadsheetId(
                    Optional.of(SPREADSHEET_ID)
                )
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
            ReadOnlyEnvironmentValueException.class,
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
            EnvironmentValueName.with(
                "Hello123",
                String.class
            )
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
    public void testRemoveEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetEnvironmentValueWithNowFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReadOnlySpreadsheetEnvironmentContext createContext() {
        final EnvironmentContext context = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LINE_ENDING,
                Locale.FRANCE,
                () -> NOW,
                Optional.of(USER)
            )
        );

        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            CURRENT_WORKING_DIRECTORY
        );
        context.setLocale(LOCALE);
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            context
        );
        spreadsheetEnvironmentContext.setSpreadsheetId(
            Optional.of(SPREADSHEET_ID)
        );

        return ReadOnlySpreadsheetEnvironmentContext.with(spreadsheetEnvironmentContext);
    }

    // environmentValueNames............................................................................................

    @Test
    public void testEnvironmentalValueNames() {
        this.environmentValueNamesAndCheck(
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            SpreadsheetEnvironmentContext.INDENTATION,
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetEnvironmentContext.SERVER_URL,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SpreadsheetEnvironmentContext.USER
        );
    }

    // storage..........................................................................................................

    @Test
    public void testStorage() {
        this.storageAndCheck(
            ReadOnlySpreadsheetEnvironmentContext.with(
                new FakeSpreadsheetEnvironmentContext() {
                    @Override
                    public Storage<SpreadsheetStorageContext> storage() {
                        return STORAGE;
                    }
                }
            ),
            STORAGE
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{currentWorkingDirectory=/current1/working2/directory3, indentation=\"  \", lineEnding=\"\\n\", locale=de, serverUrl=https://example.com, spreadsheetId=7b, user=user123@example.com}"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createContext(),
            "ReadOnlySpreadsheetEnvironmentContext\n" +
                "  BasicSpreadsheetEnvironmentContext\n" +
                "    environment\n" +
                "      EnvironmentContextSharedMap\n" +
                "        currentWorkingDirectory\n" +
                "          /current1/working2/directory3\n" +
                "        indentation\n" +
                "          \"  \" (walkingkooka.text.Indentation)\n" +
                "        lineEnding\n" +
                "          \"\\n\"\n" +
                "        locale\n" +
                "          de (java.util.Locale)\n" +
                "        now\n" +
                "          -999999999-01-01T00:00 (java.time.LocalDateTime)\n" +
                "        serverUrl\n" +
                "          https://example.com (walkingkooka.net.AbsoluteUrl)\n" +
                "        spreadsheetId\n" +
                "          7b\n" +
                "        user\n" +
                "          user123@example.com (walkingkooka.net.email.EmailAddress)\n" +
                "    storage\n" +
                "      FakeStorage (walkingkooka.spreadsheet.environment.ReadOnlySpreadsheetEnvironmentContextTest$1)\n"
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
