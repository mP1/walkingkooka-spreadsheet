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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern extends SpreadsheetMetadataPropertyName<SpreadsheetDateTimeFormatPattern> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern() {
        super();
    }

    @Override
    SpreadsheetDateTimeFormatPattern checkValue0(final Object value) {
        return this.checkValueType(value,
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

    Optional<SpreadsheetDateTimeFormatPattern> extractLocaleValue(final Locale locale) {
        return Optional.of(
                SpreadsheetPattern.dateTimeFormatPatternLocale(locale)
        );
    }

    @Override
    Class<SpreadsheetDateTimeFormatPattern> type() {
        return SpreadsheetDateTimeFormatPattern.class;
    }

    @Override
    String compareToName() {
        return this.value();
    }

    // parseValue.......................................................................................................

    @Override
    public boolean isParseValueSupported() {
        return true;
    }

    @Override
    public SpreadsheetDateTimeFormatPattern parseValue0(final String value) {
        return SpreadsheetPattern.parseDateTimeFormatPattern(value);
    }
}
