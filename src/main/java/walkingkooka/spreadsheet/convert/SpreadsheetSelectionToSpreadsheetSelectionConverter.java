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
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

/**
 * A {@link Converter} that only handles the following selection to selection conversions.
 * <ul>
 *     <li>{@link SpreadsheetCellRangeReference} to {@link SpreadsheetCellReference}</li>
 *     <li>{@link SpreadsheetCellReference} to {@link SpreadsheetCellRangeReference}</li>
 * </ul>
 */
final class SpreadsheetSelectionToSpreadsheetSelectionConverter implements Converter<SpreadsheetConverterContext> {

    /**
     * Singleton
     */
    final static SpreadsheetSelectionToSpreadsheetSelectionConverter INSTANCE = new SpreadsheetSelectionToSpreadsheetSelectionConverter();

    private SpreadsheetSelectionToSpreadsheetSelectionConverter() {
        super();
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return isCellOrCellRange(value, type) ||
                isCellToCellRange(value, type) ||
                isCellRangeToCell(value, type) ||
                isExpressionReference(value, type) ||
                isSelection(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type,
                                         final SpreadsheetConverterContext context) {
        return isCellToCellRange(value, type) ?
                this.successfulConversion(
                        cellToCellRange((SpreadsheetCellReference) value),
                        type
                ) :
                isCellRangeToCell(value, type) ?
                        this.successfulConversion(
                                cellRangeToCell((SpreadsheetCellRangeReference) value),
                                type
                        ) :
                        isCellOrCellRange(value, type) ?
                                this.successfulConversion(
                                        context.resolveIfLabel((SpreadsheetSelection) value),
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

    private static boolean isCellOrCellRange(final Object value,
                                             final Class<?> type) {
        return value instanceof SpreadsheetSelection &&
                (
                        SpreadsheetCellReference.class == type ||
                                SpreadsheetCellRangeReference.class == type ||
                                SpreadsheetCellReferenceOrRange.class == type
                );
    }

    private static boolean isCellToCellRange(final Object value,
                                             final Class<?> type) {
        return value instanceof SpreadsheetCellReference && SpreadsheetCellRangeReference.class == type;
    }

    private static boolean isCellRangeToCell(final Object value,
                                             final Class<?> type) {
        return value instanceof SpreadsheetCellRangeReference && SpreadsheetCellReference.class == type;
    }

    private static boolean isExpressionReference(final Object value,
                                                 final Class<?> type) {
        return value instanceof SpreadsheetExpressionReference && SpreadsheetExpressionReference.class == type;
    }

    private static boolean isSelection(final Object value,
                                       final Class<?> type) {
        return value instanceof SpreadsheetSelection && SpreadsheetSelection.class == type;
    }

    private static SpreadsheetCellRangeReference cellToCellRange(final SpreadsheetCellReference cell) {
        return cell.toCellRange();
    }

    private static SpreadsheetCellReference cellRangeToCell(final SpreadsheetCellRangeReference range) {
        return range.toCell();
    }

    @Override
    public String toString() {
        return "selection to selection";
    }
}
