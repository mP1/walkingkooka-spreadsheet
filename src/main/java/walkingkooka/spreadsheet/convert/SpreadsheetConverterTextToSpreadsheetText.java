
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
import walkingkooka.spreadsheet.format.SpreadsheetText;

/**
 * A {@link Converter} that converts an SpreadsheetText as a {@link String} into a {@link SpreadsheetText}.
 */
final class SpreadsheetConverterTextToSpreadsheetText extends SpreadsheetConverterTextTo {

    /**
     * Singleton
     */
    final static SpreadsheetConverterTextToSpreadsheetText INSTANCE = new SpreadsheetConverterTextToSpreadsheetText();

    private SpreadsheetConverterTextToSpreadsheetText() {
        super();
    }

    @Override
    boolean isType(final Object value,
                   final Class<?> type,
                   final SpreadsheetConverterContext context) {
        return SpreadsheetText.class == type;
    }

    @Override
    SpreadsheetText tryConvert(final String value,
                               final Class<?> type,
                               final SpreadsheetConverterContext context) {
        return SpreadsheetText.with(value);
    }

    @Override
    public String toString() {
        return "String to " + SpreadsheetText.class.getSimpleName();
    }
}
