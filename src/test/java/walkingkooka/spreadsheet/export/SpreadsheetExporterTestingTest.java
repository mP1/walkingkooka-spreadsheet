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
import walkingkooka.spreadsheet.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

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
                                            final SpreadsheetExporterContext context) {
                        throw new IllegalArgumentException(message);
                    }
                },
                SpreadsheetCellRange.with(
                        SpreadsheetSelection.ALL_CELLS,
                        Sets.empty()
                ),
                new IllegalArgumentException(message)
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
                                 final SpreadsheetExporterContext context) {
            Objects.requireNonNull(cells, "cells");
            Objects.requireNonNull(context, "context");

            return true;
        }

        @Override
        public WebEntity export(final SpreadsheetCellRange cells,
                                final SpreadsheetExporterContext context) {
            Objects.requireNonNull(cells, "cells");
            Objects.requireNonNull(context, "context");

            return WEB_ENTITY;
        }
    }
}
