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
 * Base class for all selection types, including columns, rows, cells, labels and ranges.
 */
public abstract class SpreadsheetSelection {

    SpreadsheetSelection() {
        super();
    }

    public final boolean isCellReference() {
        return this instanceof SpreadsheetCellReference;
    }

    public final boolean isColumnReference() {
        return this instanceof SpreadsheetColumnReference;
    }

    public final boolean isLabelName() {
        return this instanceof SpreadsheetLabelName;
    }

    public final boolean isRange() {
        return this instanceof SpreadsheetRange;
    }

    public final boolean isRowReference() {
        return this instanceof SpreadsheetRowReference;
    }

    // SpreadsheetSelectionVisitor......................................................................................

    abstract void accept(final SpreadsheetSelectionVisitor visitor);
}
