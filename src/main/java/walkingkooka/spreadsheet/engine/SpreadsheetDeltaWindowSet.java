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

import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.set.ImmutableSetDefaults;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * An immutable {@link java.util.Set} holding the {@link SpreadsheetCellRangeReference} of a {@link SpreadsheetDelta#window()}.
 * It also includes logic to verify that the window ranges do not overlap.
 */
final class SpreadsheetDeltaWindowSet extends AbstractSet<SpreadsheetCellRangeReference>
    implements ImmutableSetDefaults<SpreadsheetDeltaWindowSet, SpreadsheetCellRangeReference> {

    @SuppressWarnings("lgtm[java/abstract-to-concrete-cast]")
    static SpreadsheetDeltaWindowSet with(final Set<SpreadsheetCellRangeReference> window) {
        return window instanceof SpreadsheetDeltaWindowSet ?
            (SpreadsheetDeltaWindowSet) window :
            new SpreadsheetDeltaWindowSet(
                Objects.requireNonNull(window, "window")
                    .toArray(
                        new SpreadsheetCellRangeReference[window.size()]
                    ) // TODO Array.clone.
            );
    }

    private SpreadsheetDeltaWindowSet(final SpreadsheetCellRangeReference[] ranges) {
        final int length = ranges.length;

        switch (length) {
            case 0:
            case 1:
                break;
            case 2:
                failIfOverlaps(ranges[0], ranges[1]);
                break;
            case 3:
            case 4:
                failIfOverlaps(ranges, 0);
                break;
            default:
                throw new IllegalArgumentException("Window only allows 4 ranges=" + Arrays.toString(ranges));
        }

        this.ranges = ranges;
    }

    private static void failIfOverlaps(final SpreadsheetCellRangeReference[] ranges,
                                       final int index) {
        final SpreadsheetCellRangeReference range = ranges[index];

        final int length = ranges.length;
        for (int i = index + 1; i < length; i++) {
            failIfOverlaps(range, ranges[i]);
        }

        if (index + 1 < length) {
            failIfOverlaps(ranges, index + 1);
        }
    }

    private static void failIfOverlaps(final SpreadsheetCellRangeReference left,
                                       final SpreadsheetCellRangeReference right) {
        if (null == left || null == right) {
            throw new NullPointerException("Window includes null cell-range");
        }
        if (left.testCellRange(right)) {
            throw new IllegalArgumentException("Window contains overlapping ranges " + left + " and " + right);
        }
    }

    @Override
    public Iterator<SpreadsheetCellRangeReference> iterator() {
        return Iterators.array(this.ranges);
    }

    @Override
    public int size() {
        return this.ranges.length;
    }

    private final SpreadsheetCellRangeReference[] ranges;

    @Override
    public String toString() {
        return Arrays.toString(this.ranges);
    }

    // ImmutableSetDefaults.............................................................................................

    @Override
    public void elementCheck(final SpreadsheetCellRangeReference cellRange) {
        Objects.requireNonNull(cellRange, "cellRange");
    }

    @Override
    public SpreadsheetDeltaWindowSet setElements(final Set<SpreadsheetCellRangeReference> elements) {
        final SpreadsheetDeltaWindowSet copy = with(elements);
        return this.equals(copy) ?
            this :
            copy;
    }

    @Override
    public Set<SpreadsheetCellRangeReference> toSet() {
        return new TreeSet<>(
            Arrays.asList(this.ranges)
        );
    }
}
