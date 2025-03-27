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
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;
import walkingkooka.tree.text.TextNode;
import walkingkooka.tree.text.TextStyle;

/**
 * A {@link Converter} that supports converting a {@link SpreadsheetCell} to various target types, where each type maps
 * to a property within a {@link SpreadsheetCell}, such as {@link TextStyle} extracting the {@link SpreadsheetCell#style()}.
 */
final class SpreadsheetConverterSpreadsheetCell extends SpreadsheetConverter {

    /**
     * Singleton
     */
    final static SpreadsheetConverterSpreadsheetCell INSTANCE = new SpreadsheetConverterSpreadsheetCell();

    private SpreadsheetConverterSpreadsheetCell() {
    }

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type,
                              final SpreadsheetConverterContext context) {
        return value instanceof SpreadsheetCell && isClass(type);
    }

    private boolean isClass(final Class<?> type) {
        return SpreadsheetCell.class == type ||
                String.class == type || // formula.text
                Object.class == type || // formula.value
                SpreadsheetFormatterSelector.class == type || // formatter format-pattern etc.
                SpreadsheetParserSelector.class == type || // parser parse-pattern etc
                TextStyle.class == type || // style
                TextNode.class == type; // formatted value
    }

    @Override
    <T> Either<T, String> convert0(final Object value,
                                   final Class<T> type,
                                   final SpreadsheetConverterContext context) {
        return this.convertSpreadsheetCell(
                (SpreadsheetCell) value,
                type,
                context
        );
    }

    // maybe add support for picking errors or individual formatted value types (problem is String already selects formula.text.
    private <T> Either<T, String> convertSpreadsheetCell(final SpreadsheetCell cell,
                                                         final Class<T> type,
                                                         final SpreadsheetConverterContext context) {
        Object value;

        if (SpreadsheetCell.class == type) {
            value = cell;
        } else {
            if (String.class == type) {
                value = cell.formula().text();
            } else {
                if (Object.class == type) {
                    value = cell.formula()
                            .value()
                            .orElse(null);
                } else {
                    if (SpreadsheetFormatterSelector.class == type) {
                        value = cell.formatter()
                                .orElse(null);
                    } else {
                        if (SpreadsheetParserSelector.class == type) {
                            value = cell.parser()
                                    .orElse(null);
                        } else {
                            if (TextStyle.class == type) {
                                value = cell.style();
                            } else {
                                if (TextNode.class == type) {
                                    value = cell.formattedValue()
                                            .orElse(null);
                                } else {
                                    throw new IllegalArgumentException("Unexpected target type " + type.getName());
                                }
                            }
                        }
                    }
                }
            }
        }

        return this.successfulConversion(
                value,
                type
        );
    }

    @Override
    public String toString() {
        return SpreadsheetCell.class.getSimpleName() + " to *";
    }
}
