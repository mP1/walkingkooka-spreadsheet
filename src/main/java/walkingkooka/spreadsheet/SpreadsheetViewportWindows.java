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

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Objects;
import java.util.Set;

/**
 * Captures one or more windows that define the cells within a viewport.
 */
public final class SpreadsheetViewportWindows {

    /**
     * Parses a window query parameter or other string representation into a {@link Set} or {@link SpreadsheetCellRange}.
     * eg
     * <pre>
     * A1
     * B2,C3
     * D4:E5,F6,G7:HI
     * </pre>
     */
    public static SpreadsheetViewportWindows parse(final String windows) {
        return with(
                SpreadsheetSelection.parseWindow(windows)
        );
    }

    public static SpreadsheetViewportWindows with(final Set<SpreadsheetCellRange> cellRanges) {
        Objects.requireNonNull(cellRanges, "cellRanges");
        return new SpreadsheetViewportWindows(Sets.immutable(cellRanges));
    }

    private SpreadsheetViewportWindows(final Set<SpreadsheetCellRange> cellRanges) {
        super();

        this.cellRanges = Sets.immutable(cellRanges);
    }

    public Set<SpreadsheetCellRange> cellRanges() {
        return this.cellRanges;
    }

    private final Set<SpreadsheetCellRange> cellRanges;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.cellRanges.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof SpreadsheetViewportWindows && this.equals0((SpreadsheetViewportWindows) other);
    }

    private boolean equals0(final SpreadsheetViewportWindows other) {
        return this.cellRanges.equals(other.cellRanges);
    }

    @Override
    public String toString() {
        return SpreadsheetSelection.toStringWindow(this.cellRanges);
    }
}
