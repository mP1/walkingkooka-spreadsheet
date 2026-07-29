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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.Storages;

import java.time.ZoneOffset;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEnvironmentContextReadOnlyTest implements SpreadsheetEnvironmentContextTesting2<SpreadsheetEnvironmentContextReadOnly>,
    ToStringTesting<SpreadsheetEnvironmentContextReadOnly> {

    // ONLY user is writable
    private final Predicate<EnvironmentValueName<?>> READ_ONLY_FILTER = Predicates.not(
        Predicates.is(
            EnvironmentContext.USER
        )
    );

    @Test
    public void testWithNullReadOnlyFilterFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextReadOnly.with(
                null,
                SpreadsheetEnvironmentContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextReadOnly.with(
                READ_ONLY_FILTER,
                null
            )
        );
    }

    @Test
    public void testWithUnwrapsSpreadsheetEnvironmentContextReadOnlyWithSameReadOnlyFilter() {
        final SpreadsheetEnvironmentContextReadOnly context = SpreadsheetEnvironmentContextReadOnly.with(
            READ_ONLY_FILTER,
            SpreadsheetEnvironmentContexts.fake()
        );

        assertSame(
            context,
            SpreadsheetEnvironmentContextReadOnly.with(
                READ_ONLY_FILTER,
                context
            )
        );
    }

    @Test
    public void testWithUnwrapsSpreadsheetEnvironmentContextReadOnlyWithDifferentReadOnlyFilter() {
        final SpreadsheetEnvironmentContextReadOnly context = SpreadsheetEnvironmentContextReadOnly.with(
            Predicates.fake(),
            SpreadsheetEnvironmentContexts.fake()
        );

        assertNotSame(
            context,
            SpreadsheetEnvironmentContextReadOnly.with(
                READ_ONLY_FILTER,
                context
            )
        );
    }

    // cloneEnvironment.................................................................................................

    @Test
    public void testCloneEnvironment() {
        final SpreadsheetEnvironmentContextReadOnly context = this.createContext();

        assertNotSame(
            context,
            context.cloneEnvironment()
        );
    }

    @Test
    public void testCloneEnvironmentNotReadOnly() {
        final SpreadsheetEnvironmentContextReadOnly context = this.createContext();

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
        final SpreadsheetEnvironmentContext notReadOnly = SpreadsheetEnvironmentContexts.basic(
            Storages.fake(),
            STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
        final SpreadsheetEnvironmentContextReadOnly readOnly = SpreadsheetEnvironmentContextReadOnly.with(
            READ_ONLY_FILTER,
            notReadOnly
        );

        assertSame(
            readOnly.setEnvironmentContext(notReadOnly),
            readOnly
        );
    }

    @Test
    public void testSetSpreadsheetEnvironmentContext() {
        final Storage<SpreadsheetStorageContext> storage = Storages.fake();

        final SpreadsheetEnvironmentContext notReadOnly = SpreadsheetEnvironmentContexts.basic(
            storage,
            STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
        );
        final SpreadsheetEnvironmentContextReadOnly readOnly = SpreadsheetEnvironmentContextReadOnly.with(
            READ_ONLY_FILTER,
            notReadOnly
        );

        final SpreadsheetEnvironmentContext different = SpreadsheetEnvironmentContexts.basic(
            storage,
            DIFFERENT_STORAGE_ENVIRONMENT_CONTEXT
        );

        this.checkNotEquals(
            notReadOnly,
            different
        );

        final SpreadsheetEnvironmentContext set = readOnly.setEnvironmentContext(different);

        assertSame(
            different,
            set
        );
    }

    // currency.........................................................................................................

    @Test
    public void testCurrency() {
        this.currencyAndCheck(
            this.createContext(),
            CURRENCY
        );
    }

    // setCurrency......................................................................................................

    @Test
    public void testSetCurrencyFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setCurrency(CURRENCY)
        );
    }

    @Override
    public void testSetCurrencyWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
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
                .setCurrentWorkingDirectory(OPTIONAL_CURRENT_WORKING_DIRECTORY)
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
                .setSpreadsheetId(OPTIONAL_SPREADSHEET_ID)
        );
    }

    // timeOffset.......................................................................................................

    @Test
    public void testTimeOffset() {
        this.timeOffsetAndCheck(
            this.createContext(),
            ZoneOffset.UTC
        );
    }

    // setTimeOffset....................................................................................................

    @Test
    public void testSetTimeOffsetFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setTimeOffset(
                    ZoneOffset.UTC
                )
        );
    }

    @Override
    public void testSetTimeOffsetWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
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
    public void testSetUserNotReadOnly() {
        this.setUserAndCheck(
            this.createContext(),
            DIFFERENT_USER
        );
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
    public SpreadsheetEnvironmentContextReadOnly createContext() {
        final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        storageEnvironmentContext.setLocale(LOCALE);
        storageEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            storageEnvironmentContext
        );
        spreadsheetEnvironmentContext.setSpreadsheetId(OPTIONAL_SPREADSHEET_ID);

        return SpreadsheetEnvironmentContextReadOnly.with(
            READ_ONLY_FILTER,
            spreadsheetEnvironmentContext
        );
    }

    // environmentValueNames............................................................................................

    @Test
    public void testEnvironmentalValueNames() {
        this.environmentValueNamesAndCheck(
            SpreadsheetEnvironmentContext.CHARSET,
            SpreadsheetEnvironmentContext.CURRENCY,
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            SpreadsheetEnvironmentContext.HOME_DIRECTORY,
            SpreadsheetEnvironmentContext.INDENTATION,
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetEnvironmentContext.SERVER_URL,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SpreadsheetEnvironmentContext.TIME_OFFSET,
            SpreadsheetEnvironmentContext.USER
        );
    }

    // storage..........................................................................................................

    @Test
    public void testStorage() {
        this.storageAndCheck(
            SpreadsheetEnvironmentContextReadOnly.with(
                READ_ONLY_FILTER,
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
            "{charset=UTF-8, currency=AUD, currentWorkingDirectory=/current1/working2/directory3, homeDirectory=/home/user/, indentation=\"  \", lineEnding=\"\\n\", locale=en_AU, serverUrl=https://example.com, spreadsheetId=123, timeOffset=Z, user=user123@example.com}"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createContext(),
            "SpreadsheetEnvironmentContextReadOnly\n" +
                "  SpreadsheetEnvironmentContextBasic\n" +
                "    environment\n" +
                "      StorageEnvironmentContextBasic\n" +
                "        EnvironmentContextSharedMap\n" +
                "          charset\n" +
                "            UTF-8 (sun.nio.cs.UTF_8)\n" +
                "          currency\n" +
                "            AUD (java.util.Currency)\n" +
                "          currentWorkingDirectory\n" +
                "            /current1/working2/directory3\n" +
                "          homeDirectory\n" +
                "            /home/user/\n" +
                "          indentation\n" +
                "            \"  \" (walkingkooka.text.Indentation)\n" +
                "          lineEnding\n" +
                "            \"\\n\"\n" +
                "          locale\n" +
                "            en_AU (java.util.Locale)\n" +
                "          now\n" +
                "            1999-12-31T12:58:59 (java.time.LocalDateTime)\n" +
                "          serverUrl\n" +
                "            https://example.com (walkingkooka.net.AbsoluteUrl)\n" +
                "          spreadsheetId\n" +
                "            123\n" +
                "          timeOffset\n" +
                "            Z (java.time.ZoneOffset)\n" +
                "          user\n" +
                "            user123@example.com (walkingkooka.net.email.EmailAddress)\n" +
                "    storage\n" +
                "       (walkingkooka.storage.StorageSharedEmpty)\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEnvironmentContextReadOnly> type() {
        return SpreadsheetEnvironmentContextReadOnly.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
