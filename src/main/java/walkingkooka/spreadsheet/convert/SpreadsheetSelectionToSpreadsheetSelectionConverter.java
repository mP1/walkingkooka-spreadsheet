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

import walkingkooka.convert.ConversionException;
import walkingkooka.convert.Converter;
import walkingkooka.convert.TryingShortCircuitingConverter;
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
final class SpreadsheetSelectionToSpreadsheetSelectionConverter implements TryingShortCircuitingConverter<SpreadsheetConverterContext> {

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
        boolean can = false;

        if (value instanceof SpreadsheetSelection) {
            final SpreadsheetSelection selection = (SpreadsheetSelection) value;

            can = isCellOrCellRange(selection, type) ||
                isCellToCellRange(selection, type) ||
                isCellRangeToCell(selection, type) ||
                isExpressionReference(selection, type) ||
                isSelection(selection, type);
        }

        return can;
    }

    @Override
    public SpreadsheetSelection tryConvertOrFail(final Object value,
                                                 final Class<?> type,
                                                 final SpreadsheetConverterContext context) {
        final SpreadsheetSelection result;

        final SpreadsheetSelection selection = (SpreadsheetSelection) value;

        if (isCellToCellRange(selection, type)) {
            result = selection.toCellRange();
        } else {
            if (isCellRangeToCell(selection, type)) {
                result = selection.toCell();
            } else {
                if (isCellOrCellRange(selection, type)) {
                    result = context.resolveIfLabelOrFail(selection);
                } else {
                    if (isExpressionReference(selection, type)) {
                        result = selection;
                    } else {
                        if (isSelection(selection, type)) {
                            result = selection;
                        } else {
                            throw new ConversionException(
                                "Cant convert " + value + " to " + type.getName(),
                                value,
                                type
                            );
                        }
                    }
                }
            }
        }

        return result;
    }

    private static boolean isCellOrCellRange(final SpreadsheetSelection value,
                                             final Class<?> type) {
        return value instanceof SpreadsheetSelection &&
            (
                SpreadsheetCellReference.class == type ||
                    SpreadsheetCellRangeReference.class == type ||
                    SpreadsheetCellReferenceOrRange.class == type
            );
    }

    private static boolean isCellToCellRange(final SpreadsheetSelection value,
                                             final Class<?> type) {
        return value instanceof SpreadsheetCellReference && SpreadsheetCellRangeReference.class == type;
    }

    private static boolean isCellRangeToCell(final SpreadsheetSelection value,
                                             final Class<?> type) {
        return value instanceof SpreadsheetCellRangeReference && SpreadsheetCellReference.class == type;
    }

    private static boolean isExpressionReference(final SpreadsheetSelection value,
                                                 final Class<?> type) {
        return value instanceof SpreadsheetExpressionReference && SpreadsheetExpressionReference.class == type;
    }

    private static boolean isSelection(final Object value,
                                       final Class<?> type) {
        return value instanceof SpreadsheetSelection && SpreadsheetSelection.class == type;
    }

    @Override
    public String toString() {
        return "selection to selection";
    }
}
