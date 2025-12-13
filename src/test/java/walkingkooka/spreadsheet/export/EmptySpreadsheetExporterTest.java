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
import walkingkooka.ToStringTesting;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCellRange;
import walkingkooka.spreadsheet.value.SpreadsheetCellValueKind;

public final class EmptySpreadsheetExporterTest implements SpreadsheetExporterTesting<EmptySpreadsheetExporter>,
    ToStringTesting<EmptySpreadsheetExporter> {

    @Test
    public void testCanExportFalse() {
        this.canExportAndCheck(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.ALL_CELLS,
                Sets.empty()
            ),
            SpreadsheetCellValueKind.CELL,
            false
        );
    }

    @Test
    public void testExportFails() {
        this.exportFails(
            SpreadsheetCellRange.with(
                SpreadsheetSelection.parseCellRange("A1:B2"),
                Sets.empty()
            ),
            SpreadsheetCellValueKind.CELL,
            new IllegalArgumentException("Cannot export A1:B2")
        );
    }

    @Override
    public EmptySpreadsheetExporter createSpreadsheetExporter() {
        return EmptySpreadsheetExporter.INSTANCE;
    }

    @Override
    public SpreadsheetExporterContext createContext() {
        return SpreadsheetExporterContexts.fake();
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            EmptySpreadsheetExporter.INSTANCE,
            "EmptySpreadsheetExporter"
        );
    }

    // class............................................................................................................

    @Override
    public Class<EmptySpreadsheetExporter> type() {
        return EmptySpreadsheetExporter.class;
    }
}
