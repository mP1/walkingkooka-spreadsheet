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

package walkingkooka.spreadsheet.engine.hateos;

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.map.Maps;
import walkingkooka.compare.Range;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.Map;
import java.util.stream.Collectors;

final class SpreadsheetEngineLoadCellHateosHandlerBatchLoader {

    static SpreadsheetEngineLoadCellHateosHandlerBatchLoader with(final SpreadsheetEngineLoadCellHateosHandler handler) {
        return new SpreadsheetEngineLoadCellHateosHandlerBatchLoader(handler);
    }


    private SpreadsheetEngineLoadCellHateosHandlerBatchLoader(final SpreadsheetEngineLoadCellHateosHandler handler) {
        super();
        this.handler = handler;
    }

    SpreadsheetDelta batchLoad(final Range<SpreadsheetCellReference> cells) {
        SpreadsheetRange.with(cells).cellStream()
                .forEach(this::maybeLoadCell);
        return this.referenceToCell.isEmpty() ?
                null :
                SpreadsheetDelta.with(this.referenceToCell.values()
                        .stream()
                        .collect(Collectors.toSet()));
    }

    private void maybeLoadCell(final SpreadsheetCellReference reference) {
        if (false == this.referenceToCell.containsKey(reference)) {
            final SpreadsheetDelta loaded = this.handler.loadCell(reference);
            loaded.cells()
                    .stream()
                    .forEach(this::add);
        }
    }

    private void add(final SpreadsheetCell cell) {
        this.referenceToCell.put(cell.reference(), cell);
    }

    /**
     * Tracks cells that have been loaded.
     */
    final Map<SpreadsheetCellReference, SpreadsheetCell> referenceToCell = Maps.sorted(SpreadsheetCellReference.COMPARATOR);

    /**
     * The handler is used to load individual cells
     */
    final SpreadsheetEngineLoadCellHateosHandler handler;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.referenceToCell)
                .value(this.handler)
                .build();
    }
}
