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
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.MissingEnvironmentValueException;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataTesting;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetEnvironmentContextTest implements SpreadsheetEnvironmentContextTesting2<BasicSpreadsheetEnvironmentContext>,
    SpreadsheetMetadataTesting,
    TreePrintableTesting,
    ToStringTesting<BasicSpreadsheetEnvironmentContext> {

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    // with.............................................................................................................

    @Test
    public void testWithNullEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetEnvironmentContext.with(null)
        );
    }

    @Test
    public void testWithSpreadsheetEnvironmentContext() {
        final SpreadsheetEnvironmentContext wrap = SpreadsheetEnvironmentContexts.fake();

        assertSame(
            wrap,
            BasicSpreadsheetEnvironmentContext.with(wrap)
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSpreadsheetEnvironmentContext() {
        final SpreadsheetEnvironmentContext context = this.createContext();

        final SpreadsheetEnvironmentContext different = SpreadsheetEnvironmentContexts.fake();
        assertSame(
            different,
            context.setEnvironmentContext(different)
        );
    }

    // serverUrl........................................................................................................

    @Test
    public void testServerUrlMissing() {
        assertThrows(
            MissingEnvironmentValueException.class,
            () -> BasicSpreadsheetEnvironmentContext.with(
                EnvironmentContexts.empty(
                    LINE_ENDING,
                    LOCALE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            ).serverUrl()
        );
    }

    @Test
    public void testServerUrl() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                LINE_ENDING,
                LOCALE,
                HAS_NOW,
                EnvironmentContext.ANONYMOUS
            )
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );

        this.serverUrlAndCheck(
            BasicSpreadsheetEnvironmentContext.with(environmentContext),
            SERVER_URL
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    public void testSpreadsheetIdMissing() {
        assertThrows(
            MissingEnvironmentValueException.class,
            () -> BasicSpreadsheetEnvironmentContext.with(
                EnvironmentContexts.empty(
                    LINE_ENDING,
                    LOCALE,
                    HAS_NOW,
                    EnvironmentContext.ANONYMOUS
                )
            ).spreadsheetId()
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
            (BasicSpreadsheetEnvironmentContext)
                BasicSpreadsheetEnvironmentContext.with(
                    EnvironmentContexts.map(
                        EnvironmentContexts.empty(
                            LINE_ENDING,
                            LOCALE,
                            HAS_NOW,
                            EnvironmentContext.ANONYMOUS
                        )
                    )
                ),
            SPREADSHEET_ID
        );
    }

    @Test
    public void testSetSpreadsheetId2() {
        final BasicSpreadsheetEnvironmentContext context = (BasicSpreadsheetEnvironmentContext)
            BasicSpreadsheetEnvironmentContext.with(
                EnvironmentContexts.map(
                    EnvironmentContexts.empty(
                        LINE_ENDING,
                        LOCALE,
                        HAS_NOW,
                        EnvironmentContext.ANONYMOUS
                    )
                )
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
        final BasicSpreadsheetEnvironmentContext context = this.createContext();

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

    // locale...........................................................................................................

    @Test
    public void testSetLocale() {
        final BasicSpreadsheetEnvironmentContext context = this.createContext();

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
        final BasicSpreadsheetEnvironmentContext context = this.createContext();

        final Optional<EmailAddress> user = Optional.of(
            EmailAddress.parse("different@example.com")
        );
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

    // SpreadsheetEnvironmentContext....................................................................................

    @Override
    public BasicSpreadsheetEnvironmentContext createContext() {
        final EnvironmentContext environmentContext = EnvironmentContexts.map(SPREADSHEET_ENVIRONMENT_CONTEXT.cloneEnvironment());
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );
        environmentContext.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SPREADSHEET_ID
        );

        return (BasicSpreadsheetEnvironmentContext)
            BasicSpreadsheetEnvironmentContext.with(environmentContext);
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentContext() {
        this.checkNotEquals(
            BasicSpreadsheetEnvironmentContext.with(
                EnvironmentContexts.fake()
            ),
            BasicSpreadsheetEnvironmentContext.with(
                EnvironmentContexts.fake()
            )
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createContext(),
            "{lineEnding=\"\\n\", locale=en_AU, serverUrl=https://example.com, spreadsheetId=1, user=user@example.com}"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createContext(),
            "BasicSpreadsheetEnvironmentContext\n" +
                "  MapEnvironmentContext\n" +
                "    lineEnding\n" +
                "      \"\\n\"\n" +
                "    locale\n" +
                "      en_AU (java.util.Locale)\n" +
                "    now\n" +
                "      1999-12-31T12:58 (java.time.LocalDateTime)\n" +
                "    serverUrl\n" +
                "      https://example.com (walkingkooka.net.AbsoluteUrl)\n" +
                "    spreadsheetId\n" +
                "      1\n" +
                "    user\n" +
                "      user@example.com (walkingkooka.net.email.EmailAddress)\n" +
                "  \n"
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetEnvironmentContext> type() {
        return BasicSpreadsheetEnvironmentContext.class;
    }
}
