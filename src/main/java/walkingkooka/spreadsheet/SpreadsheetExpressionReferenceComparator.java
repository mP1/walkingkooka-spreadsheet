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

import java.util.Comparator;

/**
 * A {@link Comparator} that accepts all {@link SpreadsheetExpressionReference}. {@link SpreadsheetLabelName}
 * are considered to come before {@link SpreadsheetCellReference}. Note the {@link SpreadsheetReferenceKind} of
 * any cell components are unimportant.
 */
final class SpreadsheetExpressionReferenceComparator implements Comparator<SpreadsheetExpressionReference> {

    /**
     * Singleton
     */
    final static SpreadsheetExpressionReferenceComparator INSTANCE = new SpreadsheetExpressionReferenceComparator();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetExpressionReferenceComparator() {
        super();
    }

    @Override
    public int compare(final SpreadsheetExpressionReference first, final SpreadsheetExpressionReference second) {
        return first.compare(second);
    }

    @Override
    public String toString() {
        return SpreadsheetCellReference.class.getSimpleName() + " < " + SpreadsheetLabelName.class.getSimpleName();
    }
}
