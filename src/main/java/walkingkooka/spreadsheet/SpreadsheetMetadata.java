/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet;

import walkingkooka.*;
import walkingkooka.test.*;

import java.util.*;

/**
 * Metadata about a spreadsheet.
 */
public final class SpreadsheetMetadata implements HashCodeEqualsDefined {

    public static SpreadsheetMetadata with(final int columnCount, final int rowCount) {
        checkColumnCount(columnCount);
        checkRowCount(rowCount);

        return new SpreadsheetMetadata(columnCount, rowCount);
    }

    private static void checkColumnCount(final int count) {
        checkCount("Column", count);
    }

    private static void checkRowCount(final int count) {
        checkCount("Row", count);
    }

    private static void checkCount(final String label, final int count) {
        if(count < 0) {
            throw new IllegalArgumentException(label + "=" + count + " must be a positive number");
        }
    }

    private SpreadsheetMetadata(final int columnCount, final int rowCount) {
        super();

        this.columnCount = columnCount;
        this.rowCount = rowCount;
    }

    public int columnCount() {
        return this.columnCount;
    }

    public SpreadsheetMetadata setColumnCount(final int columnCount) {
        checkColumnCount(columnCount);

        return this.columnCount == columnCount ?
               this :
               this.replace(columnCount, this.rowCount());
    }

    private int columnCount;

    public int rowCount() {
        return this.rowCount;
    }

    public SpreadsheetMetadata setRowCount(final int rowCount) {
        checkRowCount(rowCount);

        return this.rowCount == rowCount ?
                this :
                this.replace(this.columnCount(), rowCount);
    }

    private int rowCount;

    private SpreadsheetMetadata replace(final int columnCount, final int rowCount) {
        return new SpreadsheetMetadata(columnCount, rowCount);
    }

    // HashCodeEqualsDefined..........................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(this.columnCount, this.rowCount);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
               other instanceof SpreadsheetMetadata &&
               this.equals0(Cast.to(other));
    }

    private boolean equals0(final SpreadsheetMetadata info) {
        return this.columnCount==info.columnCount() &&
               this.rowCount==info.rowCount();
    }

    @Override
    public String toString() {
        return this.columnCount + "x" + this.rowCount;
    }
}
