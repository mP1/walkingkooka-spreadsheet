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
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCellRange;
import walkingkooka.spreadsheet.value.SpreadsheetCellValueKind;

import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetExporterTestingTest implements SpreadsheetExporterTesting<SpreadsheetExporterTestingTest.TestSpreadsheetExporter> {

    private final static WebEntity WEB_ENTITY = WebEntity.empty()
        .setContentType(Optional.of(MediaType.TEXT_PLAIN))
        .setText("Hello");

    @Test
    public void testExportFails() {
        final String message = "Fail message 123";

        this.exportFails(
            new SpreadsheetExporterTestingTest.TestSpreadsheetExporter() {
                @Override
                public WebEntity export(final SpreadsheetCellRange cells,
                                        final SpreadsheetCellValueKind valueKind,
                                        final SpreadsheetExporterContext context) {
                    throw new IllegalArgumentException(message);
                }
            },
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.empty()
            ),
            SpreadsheetCellValueKind.CELL,
            new IllegalArgumentException(message)
        );
    }

    @Test
    public void testExportAndCheck() {
        final SpreadsheetCellValueKind valueKind = SpreadsheetCellValueKind.FORMULA;

        final WebEntity webEntity = WebEntity.empty()
            .setContentType(
                Optional.of(MediaType.TEXT_PLAIN)
            );
        final SpreadsheetCellRange cells = SpreadsheetCellRange.with(
            SpreadsheetSelection.ALL_CELLS,
            Sets.empty()
        );

        this.exportAndCheck(
            new SpreadsheetExporterTestingTest.TestSpreadsheetExporter() {
                @Override
                public WebEntity export(final SpreadsheetCellRange r,
                                        final SpreadsheetCellValueKind k,
                                        final SpreadsheetExporterContext context) {
                    checkEquals(cells, r);
                    checkEquals(valueKind, k);

                    return webEntity;
                }
            },
            cells,
            valueKind,
            webEntity
        );
    }

    @Override
    public TestSpreadsheetExporter createSpreadsheetExporter() {
        return new TestSpreadsheetExporter();
    }

    @Override
    public SpreadsheetExporterContext createContext() {
        return SpreadsheetExporterContexts.fake();
    }

    static class TestSpreadsheetExporter implements SpreadsheetExporter {

        @Override
        public boolean canExport(final SpreadsheetCellRange cells,
                                 final SpreadsheetCellValueKind kind,
                                 final SpreadsheetExporterContext context) {
            Objects.requireNonNull(cells, "cells");
            Objects.requireNonNull(kind, "kind");
            Objects.requireNonNull(context, "context");

            return true;
        }

        @Override
        public WebEntity export(final SpreadsheetCellRange cells,
                                final SpreadsheetCellValueKind kind,
                                final SpreadsheetExporterContext context) {
            Objects.requireNonNull(cells, "cells");
            Objects.requireNonNull(kind, "kind");
            Objects.requireNonNull(context, "context");

            return WEB_ENTITY;
        }
    }
}
