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

/**
 * Base class for {@link SpreadsheetRowReference} and {@link SpreadsheetRowRangeReference}.
 */
public abstract class SpreadsheetRowReferenceOrRange extends SpreadsheetColumnOrRowReferenceOrRange {

    SpreadsheetRowReferenceOrRange() {
        super();
    }

    static void checkColumnDeltaIsZero(final int column) {
        if (0 != column) {
            throw new IllegalArgumentException("Invalid non zero column delta " + column);
        }
    }

    @Override final boolean testColumnNonNull(final SpreadsheetColumnReference column) {
        return false;
    }

    @Override
    public final SpreadsheetColumnReference toColumn() {
        throw new UnsupportedOperationException(this.toString());
    }

    @Override
    public final SpreadsheetColumnRangeReference toColumnRange() {
        throw new UnsupportedOperationException(this.toString());
    }
}
