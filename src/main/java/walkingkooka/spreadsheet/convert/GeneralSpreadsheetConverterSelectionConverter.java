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

package walkingkooka.spreadsheet.convert;

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumberConverterContext;

/**
 * A {@link Converter} that only handles the following selection to selection conversions.
 * <ul>
 *     <li>{@link walkingkooka.spreadsheet.reference.SpreadsheetCellRange} to {@link walkingkooka.spreadsheet.reference.SpreadsheetCellReference}</li>
 *     <li>{@link walkingkooka.spreadsheet.reference.SpreadsheetCellReference} to {@link walkingkooka.spreadsheet.reference.SpreadsheetCellRange}</li>
 * </ul>
 */
final class GeneralSpreadsheetConverterSelectionConverter implements Converter<ExpressionNumberConverterContext> {

    /**
     * Singleton
     */
    final static GeneralSpreadsheetConverterSelectionConverter INSTANCE = new GeneralSpreadsheetConverterSelectionConverter();

    private GeneralSpreadsheetConverterSelectionConverter() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final ExpressionNumberConverterContext context) {
        return isCellRangeToCell(value, type) ||
                isCellToCellRange(value, type) ||
                isExpressionReference(value, type) ||
                isSelection(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final ExpressionNumberConverterContext context) {
        return isCellToCellRange(value, type) ?
                this.successfulConversion(
                        cellToCellRange((SpreadsheetCellReference) value),
                        type
                ) :
                isCellRangeToCell(value, type) ?
                        this.successfulConversion(
                                cellRangeToCell((SpreadsheetCellRange) value),
                                type
                        ) :
                        isExpressionReference(value, type) ?
                                this.successfulConversion(
                                        value,
                                        type
                                ) :
                                isSelection(value, type) ?
                                        this.successfulConversion(
                                                value,
                                                type
                                        ) :
                                        this.failConversion(value, type);
    }

    private static boolean isCellToCellRange(Object value, Class<?> type) {
        return value instanceof SpreadsheetCellReference && SpreadsheetCellRange.class == type;
    }

    private static boolean isCellRangeToCell(Object value, Class<?> type) {
        return value instanceof SpreadsheetCellRange && SpreadsheetCellReference.class == type;
    }

    private static boolean isExpressionReference(final Object value,
                                                 final Class<?> type) {
        return value instanceof SpreadsheetExpressionReference && SpreadsheetExpressionReference.class == type;
    }

    private static boolean isSelection(final Object value,
                                       final Class<?> type) {
        return value instanceof SpreadsheetSelection && SpreadsheetSelection.class == type;
    }

    private static SpreadsheetCellRange cellToCellRange(final SpreadsheetCellReference cell) {
        return cell.cellRange();
    }

    private static SpreadsheetCellReference cellRangeToCell(final SpreadsheetCellRange range) {
        return range.toCell();
    }

    @Override
    public String toString() {
        return "cell->cellRange|cellRange->cell";
    }
}
