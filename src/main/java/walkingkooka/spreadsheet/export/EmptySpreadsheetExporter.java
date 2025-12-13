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

import java.util.Objects;

/**
 * A {@link SpreadsheetExporter} that cannot export and always fails.
 */
final class EmptySpreadsheetExporter implements SpreadsheetExporter {

    final static EmptySpreadsheetExporter INSTANCE = new EmptySpreadsheetExporter();

    private EmptySpreadsheetExporter() {
        super();
    }

    @Override
    public boolean canExport(final SpreadsheetCellRange cells,
                             final SpreadsheetCellValueKind valueKind,
                             final SpreadsheetExporterContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(valueKind, "valueKind");
        Objects.requireNonNull(context, "context");

        return false;
    }

    @Override
    public WebEntity export(final SpreadsheetCellRange cells,
                            final SpreadsheetCellValueKind valueKind,
                            final SpreadsheetExporterContext context) {
        Objects.requireNonNull(cells, "cells");
        Objects.requireNonNull(valueKind, "valueKind");
        Objects.requireNonNull(context, "context");

        throw new IllegalArgumentException("Cannot export " + cells.range());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
