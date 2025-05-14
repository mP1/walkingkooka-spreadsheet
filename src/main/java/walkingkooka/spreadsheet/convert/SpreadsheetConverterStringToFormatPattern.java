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
import walkingkooka.text.CharSequences;

/**
 * A {@link Converter} that only selects converting values to a {@link String} using the given pattern
 * to create the appropriate {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern}.
 * <br>
 * This {@link Converter} exists to support functions like text where the user provides a value and a pattern.
 */
final class SpreadsheetConverterStringToFormatPattern extends SpreadsheetConverterStringTo {

    static SpreadsheetConverterStringToFormatPattern with(final String pattern) {
        return new SpreadsheetConverterStringToFormatPattern(
                CharSequences.failIfNullOrEmpty(pattern, "pattern")
        );
    }

    private SpreadsheetConverterStringToFormatPattern(final String pattern) {
        this.pattern = pattern;
    }

    @Override
    boolean isType(final Object value,
                   final Class<?> type,
                   final SpreadsheetConverterContext context) {
        return String.class == type || context.canConvert(
                value,
                type
        );
    }


    @Override
    String tryConvert(final Object value,
                      final Class<?> type,
                      final SpreadsheetConverterContext context) {
        return SpreadsheetConverterStringToFormatPatternSpreadsheetValueVisitor.format(
                value,
                this.pattern,
                context
        );
    }

    private final String pattern;

    @Override
    public String toString() {
        return this.pattern;
    }
}
