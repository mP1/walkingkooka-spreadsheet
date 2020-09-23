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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateFormatPattern;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern extends SpreadsheetMetadataPropertyName<SpreadsheetDateFormatPattern> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetDateFormatPattern() {
        super("date-format-pattern");
    }

    @Override
    void checkValue0(final Object value) {
        this.checkValueType(value,
                v -> v instanceof SpreadsheetDateFormatPattern);
    }

    @Override
    String expected() {
        return "Date format pattern";
    }

    @Override
    void accept(final SpreadsheetDateFormatPattern value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitDateFormatPattern(value);
    }

    @Override
    Optional<SpreadsheetDateFormatPattern> extractLocaleValue(final Locale locale) {
        return Optional.empty(); // TODO fetch DateFormat FULL form
    }

    @Override
    Class<SpreadsheetDateFormatPattern> type() {
        return SpreadsheetDateFormatPattern.class;
    }
}
