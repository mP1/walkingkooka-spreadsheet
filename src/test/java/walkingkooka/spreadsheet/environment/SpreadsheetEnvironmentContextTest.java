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
import walkingkooka.environment.CanParseEnvironmentValueNameTesting;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.storage.StorageEnvironmentContext;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEnvironmentContextTest implements ClassTesting2<SpreadsheetEnvironmentContext>,
    CanParseEnvironmentValueNameTesting,
    ThrowableTesting {

    // TERMINAL_CONTEXT_PARSE...........................................................................................

    @Test
    public void testSpreadsheetEnvironmentContextParseWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContext.SPREADSHEET_ENVIRONMENT_CONTEXT_PARSE.parseEnvironmentValueName(null)
        );
    }

    @Test
    public void testSpreadsheetEnvironmentContextParseWithUnknownFails() {
        final IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> SpreadsheetEnvironmentContext.SPREADSHEET_ENVIRONMENT_CONTEXT_PARSE.parseEnvironmentValueName("unknown")
        );

        this.getMessageAndCheck(
            thrown,
            "Unknown environment value name \"unknown\""
        );
    }

    @Test
    public void testSpreadsheetEnvironmentContextParseWithCharset() {
        this.parseEnvironmentValueNameAndCheck(
            SpreadsheetEnvironmentContext.SPREADSHEET_ENVIRONMENT_CONTEXT_PARSE,
            EnvironmentValueName.CHARSET
        );
    }

    @Test
    public void testSpreadsheetEnvironmentContextParseWithHomeDirectory() {
        this.parseEnvironmentValueNameAndCheck(
            SpreadsheetEnvironmentContext.SPREADSHEET_ENVIRONMENT_CONTEXT_PARSE,
            StorageEnvironmentContext.HOME_DIRECTORY
        );
    }

    @Test
    public void testSpreadsheetEnvironmentContextParseWithServerUrl() {
        this.parseEnvironmentValueNameAndCheck(
            SpreadsheetEnvironmentContext.SPREADSHEET_ENVIRONMENT_CONTEXT_PARSE,
            SpreadsheetEnvironmentContext.SERVER_URL
        );
    }

    @Test
    public void testSpreadsheetEnvironmentContextParseWithEnvironmentConstants() throws Exception {
        int i = 0;

        for (final Field field : EnvironmentContext.class.getDeclaredFields()) {
            if (field.getType() == EnvironmentValueName.class) {
                this.parseEnvironmentValueNameAndCheck(
                    SpreadsheetEnvironmentContext.SPREADSHEET_ENVIRONMENT_CONTEXT_PARSE,
                    (EnvironmentValueName<?>) field.get(null)
                );
                i++;
            }
        }

        this.checkNotEquals(
            0,
            i
        );
    }
    
    @Test
    public void testServerUrlConstant() {
        final EnvironmentValueName<AbsoluteUrl> serverUrl = SpreadsheetEnvironmentContext.SERVER_URL;

        assertSame(
            serverUrl,
            EnvironmentValueName.with(
                "serverUrl",
                AbsoluteUrl.class
            )
        );
    }

    @Test
    public void testSpreadsheetIdConstant() {
        final EnvironmentValueName<SpreadsheetId> spreadsheetId = SpreadsheetEnvironmentContext.SPREADSHEET_ID;

        assertSame(
            spreadsheetId,
            EnvironmentValueName.with(
                "spreadsheetId",
                SpreadsheetId.class
            )
        );
    }
    
    // class............................................................................................................

    @Override
    public Class<SpreadsheetEnvironmentContext> type() {
        return SpreadsheetEnvironmentContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
