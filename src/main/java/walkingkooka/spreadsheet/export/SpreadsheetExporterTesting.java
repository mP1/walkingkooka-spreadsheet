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

package walkingkooka.spreadsheet.export;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.WebEntity;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCellRange;
import walkingkooka.spreadsheet.value.SpreadsheetCellValueKind;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExporterTesting<E extends SpreadsheetExporter> extends TreePrintableTesting {

    // canExport........................................................................................................

    @Test
    default void testCanExportWithNullCellsFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetExporter()
                .canExport(
                    null,
                    SpreadsheetCellValueKind.CELL,
                    this.createContext()
                )
        );
    }

    @Test
    default void testCanExportWithNullValueKindFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetExporter()
                .canExport(
                    SpreadsheetCellRange.with(
                        SpreadsheetSelection.ALL_CELLS,
                        Sets.empty()
                    ),
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
                    SpreadsheetCellValueKind.CELL,
                    null
                )
        );
    }

    default void canExportAndCheck(final SpreadsheetCellRange cells,
                                   final SpreadsheetCellValueKind valueKind,
                                   final boolean expected) {
        this.canExportAndCheck(
            cells,
            valueKind,
            this.createContext(),
            expected
        );
    }

    default void canExportAndCheck(final SpreadsheetCellRange cells,
                                   final SpreadsheetCellValueKind valueKind,
                                   final SpreadsheetExporterContext context,
                                   final boolean expected) {
        this.canExportAndCheck(
            this.createSpreadsheetExporter(),
            cells,
            valueKind,
            context,
            expected
        );
    }

    default void canExportAndCheck(final E exporter,
                                   final SpreadsheetCellRange cells,
                                   final SpreadsheetCellValueKind valueKind,
                                   final SpreadsheetExporterContext context,
                                   final boolean expected) {
        this.checkEquals(
            expected,
            exporter.canExport(
                cells,
                valueKind,
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
                    SpreadsheetCellValueKind.CELL,
                    this.createContext()
                )
        );
    }

    @Test
    default void testExportCellsWithNullValueKindFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createSpreadsheetExporter()
                .export(
                    SpreadsheetCellRange.with(
                        SpreadsheetSelection.ALL_CELLS,
                        Sets.empty()
                    ),
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
                    SpreadsheetCellValueKind.CELL,
                    null
                )
        );
    }

    default void exportAndCheck(final SpreadsheetCellRange cells,
                                final SpreadsheetCellValueKind valueKind,
                                final WebEntity expected) {
        this.exportAndCheck(
            cells,
            valueKind,
            this.createContext(),
            expected
        );
    }

    default void exportAndCheck(final SpreadsheetCellRange cells,
                                final SpreadsheetCellValueKind valueKind,
                                final SpreadsheetExporterContext context,
                                final WebEntity expected) {
        this.exportAndCheck(
            this.createSpreadsheetExporter(),
            cells,
            valueKind,
            context,
            expected
        );
    }

    default void exportAndCheck(final E exporter,
                                final SpreadsheetCellRange cells,
                                final SpreadsheetCellValueKind valueKind,
                                final WebEntity expected) {
        this.exportAndCheck(
            exporter,
            cells,
            valueKind,
            this.createContext(),
            expected
        );
    }

    default void exportAndCheck(final E exporter,
                                final SpreadsheetCellRange cells,
                                final SpreadsheetCellValueKind valueKind,
                                final SpreadsheetExporterContext context,
                                final WebEntity expected) {
        this.canExportAndCheck(
            exporter,
            cells,
            valueKind,
            context,
            true
        );

        this.checkEquals(
            expected,
            exporter.export(
                cells,
                valueKind,
                context
            )
        );
    }

    // exportFails......................................................................................................

    default void exportFails(final SpreadsheetCellRange cells,
                             final SpreadsheetCellValueKind valueKind,
                             final RuntimeException expected) {
        this.exportFails(
            cells,
            valueKind,
            this.createContext(),
            expected
        );
    }

    default void exportFails(final E exporter,
                             final SpreadsheetCellRange cells,
                             final SpreadsheetCellValueKind valueKind,
                             final RuntimeException expected) {
        this.exportFails(
            exporter,
            cells,
            valueKind,
            this.createContext(),
            expected
        );
    }

    default void exportFails(final SpreadsheetCellRange cells,
                             final SpreadsheetCellValueKind valueKind,
                             final SpreadsheetExporterContext context,
                             final RuntimeException expected) {
        this.exportFails(
            this.createSpreadsheetExporter(),
            cells,
            valueKind,
            context,
            expected
        );
    }

    default void exportFails(final E exporter,
                             final SpreadsheetCellRange cells,
                             final SpreadsheetCellValueKind valueKind,
                             final SpreadsheetExporterContext context,
                             final RuntimeException expected) {
        final RuntimeException thrown = assertThrows(
            expected.getClass(),
            () -> exporter.export(
                cells,
                valueKind,
                context
            )
        );
        this.checkEquals(
            expected.getMessage(),
            thrown.getMessage(),
            "message"
        );
    }

    E createSpreadsheetExporter();

    SpreadsheetExporterContext createContext();
}
