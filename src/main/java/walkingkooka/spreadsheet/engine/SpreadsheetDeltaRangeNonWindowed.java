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

import walkingkooka.collect.list.Lists;
import walkingkooka.compare.Range;
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRange;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} without any window/filtering.
 */
final class SpreadsheetDeltaRangeNonWindowed<I extends Comparable<I> & HasHateosLinkId> extends SpreadsheetDeltaRange<I> {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaRangeNonWindowed} without copying or filtering the cells.
     */
    static <I extends Comparable<I> & HasHateosLinkId> SpreadsheetDeltaRangeNonWindowed<I> with(final Range<I> id,
                                                                                                final Set<SpreadsheetCell> cells) {
        return new SpreadsheetDeltaRangeNonWindowed<>(id, cells);
    }

    private SpreadsheetDeltaRangeNonWindowed(final Range<I> id,
                                             final Set<SpreadsheetCell> cells) {
        super(id, cells);
    }

    @Override
    <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Optional<II>> replaceId(final Optional<II> id) {
        return SpreadsheetDeltaIdNonWindowed.with(id, this.cells);
    }

    @Override
    <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Range<II>> replaceRange(final Range<II> range) {
        return new SpreadsheetDeltaRangeNonWindowed<>(range, this.cells);
    }

    @Override
    Set<SpreadsheetCell> copyCells(final Set<SpreadsheetCell> cells) {
        return cells; // will be empty no need to copy etc.
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
        return other instanceof SpreadsheetDeltaRangeNonWindowed;
    }

    @Override
    boolean equals1(final SpreadsheetDelta other) {
        return true;
    }
}
