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

import walkingkooka.Cast;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

/**
 * A {@link Converter} that handles converting {@link String} into {@link SpreadsheetSelection} using the right
 * {@link SpreadsheetSelection} parseXXX method.
 */
final class SpreadsheetConverterTextToSpreadsheetSelection extends SpreadsheetConverterTextTo {

    final static SpreadsheetConverterTextToSpreadsheetSelection INSTANCE = new SpreadsheetConverterTextToSpreadsheetSelection();

    private SpreadsheetConverterTextToSpreadsheetSelection() {
        super();
    }

    @Override
    public boolean isTargetType(final Object value,
                                final Class<?> type,
                                final SpreadsheetConverterContext context) {
        return type == SpreadsheetCellReference.class ||
            type == SpreadsheetCellReferenceOrRange.class ||
            type == SpreadsheetCellRangeReference.class ||
            type == SpreadsheetColumnReference.class ||
            type == SpreadsheetColumnRangeReference.class ||
            type == SpreadsheetLabelName.class ||
            type == SpreadsheetRowReference.class ||
            type == SpreadsheetRowRangeReference.class ||
            type == SpreadsheetExpressionReference.class;
    }

    @Override
    public Object parseText(final String value,
                            final Class<?> type,
                            final SpreadsheetConverterContext context) {
        return SpreadsheetConverterTextToSpreadsheetSelectionSpreadsheetValueTypeVisitor.parse(
            value,
            Cast.to(type),
            context
        );
    }

    @Override
    public String toString() {
        return "String to Selection";
    }
}
