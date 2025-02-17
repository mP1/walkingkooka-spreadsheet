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
 * A {@link Comparator} that may be used to compare all types of {@link SpreadsheetSelection} ignoring their {@link SpreadsheetReferenceKind}.
 */
final class SpreadsheetSelectionIgnoresReferenceKindComparator implements Comparator<SpreadsheetSelection> {

    /**
     * Singleton
     */
    final static SpreadsheetSelectionIgnoresReferenceKindComparator INSTANCE = new SpreadsheetSelectionIgnoresReferenceKindComparator();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetSelectionIgnoresReferenceKindComparator() {
        super();
    }

    @Override
    public int compare(final SpreadsheetSelection left,
                       final SpreadsheetSelection right) {
        return left.toRelative()
                .toString()
                .compareToIgnoreCase(
                        right.toRelative()
                                .toString()
                );
    }

    @Override
    public String toString() {
        return Comparator.class.getSimpleName() +
                "(" +
                SpreadsheetSelection.class.getSimpleName() +
                " ignoring " +
                SpreadsheetReferenceKind.class.getSimpleName() +
                ")";
    }
}
