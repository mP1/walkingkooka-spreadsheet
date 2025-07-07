
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public abstract class SpreadsheetCellReferenceOrRangeTestCase<R extends SpreadsheetCellReferenceOrRange> extends SpreadsheetExpressionReferenceTestCase<R> {

    SpreadsheetCellReferenceOrRangeTestCase() {
        super();
    }

    // toCellOrCellRange................................................................................................

    @Test
    public final void testToCellOrCellRange() {
        final R selection = this.createSelection();
        final SpreadsheetCellReferenceOrRange cellOrCellRange = selection.toCellOrCellRange();

        assertSame(
            selection,
            cellOrCellRange
        );
    }

    // toRange.........................................................................................................

    @Test
    public final void testToRange() {
        final R selection = this.createSelection();

        this.toRangeAndCheck(
            selection,
            this.parseRange(
                selection.toString()
            )
        );
    }
}
