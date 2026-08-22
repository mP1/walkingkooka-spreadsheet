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
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.FakeStorage;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.StorageEnvironmentContexts;
import walkingkooka.storage.StoragePath;
import walkingkooka.storage.Storages;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEnvironmentContextBasicTest implements SpreadsheetEnvironmentContextTesting2<SpreadsheetEnvironmentContextBasic>,
    SpreadsheetMetadataTesting,
    TreePrintableTesting,
    ToStringTesting<SpreadsheetEnvironmentContextBasic> {

    private final static Storage<SpreadsheetStorageContext> STORAGE = new FakeStorage<>() {

        @Override
        public String toString() {
            return FakeStorage.class.getSimpleName();
        }
    };

    // with.............................................................................................................

    @Test
    public void testWithNullStorageFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextBasic.with(
                null,
                SpreadsheetEnvironmentContexts.fake()
            )
        );
    }

    @Test
    public void testWithNullStorageEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                null
            )
        );
    }

    @Test
    public void testWithSpreadsheetEnvironmentContextAndSameStorage() {
        final SpreadsheetEnvironmentContext wrap = new FakeSpreadsheetEnvironmentContext() {
            @Override
            public Storage<SpreadsheetStorageContext> storage() {
                return STORAGE;
            }
        };

        assertSame(
            wrap,
            SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                wrap
            )
        );
    }

    @Test
    public void testWithSpreadsheetEnvironmentContextAndDifferentStorage() {
        final SpreadsheetEnvironmentContext wrap = new FakeSpreadsheetEnvironmentContext() {
            @Override
            public Storage<SpreadsheetStorageContext> storage() {
                return Storages.fake();
            }
        };

        final SpreadsheetEnvironmentContextBasic basicSpreadsheetEnvironmentContext = (SpreadsheetEnvironmentContextBasic) SpreadsheetEnvironmentContextBasic.with(
            STORAGE,
            wrap
        );

        this.storageAndCheck(
            basicSpreadsheetEnvironmentContext,
            STORAGE
        );

        this.environmentContextAndCheck(
            basicSpreadsheetEnvironmentContext,
            wrap
        );
    }

    @Test
    public void testWithEnvironmentContext() {
        final StorageEnvironmentContext storageEnvironmentContext = StorageEnvironmentContexts.fake();

        final SpreadsheetEnvironmentContextBasic basicSpreadsheetEnvironmentContext = (SpreadsheetEnvironmentContextBasic) SpreadsheetEnvironmentContextBasic.with(
            STORAGE,
            storageEnvironmentContext
        );

        this.storageAndCheck(
            basicSpreadsheetEnvironmentContext,
            STORAGE
        );

        this.environmentContextAndCheck(
            basicSpreadsheetEnvironmentContext,
            storageEnvironmentContext
        );
    }

    private void environmentContextAndCheck(final SpreadsheetEnvironmentContextBasic basicSpreadsheetEnvironmentContext,
                                            final EnvironmentContext environmentContext) {
        assertSame(
            environmentContext,
            basicSpreadsheetEnvironmentContext.environmentContext(),
            "environmentContext"
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSelf() {
        final SpreadsheetEnvironmentContext context = this.createContext();

        assertSame(
            context,
            context.setEnvironmentContext(context)
        );
    }

    @Test
    public void testSetEnvironmentContextWithSpreadsheetEnvironmentContext() {
        final SpreadsheetEnvironmentContext context = this.createContext();

        final SpreadsheetEnvironmentContext different = new FakeSpreadsheetEnvironmentContext() {
            @Override
            public Storage<SpreadsheetStorageContext> storage() {
                return STORAGE;
            }
        };
        assertSame(
            different,
            context.setEnvironmentContext(different)
        );
    }

    // currentWorkingDirectory..........................................................................................

    @Test
    public void testCurrentWorkingDirectoryMissing() {
        final EnvironmentContext environmentContext = ENVIRONMENT_CONTEXT.cloneEnvironment();

        this.currentWorkingDirectoryAndCheck(
            SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                StorageEnvironmentContexts.basic(environmentContext)
            )
        );
    }

    @Test
    public void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    // setCurrentWorkingDirectory.......................................................................................

    @Test
    public void testSetCurrentWorkingDirectory() {
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

    // serverUrl........................................................................................................

    @Test
    public void testServerUrlMissing() {
        assertThrows(
            MissingEnvironmentValueException.class,
            () -> SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
            ).serverUrl()
        );
    }

    @Test
    public void testServerUrl() {
        final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        storageEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );

        this.serverUrlAndCheck(
            SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                storageEnvironmentContext
            ),
            SERVER_URL
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    public void testSpreadsheetIdMissing() {
        this.spreadsheetIdAndCheck(
            SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
            )
        );
    }

    @Test
    public void testSpreadsheetId() {
        this.spreadsheetIdAndCheck(
            this.createContext(),
            SPREADSHEET_ID
        );
    }

    // setSpreadsheetId.................................................................................................

    @Test
    public void testSetSpreadsheetId() {
        this.setSpreadsheetIdAndCheck(
            (SpreadsheetEnvironmentContextBasic)
                SpreadsheetEnvironmentContextBasic.with(
                    STORAGE,
                    STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
                ),
            SPREADSHEET_ID
        );
    }

    @Test
    public void testSetSpreadsheetId2() {
        final SpreadsheetEnvironmentContextBasic context = (SpreadsheetEnvironmentContextBasic)
            SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
            );

        this.setSpreadsheetIdAndCheck(
            context,
            SPREADSHEET_ID
        );

        final SpreadsheetId spreadsheetId2 = SpreadsheetId.with(2);

        this.setSpreadsheetIdAndCheck(
            context,
            spreadsheetId2
        );
    }

    // lineEnding.......................................................................................................

    @Test
    public void testSetLineEnding() {
        final SpreadsheetEnvironmentContextBasic context = this.createContext();

        final LineEnding lineEnding = LineEnding.CRNL;
        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        this.lineEndingAndCheck(
            context,
            LINE_ENDING
        );

        this.setLineEndingAndCheck(
            context,
            lineEnding
        );
    }

    // indentation......................................................................................................

    @Test
    public void testSetIndentation() {
        final SpreadsheetEnvironmentContextBasic context = this.createContext();

        final Indentation indentation = Indentation.SPACES4;
        this.checkNotEquals(
            INDENTATION,
            indentation
        );

        this.indentationAndCheck(
            context,
            INDENTATION
        );

        this.setIndentationAndCheck(
            context,
            indentation
        );
    }
    
    // locale...........................................................................................................

    @Test
    public void testSetLocale() {
        final SpreadsheetEnvironmentContextBasic context = this.createContext();

        final Locale locale = Locale.GERMANY;
        this.checkNotEquals(
            LOCALE,
            locale
        );

        this.localeAndCheck(
            context,
            LOCALE
        );

        this.setLocaleAndCheck(
            context,
            locale
        );
    }

    // user.............................................................................................................

    @Test
    public void testSetUser() {
        final SpreadsheetEnvironmentContextBasic context = this.createContext();

        final Optional<EmailAddress> user = Optional.of(DIFFERENT_USER);
        this.checkNotEquals(
            USER,
            user
        );

        this.userAndCheck(
            context,
            USER
        );

        this.setUserAndCheck(
            context,
            user
        );
    }

    @Test
    public void testParseEnvironmentValueNameAfterSetEnvironmentValue() {
        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "magic",
            String.class
        );

        final SpreadsheetEnvironmentContextBasic context = this.createContext();
        this.setEnvironmentValueAndCheck(
            context,
            name,
            "value123"
        );

        this.parseEnvironmentValueNameAndCheck(
            context,
            name
        );
    }

    // storage..........................................................................................................

    @Test
    public void testStorage() {
        this.storageAndCheck(
            this.createContext(),
            STORAGE
        );
    }

    // SpreadsheetEnvironmentContext....................................................................................

    @Override
    public SpreadsheetEnvironmentContextBasic createContext() {
        final StorageEnvironmentContext storageEnvironmentContext = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();

        storageEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );
        storageEnvironmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID
        );

        return (SpreadsheetEnvironmentContextBasic)
            SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                storageEnvironmentContext
            );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentStorage() {
        final StorageEnvironmentContext storageEnvironmentContext = StorageEnvironmentContexts.fake();

        this.checkNotEquals(
            SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                storageEnvironmentContext
            ),
            SpreadsheetEnvironmentContextBasic.with(
                Storages.fake(),
                storageEnvironmentContext
            )
        );
    }

    @Test
    public void testEqualsDifferentEnvironmentContext() {
        this.checkNotEquals(
            SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                StorageEnvironmentContexts.fake()
            ),
            SpreadsheetEnvironmentContextBasic.with(
                STORAGE,
                StorageEnvironmentContexts.fake()
            )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{charset=UTF-8, currency=AUD, currentWorkingDirectory=/current1/working2/directory3, homeDirectory=/users/user123@example.com, indentation=\"  \", lineEnding=\"\\n\", locale=en_AU, serverUrl=https://example.com, spreadsheetId=123, timeOffset=Z, user=user123@example.com}"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createContext(),
            "SpreadsheetEnvironmentContextBasic\n" +
                "  environment\n" +
                "    StorageEnvironmentContextBasic\n" +
                "      EnvironmentContextSharedMap\n" +
                "        charset\n" +
                "          UTF-8 (sun.nio.cs.UTF_8)\n" +
                "        currency\n" +
                "          AUD (java.util.Currency)\n" +
                "        currentWorkingDirectory\n" +
                "          /current1/working2/directory3\n" +
                "        homeDirectory\n" +
                "          /users/user123@example.com\n" +
                "        indentation\n" +
                "          \"  \" (walkingkooka.text.Indentation)\n" +
                "        lineEnding\n" +
                "          \"\\n\"\n" +
                "        locale\n" +
                "          en_AU (java.util.Locale)\n" +
                "        now\n" +
                "          1999-12-31T12:58:59 (java.time.LocalDateTime)\n" +
                "        serverUrl\n" +
                "          https://example.com (walkingkooka.net.AbsoluteUrl)\n" +
                "        spreadsheetId\n" +
                "          123\n" +
                "        timeOffset\n" +
                "          Z (java.time.ZoneOffset)\n" +
                "        user\n" +
                "          user123@example.com (walkingkooka.net.email.EmailAddress)\n" +
                "  storage\n" +
                "    FakeStorage (walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextBasicTest$1)\n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEnvironmentContextBasic> type() {
        return SpreadsheetEnvironmentContextBasic.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }
}
