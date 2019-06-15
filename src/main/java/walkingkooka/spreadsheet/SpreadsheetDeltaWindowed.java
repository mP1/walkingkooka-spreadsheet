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
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParsers;
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
                                          final List<Range<SpreadsheetCellReference>> window) {
        return new SpreadsheetDeltaWindowed(id, cells, window);
    }

    private SpreadsheetDeltaWindowed(final SpreadsheetId id,
                                     final Set<SpreadsheetCell> cells,
                                     final List<Range<SpreadsheetCellReference>> window) {
        super(id, cells);
        this.window = window;
    }

    @Override
    public SpreadsheetDelta setCells(final Set<SpreadsheetCell> cells) {
        checkCells(cells);

        final List<Range<SpreadsheetCellReference>> window = this.window;
        final Set<SpreadsheetCell> filtered = filterCells(cells, window);
        return this.cells.equals(filtered) ?
                this :
                new SpreadsheetDeltaWindowed(this.id, filtered, window);
    }

    @Override
    public List<Range<SpreadsheetCellReference>> window() {
        return this.window;
    }

    private final List<Range<SpreadsheetCellReference>> window;

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
                .map(SpreadsheetDeltaWindowed::toStringRange)
                .collect(Collectors.joining(WINDOW_SEPARATOR)));
    }

    private static String toStringRange(final Range<SpreadsheetCellReference> range) {
        return toStringRangeBound(range.lowerBound())
                .concat(SpreadsheetParsers.RANGE_SEPARATOR.string())
                .concat(toStringRangeBound(range.upperBound()));
    }

    private static String toStringRangeBound(final RangeBound<SpreadsheetCellReference> bound) {
        return bound.value().map(Object::toString).orElse("");
    }
}
