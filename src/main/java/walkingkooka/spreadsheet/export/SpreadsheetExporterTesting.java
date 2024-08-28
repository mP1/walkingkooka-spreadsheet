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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or explied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.spreadsheet.export;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.WebEntity;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExporterTesting<E extends SpreadsheetExporter> extends TreePrintableTesting {

    // canExport........................................................................................................

    @Test
    default void testCanExportWithNullWebEntityFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExporter()
                        .canExport(
                                null,
                                this.createContext()
                        )
        );
    }

    @Test
    default void testCanExportWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExporter()
                        .canExport(
                                SpreadsheetCellRange.with(
                                        SpreadsheetSelection.ALL_CELLS,
                                        Sets.empty()
                                ),
                                null
                        )
        );
    }

    default void canExportAndCheck(final SpreadsheetCellRange cells,
                                   final boolean expected) {
        this.canExportAndCheck(
                cells,
                this.createContext(),
                expected
        );
    }

    default void canExportAndCheck(final SpreadsheetCellRange cells,
                                   final SpreadsheetExporterContext context,
                                   final boolean expected) {
        this.canExportAndCheck(
                this.createSpreadsheetExporter(),
                cells,
                context,
                expected
        );
    }

    default void canExportAndCheck(final E exporter,
                                   final SpreadsheetCellRange cells,
                                   final SpreadsheetExporterContext context,
                                   final boolean expected) {
        this.checkEquals(
                expected,
                exporter.canExport(
                        cells,
                        context
                )
        );
    }

    // export...........................................................................................................

    @Test
    default void testExportCellsWithNullCellsFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExporter()
                        .export(
                                null,
                                this.createContext()
                        )
        );
    }

    @Test
    default void testExportCellsWithNullContextFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createSpreadsheetExporter()
                        .export(
                                SpreadsheetCellRange.with(
                                        SpreadsheetSelection.ALL_CELLS,
                                        Sets.empty()
                                ),
                                null
                        )
        );
    }

    default void exportAndCheck(final SpreadsheetCellRange cells,
                                final WebEntity expected) {
        this.exportAndCheck(
                cells,
                this.createContext(),
                expected
        );
    }

    default void exportAndCheck(final SpreadsheetCellRange cells,
                                final SpreadsheetExporterContext context,
                                final WebEntity expected) {
        this.exportAndCheck(
                this.createSpreadsheetExporter(),
                cells,
                context,
                expected
        );
    }

    default void exportAndCheck(final E exporter,
                                final SpreadsheetCellRange cells,
                                final SpreadsheetExporterContext context,
                                final WebEntity expected) {
        this.checkEquals(
                expected,
                exporter.export(
                        cells,
                        context
                )
        );
    }

    E createSpreadsheetExporter();

    SpreadsheetExporterContext createContext();
}
