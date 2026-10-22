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
import walkingkooka.color.Color;
import walkingkooka.environment.EnvironmentContextTesting2;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.storage.StorageEnvironmentContextTesting2;
import walkingkooka.terminal.TerminalContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetEnvironmentContextTesting2<C extends SpreadsheetEnvironmentContext> extends SpreadsheetEnvironmentContextTesting,
    EnvironmentContextTesting2<C>,
    StorageEnvironmentContextTesting2<C> {

    // parseEnvironmentValueName........................................................................................

    @Test
    default void testParseEnvironmentValueNameWithColorPrefix() {
        final String name = "color1";

        this.parseEnvironmentValueNameAndCachedCheck(
            name,
            EnvironmentValueName.with(
                name,
                Color.class
            )
        );
    }

    @Test
    default void testParseEnvironmentValueNameWithColorPrefixMixedCase() {
        final String name = "COLOR1";

        this.parseEnvironmentValueNameAndCachedCheck(
            name,
            EnvironmentValueName.with(
                name,
                Color.class
            )
        );
    }

    @Test
    default void testParseEnvironmentValueNameWithColorPrefix2() {
        final String name = "color2";

        this.parseEnvironmentValueNameAndCachedCheck(
            name,
            EnvironmentValueName.with(
                name,
                Color.class
            )
        );
    }

    @Test
    default void testParseEnvironmentValueNameWithColorPrefix99() {
        final String name = "color99";

        this.parseEnvironmentValueNameAndCheck(
            name,
            EnvironmentValueName.with(
                name,
                Color.class
            )
        );
    }

    @Test
    default void testParseEnvironmentValueNameWithServerUrl() {
        this.parseEnvironmentValueNameAndCachedCheck(SpreadsheetEnvironmentContext.SERVER_URL);
    }

    @Test
    default void testParseEnvironmentValueNameWithSpreadsheetId() {
        this.parseEnvironmentValueNameAndCachedCheck(SpreadsheetEnvironmentContext.SPREADSHEET_ID);
    }

    @Test
    default void testParseEnvironmentValueNameWithSpreadsheetMetadata() {
        this.parseEnvironmentValueNameAndCachedCheck(SpreadsheetEnvironmentContext.SPREADSHEET_METADATA);
    }

    @Test
    default void testParseEnvironmentValueNameWithTerminalId() {
        this.parseEnvironmentValueNameAndCachedCheck(TerminalContext.TERMINAL_ID);
    }

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

    @Test
    default void testSetServerWithDifferentUrlFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setEnvironmentValue(
                    SpreadsheetEnvironmentContext.SERVER_URL,
                    DIFFERENT_SERVER_URL
                )
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    default void testSpreadsheetIdAndEnvironmentValueName() {
        final C context = this.createContext();

        this.environmentValueAndCheck(
            context,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
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

        try {
            this.setSpreadsheetIdAndCheck(
                context,
                context.spreadsheetId()
            );
        } catch (final ReadOnlyEnvironmentValueException ignore) {
            // nop
        }
    }

    // setSpreadsheetMetadata...........................................................................................

    @Test
    default void testSetSpreadsheetMetadataFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setEnvironmentValue(
                    SpreadsheetEnvironmentContext.SPREADSHEET_METADATA,
                    SpreadsheetMetadata.EMPTY
                )
        );
    }

    @Test
    default void testSetEnvironmentValueWithSpreadsheetMetadataAndSpreadsheetMetadataPropertyNameFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .setEnvironmentValue(
                    EnvironmentValueName.with(
                        SpreadsheetEnvironmentContext.SPREADSHEET_METADATA_PREFIX + SpreadsheetMetadataPropertyName.AUDIT_INFO.value(),
                        Object.class
                    ),
                    SpreadsheetMetadata.EMPTY
                )
        );
    }

    // RemoveEnvironmentValue...........................................................................................

    @Test
    default void testRemoveEnvironmentValueWithSpreadsheetMetadataFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .removeEnvironmentValue(
                    SpreadsheetEnvironmentContext.SPREADSHEET_METADATA
                )
        );
    }

    @Test
    default void testRemoveEnvironmentValueWithSpreadsheetMetadataAndSpreadsheetMetadataPropertyNameFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> this.createContext()
                .removeEnvironmentValue(
                    EnvironmentValueName.with(
                        SpreadsheetEnvironmentContext.SPREADSHEET_METADATA_PREFIX + SpreadsheetMetadataPropertyName.AUDIT_INFO.value(),
                        Object.class
                    )
                )
        );
    }

    // type.............................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetEnvironmentContext.class.getSimpleName();
    }

}
