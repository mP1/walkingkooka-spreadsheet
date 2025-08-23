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

import walkingkooka.convert.Converter;
import walkingkooka.convert.TryingShortCircuitingConverter;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

/**
 * A {@link Converter} that only handles most selection to selection conversions, that make sense.
 * A {@link SpreadsheetColumnReference} to {@link SpreadsheetCellRangeReference} is supported, A -> A1:A1048576.
 * Other conversions for example are not supported, eg: column to a row.
 * Attempts to convert from a {@link SpreadsheetSelection} to another type such as {@link String} will fail.
 * Attempts to convert from a {@link SpreadsheetLabelName} will attempt to resolve it to a {@link SpreadsheetCellReferenceOrRange},
 * and then the target type.
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
            if (SpreadsheetSelection.isSelectionClass(type)) {
                if (SpreadsheetSelection.class == type) {
                    can = true;
                } else {
                    final SpreadsheetSelection selection = (SpreadsheetSelection) value;
                    if (selection.isExternalReference()) {
                        can = true;
                    } else {
                        if (selection.isColumnOrColumnRange()) {
                            can = isCellRange(type) || isColumn(type);
                        } else {
                            if (selection.isRowOrRowRange()) {
                                can = isCellRange(type) || isRow(type);
                            }
                        }
                    }
                }
            }
        }

        return can;
    }

    private static boolean isCellRange(final Class<?> type) {
        return SpreadsheetExpressionReference.class == type ||
            SpreadsheetCellReferenceOrRange.class == type ||
            SpreadsheetCellRangeReference.class == type;
    }

    private static boolean isColumn(final Class<?> type) {
        return SpreadsheetColumnReference.class == type ||
            SpreadsheetColumnReferenceOrRange.class == type ||
            SpreadsheetColumnRangeReference.class == type;
    }

    private static boolean isRow(final Class<?> type) {
        return SpreadsheetRowReference.class == type ||
            SpreadsheetRowReferenceOrRange.class == type ||
            SpreadsheetRowRangeReference.class == type;
    }

    @Override
    public SpreadsheetSelection tryConvertOrFail(final Object value,
                                                 final Class<?> type,
                                                 final SpreadsheetConverterContext context) {
        return SpreadsheetSelectionToSpreadsheetSelectionConverterSpreadsheetValueTypeVisitor.convert(
            context.resolveIfLabelOrFail(
                (SpreadsheetSelection) value
            ),
            type
        );
    }

    @Override
    public String toString() {
        return "selection to selection";
    }
}
