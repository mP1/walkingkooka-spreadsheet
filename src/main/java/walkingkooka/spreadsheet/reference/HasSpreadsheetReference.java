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

import java.util.Comparator;

/**
 * Declares that a spreadsheet component includes a reference which may be used to identify itself, such as a
 * {@link walkingkooka.spreadsheet.SpreadsheetCell}, {@link walkingkooka.spreadsheet.SpreadsheetColumn} or {@link walkingkooka.spreadsheet.SpreadsheetRow}.
 */
public interface HasSpreadsheetReference<R extends Comparable<R>> {

    /**
     * Returns a {@link Comparator} that may be used to sort a {@link HasSpreadsheetReference} such as a {@link walkingkooka.spreadsheet.SpreadsheetCell}
     */
    static <H extends HasSpreadsheetReference<R>, R extends Comparable<R>> Comparator<H> hasSpreadsheetReferenceComparator() {
        return (l, r) -> l.reference().compareTo(r.reference());
    }

    /**
     * Helper that attempts to cast the {@link walkingkooka.tree.expression.ExpressionReference} to
     * {@link SpreadsheetSelection} if possible or throwing a {@link IllegalArgumentException}.
     */
    default SpreadsheetSelection toSpreadsheetSelection() {
        final R reference = this.reference();
        if(reference instanceof SpreadsheetSelection) {
            return (SpreadsheetSelection) reference;
        }

        throw new IllegalArgumentException("Not a " + SpreadsheetSelection.class.getSimpleName() + ": " + reference);
    }

    /**
     * Returns the reference for the spreadsheet unit.
     */
    R reference();
}
