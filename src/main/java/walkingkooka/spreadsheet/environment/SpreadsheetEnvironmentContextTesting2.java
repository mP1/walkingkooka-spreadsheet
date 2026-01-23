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
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.engine.SpreadsheetEngineContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEnvironmentContextTesting2<C extends SpreadsheetEnvironmentContext> extends SpreadsheetEnvironmentContextTesting,
    EnvironmentContextTesting2<C> {

    // serverUrl........................................................................................................

    @Test
    default void testServerUrlAndEnvironmentValueName() {
        final C context = this.createContext();

        AbsoluteUrl serverUrl;
        try {
            serverUrl = context.serverUrl();
        } catch (final RuntimeException ignore) {
            serverUrl = null;
        }

        this.environmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContext.SERVER_URL,
            Optional.ofNullable(serverUrl)
        );
    }
    
    // spreadsheetId....................................................................................................

    @Test
    default void testSpreadsheetIdAndEnvironmentValueName() {
        final C context = this.createContext();

        SpreadsheetId spreadsheetId;
        try {
            spreadsheetId = context.spreadsheetId();
        } catch (final RuntimeException ignore) {
            spreadsheetId = null;
        }

        this.environmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            Optional.ofNullable(spreadsheetId)
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
    default void testSetSpreadsheetIdWithSameIfPresent() {
        final C context = this.createContext();

        final SpreadsheetId spreadsheetId = context.environmentValue(
            SpreadsheetEngineContext.SPREADSHEET_ID
        ).orElse(null);

        if (null != spreadsheetId) {
            try {
                this.setSpreadsheetIdAndCheck(
                    context,
                    context.spreadsheetId()
                );
            } catch (final ReadOnlyEnvironmentValueException ignore) {
                // nop
            }
        }
    }

    default void setSpreadsheetIdAndCheck(final C context,
                                          final SpreadsheetId spreadsheetId) {
        context.setSpreadsheetId(spreadsheetId);

        this.spreadsheetIdAndCheck(
            context,
            spreadsheetId
        );
    }

    // type.............................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetEnvironmentContext.class.getSimpleName();
    }

}
