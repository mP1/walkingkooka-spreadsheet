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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeFormatPattern;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern extends SpreadsheetMetadataPropertyName<SpreadsheetDateTimeFormatPattern> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern INSTANCE = new SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern();

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern() {
        super("date-time-format-pattern");
    }

    @Override
    void checkValue0(final Object value) {
        this.checkValueType(value,
                v -> v instanceof SpreadsheetDateTimeFormatPattern);
    }

    @Override
    String expected() {
        return "DateTime format pattern";
    }

    @Override
    void accept(final SpreadsheetDateTimeFormatPattern value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitDateTimeFormatPattern(value);
    }

    @Override
    Optional<SpreadsheetDateTimeFormatPattern> extractLocaleValue(final Locale locale) {
        return Optional.empty(); // TODO select DateTimeFormatter LONG/FULL pattern
    }

    @Override
    Class<SpreadsheetDateTimeFormatPattern> type() {
        return SpreadsheetDateTimeFormatPattern.class;
    }
}
