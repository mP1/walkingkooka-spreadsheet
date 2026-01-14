
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

package walkingkooka.spreadsheet.storage;

import org.junit.jupiter.api.Test;
import walkingkooka.convert.ConverterLikeTesting;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataContextTesting;
import walkingkooka.storage.StorageContextTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetStorageContextTesting2<C extends SpreadsheetStorageContext> extends SpreadsheetStorageContextTesting,
    StorageContextTesting<C>,
    SpreadsheetMetadataContextTesting<C>,
    SpreadsheetEnvironmentContextTesting2<C>,
    ConverterLikeTesting<C> {

    @Override
    default void testCreateMetadataWithNullLocaleFails() {
        throw new UnsupportedOperationException();
    }

    @Override
    default void testCreateMetadataWithNullUserFails() {
        throw new UnsupportedOperationException();
    }

    // loadCells........................................................................................................

    @Test
    default void testLoadCellsWithNullCellOrLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .loadCells(null)
        );
    }

    // saveCells........................................................................................................

    @Test
    default void testSaveCellsWithNullCellOrLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .saveCells(null)
        );
    }

    // deleteCells......................................................................................................

    @Test
    default void testDeleteCellsWithNullCellOrLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .deleteCells(null)
        );
    }

    // loadLabel........................................................................................................

    @Test
    default void testLoadLabelWithNullLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .loadLabel(null)
        );
    }

    // saveLabel........................................................................................................

    @Test
    default void testSaveLabelWithNullLabelMappingFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .saveLabel(null)
        );
    }

    // deleteLabel......................................................................................................

    @Test
    default void testDeleteLabelWithLabelsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .deleteLabel(null)
        );
    }

    // findLabelsByName.................................................................................................

    @Test
    default void testFindLabelsByNameWithNullTextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .findLabelsByName(
                    null,
                    0, // offset
                    0 // count,
                )
        );
    }

    @Test
    default void testFindLabelsByNameWithNegativeOffsetFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .findLabelsByName(
                    "",
                    -1, // offset
                    0 // count,
                )
        );
    }

    @Test
    default void testFindLabelsByNameWithNegativeCountFails() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.createContext()
                .findLabelsByName(
                    "",
                    0, // offset
                    -1 // count,
                )
        );
    }

    // ConverterLike....................................................................................................

    @Override
    default C createConverterLike() {
        return this.createContext();
    }

    // class............................................................................................................

    @Override
    default void testTypeNaming() {
        StorageContextTesting.super.testTypeNaming();
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetStorageContext.class.getSimpleName();
    }
}
