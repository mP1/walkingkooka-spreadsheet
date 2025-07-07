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

package walkingkooka.spreadsheet.reference;

import walkingkooka.spreadsheet.SpreadsheetCell;

import java.util.Comparator;
import java.util.Objects;

/**
 * A {@link Comparator} that wraps a {@link Comparator} that compares {@link SpreadsheetCell#reference()}.
 */
final class SpreadsheetCellReferenceComparator implements Comparator<SpreadsheetCell> {

    static SpreadsheetCellReferenceComparator with(final Comparator<SpreadsheetCellReference> comparator) {
        Objects.requireNonNull(comparator, "compare");
        return new SpreadsheetCellReferenceComparator(comparator);
    }

    public SpreadsheetCellReferenceComparator(final Comparator<SpreadsheetCellReference> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(final SpreadsheetCell left,
                       final SpreadsheetCell right) {
        return this.comparator.compare(
            left.reference(),
            right.reference()
        );
    }

    private final Comparator<SpreadsheetCellReference> comparator;

    @Override
    public String toString() {
        return this.comparator.toString();
    }
}
