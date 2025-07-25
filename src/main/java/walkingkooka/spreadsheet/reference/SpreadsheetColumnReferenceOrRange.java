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

public abstract class SpreadsheetColumnReferenceOrRange extends SpreadsheetColumnOrRowReferenceOrRange {

    SpreadsheetColumnReferenceOrRange() {
        super();
    }

    static void checkRowDeltaIsZero(final int row) {
        if (0 != row) {
            throw new IllegalArgumentException("Invalid non zero row delta " + row);
        }
    }

    @Override //
    final boolean testRowNonNull(final SpreadsheetRowReference row) {
        return false;
    }

    @Override
    public final SpreadsheetRowReference toRow() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public final SpreadsheetRowRangeReference toRowRange() {
        throw new UnsupportedOperationException(this.toString());
    }
}
