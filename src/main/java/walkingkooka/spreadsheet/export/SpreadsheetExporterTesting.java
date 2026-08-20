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

import walkingkooka.net.WebEntity;
import walkingkooka.spreadsheet.value.SpreadsheetCellRange;
import walkingkooka.spreadsheet.value.SpreadsheetCellValueKind;
import walkingkooka.text.printer.TreePrintableTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetExporterTesting extends TreePrintableTesting {

    default void canExportAndCheck(final SpreadsheetExporter exporter,
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

    default void exportAndCheck(final SpreadsheetExporter exporter,
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

    default void exportFails(final SpreadsheetExporter exporter,
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
}
