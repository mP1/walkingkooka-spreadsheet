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
import walkingkooka.net.http.server.hateos.HasHateosLinkId;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetRange;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A {@link SpreadsheetDelta} without any window/filtering.
 */
final class SpreadsheetDeltaIdWindowed<I extends Comparable<I> & HasHateosLinkId> extends SpreadsheetDeltaId<I> {

    /**
     * Factory that creates a new {@link SpreadsheetDeltaIdWindowed} without copying or filtering the cells.
     */
    static <I extends Comparable<I> & HasHateosLinkId> SpreadsheetDeltaIdWindowed<I> with(final Optional<I> id,
                                                                                          final Set<SpreadsheetCell> cells,
                                                                                          final List<SpreadsheetRange> window) {
        return new SpreadsheetDeltaIdWindowed<>(id, cells, window);
    }

    private SpreadsheetDeltaIdWindowed(final Optional<I> id,
                                       final Set<SpreadsheetCell> cells,
                                       final List<SpreadsheetRange> window) {
        super(id, cells);
        this.window = window;
    }

    @Override
    <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Optional<II>> replaceId(final Optional<II> id) {
        return new SpreadsheetDeltaIdWindowed<>(id, this.cells, this.window);
    }

    @Override
    <II extends Comparable<II> & HasHateosLinkId> SpreadsheetDelta<Range<II>> replaceRange(final Range<II> range) {
        return SpreadsheetDeltaRangeWindowed.with(range, this.cells, this.window);
    }

    @Override
    public List<SpreadsheetRange> window() {
        return this.window;
    }

    private final List<SpreadsheetRange> window;

    @Override
    Set<SpreadsheetCell> copyCells(Set<SpreadsheetCell> cells) {
        return maybeFilterCells(cells, this.window);
    }

    @Override
    boolean canBeEquals(final Object other) {
        return other instanceof SpreadsheetDeltaIdWindowed;
    }

    @Override
    boolean equals1(final SpreadsheetDelta other) {
        return this.window.equals(other.window());
    }
}
