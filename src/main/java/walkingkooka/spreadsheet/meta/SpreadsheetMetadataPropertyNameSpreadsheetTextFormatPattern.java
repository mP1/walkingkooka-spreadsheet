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

package walkingkooka.spreadsheet.meta;

import walkingkooka.spreadsheet.format.pattern.SpreadsheetTextFormatPattern;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetTextFormatPattern extends SpreadsheetMetadataPropertyName<SpreadsheetTextFormatPattern> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameSpreadsheetTextFormatPattern instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetTextFormatPattern();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetTextFormatPattern() {
        super("text-format-pattern");
    }

    @Override
    void checkValue0(final Object value) {
        this.checkValueType(value,
                v -> v instanceof SpreadsheetTextFormatPattern);
    }

    @Override
    String expected() {
        return "Text format pattern";
    }

    @Override
    void accept(final SpreadsheetTextFormatPattern value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitTextFormatPattern(value);
    }

    @Override
    Optional<SpreadsheetTextFormatPattern> extractLocaleValue(final Locale locale) {
        return Optional.empty(); // Not locale aware.
    }

    @Override
    Class<SpreadsheetTextFormatPattern> type() {
        return SpreadsheetTextFormatPattern.class;
    }
}
