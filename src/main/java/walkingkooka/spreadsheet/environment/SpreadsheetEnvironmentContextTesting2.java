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
import walkingkooka.environment.EnvironmentContextTesting2;
import walkingkooka.spreadsheet.SpreadsheetContext;
import walkingkooka.spreadsheet.SpreadsheetId;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEnvironmentContextTesting2<C extends SpreadsheetEnvironmentContext> extends EnvironmentContextTesting2<C> {

    // serverUrl........................................................................................................

    @Test
    default void testEnvironmentValueNameWithServerUrl() {
        final C context = this.createContext();

        this.environmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContext.SERVER_URL,
            context.serverUrl()
        );
    }
    
    // spreadsheetId....................................................................................................

    @Test
    default void testEnvironmentValueNameWithSpreadsheetId() {
        final C context = this.createContext();

        this.environmentValueAndCheck(
            context,
            SpreadsheetContext.SPREADSHEET_ID,
            context.spreadsheetId()
        );
    }

    // setSpreadsheetId.................................................................................................

    @Test
    default void testSetSpreadsheetIdWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .setSpreadsheetId(null)
        );
    }

    @Test
    default void testSetSpreadsheetIdWithSame() {
        final C context = this.createContext();

        assertSame(
            context,
            context.setSpreadsheetId(
                context.spreadsheetId()
            )
        );
    }

    default void setSpreadsheetIdAndCheck(final C context,
                                          final SpreadsheetId spreadsheetId) {

    }
}
