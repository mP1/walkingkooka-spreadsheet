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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.compare.Range;
import walkingkooka.tree.json.JsonNode;

import java.util.List;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} without any window/filtering.
 */
final class SpreadsheetDeltaNonWindowed extends SpreadsheetDelta {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaNonWindowed} without copying or filtering the cells.
     */
    static SpreadsheetDeltaNonWindowed with0(final SpreadsheetId id,
                                             final Set<SpreadsheetCell> cells) {
        return new SpreadsheetDeltaNonWindowed(id, cells);
    }

    private SpreadsheetDeltaNonWindowed(final SpreadsheetId id,
                                        final Set<SpreadsheetCell> cells) {
        super(id, cells);
    }

    @Override
    public SpreadsheetDelta setCells(final Set<SpreadsheetCell> cells) {
        checkCells(cells);

        final Set<SpreadsheetCell> copy = Sets.immutable(cells);
        return this.cells.equals(copy) ?
                this :
                new SpreadsheetDeltaNonWindowed(this.id, copy);
    }

    /**
     * There is no window.
     */
    @Override
    public List<SpreadsheetRange> window() {
        return Lists.empty();
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDeltaNonWindowed;
    }

    @Override
    boolean equals1(final SpreadsheetDelta other) {
        return other instanceof SpreadsheetDeltaNonWindowed;
    }

    @Override
    public JsonNode toJsonNode() {
        return this.toJsonNodeIdAndCells();
    }
}
