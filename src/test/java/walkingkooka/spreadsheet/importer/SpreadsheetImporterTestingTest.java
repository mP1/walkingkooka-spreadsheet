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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.WebEntity;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetImporterTestingTest implements SpreadsheetImporterTesting<SpreadsheetImporterTestingTest.TestSpreadsheetImporter> {

    private final static SpreadsheetCellRange CELL_RANGE = SpreadsheetCellRange.with(
            SpreadsheetSelection.ALL_CELLS,
            Sets.of(
                    SpreadsheetSelection.A1.setFormula(SpreadsheetFormula.EMPTY)
            )
    );

    @Test
    public void testImportCellsAndCheck() {
        final WebEntity webEntity = WebEntity.empty()
                .setContentType(
                        Optional.of(
                                MediaType.TEXT_PLAIN
                        )
                );
        final SpreadsheetCellRange cells = SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.empty()
        );

        this.importCellsAndCheck(
                new TestSpreadsheetImporter() {
                    @Override
                    public SpreadsheetCellRange importCells(final WebEntity w,
                                                            final SpreadsheetImporterContext context) {
                        checkEquals(webEntity, w);
                        return cells;
                    }
                },
                webEntity,
                cells
        );
    }

    @Test
    public void testImportCellsFails() {
        final String message = "Fail message 123";

        this.importCellsFails(
                new TestSpreadsheetImporter() {
                    @Override
                    public SpreadsheetCellRange importCells(final WebEntity cells,
                                                            final SpreadsheetImporterContext context) {
                        throw new IllegalArgumentException(message);
                    }
                },
                WebEntity.empty(),
                new IllegalArgumentException(message)
        );
    }

    @Override
    public TestSpreadsheetImporter createSpreadsheetImporter() {
        return new TestSpreadsheetImporter();
    }

    @Override
    public SpreadsheetImporterContext createContext() {
        return SpreadsheetImporterContexts.fake();
    }

    static class TestSpreadsheetImporter implements SpreadsheetImporter {

        @Override
        public boolean canImport(final WebEntity cells,
                                 final SpreadsheetImporterContext context) {
            Objects.requireNonNull(cells, "cells");
            Objects.requireNonNull(context, "context");

            return true;
        }

        @Override
        public SpreadsheetCellRange importCells(final WebEntity cells,
                                                final SpreadsheetImporterContext context) {
            Objects.requireNonNull(cells, "cells");
            Objects.requireNonNull(context, "context");

            return CELL_RANGE;
        }
    }
}
