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
import walkingkooka.ContextTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetMetadataContextTesting2<C extends SpreadsheetMetadataContext> extends ContextTesting<C>,
    SpreadsheetMetadataContextTesting,
    SpreadsheetMetadataCreatorTesting2<C>,
    SpreadsheetMetadataLoaderTesting2<C> {

    // saveMetadata.....................................................................................................

    @Test
    default void testSaveMetadataWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .saveMetadata(null)
        );
    }

    // deleteMetadata...................................................................................................

    @Test
    default void testDeleteMetadataWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .deleteMetadata(null)
        );
    }

    // findMetadataBySpreadsheetName....................................................................................

    @Test
    default void testFindMetadataBySpreadsheetNameWithNullNameFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .findMetadataBySpreadsheetName(
                    null,
                    0, // offset
                    1 // count
                )
        );
    }

    @Test
    default void testFindMetadataBySpreadsheetNameWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .findMetadataBySpreadsheetName(
                    "Hello",
                    -1, // offset
                    1 // count
                )
        );
    }

    @Test
    default void testFindMetadataBySpreadsheetNameWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .findMetadataBySpreadsheetName(
                    "Hello",
                    0, // offset
                    -1 // count
                )
        );
    }

    // addMetadataWatcher...............................................................................................

    @Test
    default void testAddMetadataWatcherWithNullWatcherFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .addMetadataWatcher(null)
        );
    }

    // addMetadataWatcherOnce...........................................................................................

    @Test
    default void testAddMetadataWatcherOnceWithNullWatcherFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .addMetadataWatcherOnce(null)
        );
    }

    // SpreadsheetMetadataCreator.......................................................................................

    @Override
    default C createSpreadsheetMetadataCreator() {
        return this.createContext();
    }

    // SpreadsheetMetadataLoader........................................................................................

    @Override
    default C createSpreadsheetMetadataLoader() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    default String typeNameSuffix() {
        return SpreadsheetMetadataContext.class.getSimpleName();
    }
}
