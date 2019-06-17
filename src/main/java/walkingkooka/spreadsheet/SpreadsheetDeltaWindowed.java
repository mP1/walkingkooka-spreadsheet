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

package walkingkooka.spreadsheet;

import walkingkooka.compare.Range;
import walkingkooka.compare.RangeBound;
import walkingkooka.spreadsheet.parser.SpreadsheetParsers;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonStringNode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link SpreadsheetDelta} with a non empty window.
 */
final class SpreadsheetDeltaWindowed extends SpreadsheetDelta {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaWindowed} without checking, copying or filtering the cells.
     */
    static SpreadsheetDeltaWindowed with0(final SpreadsheetId id,
                                          final Set<SpreadsheetCell> cells,
                                          final List<SpreadsheetRange> window) {
        return new SpreadsheetDeltaWindowed(id, cells, window);
    }

    private SpreadsheetDeltaWindowed(final SpreadsheetId id,
                                     final Set<SpreadsheetCell> cells,
                                     final List<SpreadsheetRange> window) {
        super(id, cells);
        this.window = window;
    }

    @Override
    public SpreadsheetDelta setCells(final Set<SpreadsheetCell> cells) {
        checkCells(cells);

        final List<SpreadsheetRange> window = this.window;
        final Set<SpreadsheetCell> filtered = filterCells(cells, window);
        return this.cells.equals(filtered) ?
                this :
                new SpreadsheetDeltaWindowed(this.id, filtered, window);
    }

    @Override
    public List<SpreadsheetRange> window() {
        return this.window;
    }

    private final List<SpreadsheetRange> window;

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDeltaWindowed;
    }

    @Override
    boolean equals1(final SpreadsheetDelta other) {
        return this.window.equals(other.window());
    }

    @Override
    public JsonNode toJsonNode() {
        return this.toJsonNodeIdAndCells()
                .set(WINDOW_PROPERTY, this.windowToJsonNode());
    }

    private JsonStringNode windowToJsonNode() {
        return JsonNode.string(this.window.stream()
                .map(SpreadsheetRange::toString)
                .collect(Collectors.joining(WINDOW_SEPARATOR)));
    }
}
