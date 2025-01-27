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

import walkingkooka.spreadsheet.parser.ColumnReferenceSpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.RowReferenceSpreadsheetParserToken;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;

/**
 * Template that includes most of the methods that may be used to delete or insert columns or rows,
 * including fixing of cell references.
 */
abstract class BasicSpreadsheetEngineDeleteOrInsertColumnOrRow {

    BasicSpreadsheetEngineDeleteOrInsertColumnOrRow(final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow) {
        super();
        this.columnOrRow = columnOrRow;
        columnOrRow.deleteOrInsert = this;
    }

    abstract boolean isColumnDeleted(final ColumnReferenceSpreadsheetParserToken column);

    abstract boolean isRowDeleted(final RowReferenceSpreadsheetParserToken row);

    abstract int fixColumnOrRowReference(final int count);

    abstract void fixLabelMapping(final SpreadsheetLabelMapping mapping);

    final BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow columnOrRow;
}
