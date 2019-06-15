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

import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReference;
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetReferenceKind;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReference;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;

import java.util.Collection;
import java.util.Optional;

/**
 * Performs operations on rows for delete or insertion.
 */
final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow extends BasicSpreadsheetEngineDeleteOrInsertColumnOrRowColumnOrRow {

    static BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow with(final int value,
                                                                   final int count,
                                                                   final BasicSpreadsheetEngine engine,
                                                                   final SpreadsheetEngineContext context) {
        return new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow(value, count, engine, context);
    }

    /**
     * Private ctor use static factory.
     */
    private BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow(final int value,
                                                               final int count,
                                                               final BasicSpreadsheetEngine engine,
                                                               final SpreadsheetEngineContext context) {
        super(value, count, engine, context);
    }

    @Override
    final int max() {
        return this.maxRow();
    }

    @Override
    final Collection<SpreadsheetCell> cells(final int row) {
        return this.rowCells(SpreadsheetReferenceKind.ABSOLUTE.row(row));
    }

    @Override
    Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression(final SpreadsheetColumnReferenceParserToken token) {
        // only fixing rows refs not cols
        return Optional.of(token);
    }

    @Override
    Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression(final SpreadsheetRowReferenceParserToken token) {
        return this.deleteOrInsert.isDeletedReference(token) ?
                INVALID_CELL_REFERENCE :
                this.fixCellReferencesWithinExpression0(token);
    }

    private Optional<SpreadsheetParserToken> fixCellReferencesWithinExpression0(final SpreadsheetRowReferenceParserToken token) {
        final SpreadsheetRowReference old = token.value();
        final int value = old.value();

        SpreadsheetRowReferenceParserToken result = token;

        if (value > this.value) {
            final SpreadsheetRowReference reference = old.setValue(value + this.deleteOrInsert.fixReferenceOffset(this.count));
            result = SpreadsheetParserToken.rowReference(reference, reference.toString());
        }

        return Optional.of(result);
    }

    @Override
    SpreadsheetCellReference fixCellReference(final SpreadsheetCellReference reference) {
        return reference.addRow(this.deleteOrInsert.fixReferenceOffset(this.count));
    }

    @Override
    final int columnOrRowValue(final SpreadsheetCellReference cell) {
        return cell.row().value();
    }

    @Override
    SpreadsheetCellReference setColumnOrRowValue(final SpreadsheetCellReference cell, final int value) {
        return cell.setRow(cell.row().setValue(value));
    }

    @Override
    SpreadsheetCellReference addColumnOrRowValue(final SpreadsheetCellReference cell, final int value) {
        return cell.setRow(cell.row().add(value));
    }
}
