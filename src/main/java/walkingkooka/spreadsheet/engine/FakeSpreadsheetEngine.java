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

package walkingkooka.spreadsheet.engine;

import walkingkooka.compare.Range;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.spreadsheet.SpreadsheetRowReference;
import walkingkooka.test.Fake;

import java.util.Collection;
import java.util.Optional;

public class FakeSpreadsheetEngine implements SpreadsheetEngine, Fake {
    @Override
    public SpreadsheetId id() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Optional<SpreadsheetCellReference>> loadCell(final SpreadsheetCellReference cell,
                                                                         final SpreadsheetEngineEvaluation evaluation,
                                                                         final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Optional<SpreadsheetCellReference>> saveCell(final SpreadsheetCell cell,
                                                                         final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Optional<SpreadsheetCellReference>> deleteCell(final SpreadsheetCellReference cell,
                                                                           final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Range<SpreadsheetColumnReference>> deleteColumns(final SpreadsheetColumnReference column,
                                                                             final int count,
                                                                             final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Range<SpreadsheetRowReference>> deleteRows(final SpreadsheetRowReference row,
                                                                       final int count,
                                                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Range<SpreadsheetColumnReference>> insertColumns(final SpreadsheetColumnReference column,
                                                                             final int count,
                                                                             final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Range<SpreadsheetRowReference>> insertRows(final SpreadsheetRowReference row,
                                                                       final int count,
                                                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Range<SpreadsheetCellReference>> copyCells(final Collection<SpreadsheetCell> from,
                                                                       final SpreadsheetRange to,
                                                                       final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Optional<SpreadsheetLabelName>> saveLabel(final SpreadsheetLabelMapping mapping,
                                                                      final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetDelta<Optional<SpreadsheetLabelName>> removeLabel(final SpreadsheetLabelName label,
                                                                        final SpreadsheetEngineContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetLabelMapping> loadLabel(final SpreadsheetLabelName name) {
        throw new UnsupportedOperationException();
    }
}
