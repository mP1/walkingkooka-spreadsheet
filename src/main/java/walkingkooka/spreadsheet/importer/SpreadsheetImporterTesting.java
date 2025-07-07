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
import walkingkooka.collect.list.Lists;
import walkingkooka.net.WebEntity;
import walkingkooka.text.printer.TreePrintableTesting;

import java.util.List;

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

    // doImportAndCheck.................................................................................................

    default void doImportAndCheck(final WebEntity cells,
                                  final SpreadsheetImporterCellValue... expected) {
        this.doImportAndCheck(
            cells,
            Lists.of(expected)
        );
    }

    default void doImportAndCheck(final WebEntity cells,
                                  final List<SpreadsheetImporterCellValue> expected) {
        this.doImportAndCheck(
            cells,
            this.createContext(),
            expected
        );
    }

    default void doImportAndCheck(final WebEntity cells,
                                  final SpreadsheetImporterContext context,
                                  final SpreadsheetImporterCellValue... expected) {
        this.doImportAndCheck(
            cells,
            context,
            Lists.of(expected)
        );
    }

    default void doImportAndCheck(final WebEntity cells,
                                  final SpreadsheetImporterContext context,
                                  final List<SpreadsheetImporterCellValue> expected) {
        this.doImportAndCheck(
            this.createSpreadsheetImporter(),
            cells,
            context,
            expected
        );
    }

    default void doImportAndCheck(final I importer,
                                  final WebEntity cells,
                                  final SpreadsheetImporterCellValue... expected) {
        this.doImportAndCheck(
            importer,
            cells,
            Lists.of(expected)
        );
    }

    default void doImportAndCheck(final I importer,
                                  final WebEntity cells,
                                  final List<SpreadsheetImporterCellValue> expected) {
        this.doImportAndCheck(
            importer,
            cells,
            this.createContext(),
            expected
        );
    }

    default void doImportAndCheck(final I importer,
                                  final WebEntity cells,
                                  final SpreadsheetImporterContext context,
                                  final SpreadsheetImporterCellValue... expected) {
        this.doImportAndCheck(
            importer,
            cells,
            context,
            Lists.of(
                expected
            )
        );
    }

    default void doImportAndCheck(final I importer,
                                  final WebEntity cells,
                                  final SpreadsheetImporterContext context,
                                  final List<SpreadsheetImporterCellValue> expected) {
        this.canImportAndCheck(
            importer,
            cells,
            context,
            true
        );

        this.checkEquals(
            expected,
            importer.doImport(
                cells,
                context
            )
        );
    }

    // doImportFails....................................................................................................

    default void doImportFails(final WebEntity cells,
                               final RuntimeException expected) {
        this.doImportFails(
            cells,
            this.createContext(),
            expected
        );
    }

    default void doImportFails(final I importer,
                               final WebEntity cells,
                               final RuntimeException expected) {
        this.doImportFails(
            importer,
            cells,
            this.createContext(),
            expected
        );
    }

    default void doImportFails(final WebEntity cells,
                               final SpreadsheetImporterContext context,
                               final RuntimeException expected) {
        this.doImportFails(
            this.createSpreadsheetImporter(),
            cells,
            context,
            expected
        );
    }

    default void doImportFails(final I importer,
                               final WebEntity cells,
                               final SpreadsheetImporterContext context,
                               final RuntimeException expected) {
        final RuntimeException thrown = assertThrows(
            expected.getClass(),
            () -> importer.doImport(
                cells,
                context
            )
        );
        this.checkEquals(
            expected.getMessage(),
            thrown.getMessage(),
            "message"
        );
    }

    I createSpreadsheetImporter();

    SpreadsheetImporterContext createContext();
}
