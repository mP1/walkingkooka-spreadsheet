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
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
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
                                MediaType.ALL,
                                this.createContext()
                        )
        );
    }

    @Test
    default void testCanExportWithNullContentTypeFails() {
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
                                MediaType.ALL,
                                null
                        )
        );
    }

    default void canExportAndCheck(final SpreadsheetCellRange cells,
                                   final MediaType contentType,
                                   final boolean expected) {
        this.canExportAndCheck(
                cells,
                contentType,
                this.createContext(),
                expected
        );
    }

    default void canExportAndCheck(final SpreadsheetCellRange cells,
                                   final MediaType contentType,
                                   final SpreadsheetExporterContext context,
                                   final boolean expected) {
        this.canExportAndCheck(
                this.createSpreadsheetExporter(),
                cells,
                contentType,
                context,
                expected
        );
    }

    default void canExportAndCheck(final E exporter,
                                   final SpreadsheetCellRange cells,
                                   final MediaType contentType,
                                   final SpreadsheetExporterContext context,
                                   final boolean expected) {
        this.checkEquals(
                expected,
                exporter.canExport(
                        cells,
                        contentType,
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
                                MediaType.ALL,
                                this.createContext()
                        )
        );
    }

    @Test
    default void testExportCellsWithNullContentTypeFails() {
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
                                MediaType.ALL,
                                null
                        )
        );
    }

    default void exportAndCheck(final SpreadsheetCellRange cells,
                                final MediaType contentType,
                                final WebEntity expected) {
        this.exportAndCheck(
                cells,
                contentType,
                this.createContext(),
                expected
        );
    }

    default void exportAndCheck(final SpreadsheetCellRange cells,
                                final MediaType contentType,
                                final SpreadsheetExporterContext context,
                                final WebEntity expected) {
        this.exportAndCheck(
                this.createSpreadsheetExporter(),
                cells,
                contentType,
                context,
                expected
        );
    }

    default void exportAndCheck(final E exporter,
                                final SpreadsheetCellRange cells,
                                final MediaType contentType,
                                final WebEntity expected) {
        this.exportAndCheck(
                exporter,
                cells,
                contentType,
                this.createContext(),
                expected
        );
    }

    default void exportAndCheck(final E exporter,
                                final SpreadsheetCellRange cells,
                                final MediaType contentType,
                                final SpreadsheetExporterContext context,
                                final WebEntity expected) {
        this.canExportAndCheck(
                exporter,
                cells,
                contentType,
                context,
                true
        );

        this.checkEquals(
                expected,
                exporter.export(
                        cells,
                        contentType,
                        context
                )
        );
    }

    // exportFails......................................................................................................

    default void exportFails(final SpreadsheetCellRange cells,
                             final MediaType contentType,
                             final RuntimeException expected) {
        this.exportFails(
                cells,
                contentType,
                this.createContext(),
                expected
        );
    }

    default void exportFails(final E exporter,
                             final SpreadsheetCellRange cells,
                             final MediaType contentType,
                             final RuntimeException expected) {
        this.exportFails(
                exporter,
                cells,
                contentType,
                this.createContext(),
                expected
        );
    }

    default void exportFails(final SpreadsheetCellRange cells,
                             final MediaType contentType,
                             final SpreadsheetExporterContext context,
                             final RuntimeException expected) {
        this.exportFails(
                this.createSpreadsheetExporter(),
                cells,
                contentType,
                context,
                expected
        );
    }

    default void exportFails(final E exporter,
                             final SpreadsheetCellRange cells,
                             final MediaType contentType,
                             final SpreadsheetExporterContext context,
                             final RuntimeException expected) {
        final RuntimeException thrown = assertThrows(
                expected.getClass(),
                () -> exporter.export(
                        cells,
                        contentType,
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
