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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeFormatPattern;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern extends SpreadsheetMetadataPropertyName<SpreadsheetTimeFormatPattern> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetTimeFormatPattern() {
        super("time-format-pattern");
    }

    @Override
    void checkValue0(final Object value) {
        this.checkValueType(value,
                v -> v instanceof SpreadsheetTimeFormatPattern);
    }

    @Override
    String expected() {
        return "Time format pattern";
    }

    @Override
    void accept(final SpreadsheetTimeFormatPattern value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitTimeFormatPattern(value);
    }

    @Override
    Optional<SpreadsheetTimeFormatPattern> extractLocaleValue(final Locale locale) {
        return this.extractLocaleSimpleDateFormat(locale,
                (l) -> DateFormat.getTimeInstance(DateFormat.FULL, l),
                SpreadsheetDateFormatPattern::parseTimeFormatPattern);
    }

    @Override
    Class<SpreadsheetTimeFormatPattern> type() {
        return SpreadsheetTimeFormatPattern.class;
    }
}
