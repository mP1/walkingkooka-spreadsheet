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

package walkingkooka.spreadsheet.importer;

import org.junit.jupiter.api.Test;
import walkingkooka.net.WebEntity;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetImporterTesting<I extends SpreadsheetImporter> extends TreePrintableTesting {

    // canImport........................................................................................................

    @Test
    default void testCanImportWithNullCellsFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetImporter()
                        .canImport(
                                null,
                                this.createContext()
                        )
        );
    }

    @Test
    default void testCanImportWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetImporter()
                        .canImport(
                                WebEntity.empty(),
                                null
                        )
        );
    }

    default void canImportAndCheck(final WebEntity cells,
                                   final boolean expected) {
        this.canImportAndCheck(
                cells,
                this.createContext(),
                expected
        );
    }

    default void canImportAndCheck(final WebEntity cells,
                                   final SpreadsheetImporterContext context,
                                   final boolean expected) {
        this.canImportAndCheck(
                this.createSpreadsheetImporter(),
                cells,
                context,
                expected
        );
    }

    default void canImportAndCheck(final I importer,
                                   final WebEntity cells,
                                   final SpreadsheetImporterContext context,
                                   final boolean expected) {
        this.checkEquals(
                expected,
                importer.canImport(
                        cells,
                        context
                )
        );
    }
    
    // importCells......................................................................................................
    
    @Test
    default void testImportCellsWithNullCellsFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetImporter().importCells(
                        null,
                        this.createContext()
                )
        );
    }

    @Test
    default void testImportCellsWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetImporter().importCells(
                        WebEntity.empty(),
                        null
                )
        );
    }

    default void importCellsAndCheck(final WebEntity cells,
                                     final SpreadsheetCellRange expected) {
        this.importCellsAndCheck(
                cells,
                this.createContext(),
                expected
        );
    }

    default void importCellsAndCheck(final WebEntity cells,
                                     final SpreadsheetImporterContext context,
                                     final SpreadsheetCellRange expected) {
        this.importCellsAndCheck(
                this.createSpreadsheetImporter(),
                cells,
                context,
                expected
        );
    }

    default void importCellsAndCheck(final I importer,
                                     final WebEntity cells,
                                     final SpreadsheetImporterContext context,
                                     final SpreadsheetCellRange expected) {
        this.checkEquals(
                expected,
                importer.importCells(
                        cells,
                        context
                )
        );
    }

    I createSpreadsheetImporter();

    SpreadsheetImporterContext createContext();
}
