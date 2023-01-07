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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePattern extends SpreadsheetMetadataPropertyName<SpreadsheetDateTimeParsePattern> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePattern instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePattern();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetDateTimeParsePattern() {
        super();
    }

    @Override
    SpreadsheetDateTimeParsePattern checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetDateTimeParsePattern);
    }

    @Override
    String expected() {
        return "DateTime parse pattern";
    }

    @Override
    void accept(final SpreadsheetDateTimeParsePattern value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitDateTimeParsePattern(value);
    }

    @Override
    Optional<SpreadsheetDateTimeParsePattern> extractLocaleValue(final Locale locale) {
        return Optional.of(
                SpreadsheetPattern.dateTimeParsePatternLocale(locale)
        );
    }

    @Override
    Class<SpreadsheetDateTimeParsePattern> type() {
        return SpreadsheetDateTimeParsePattern.class;
    }

    @Override
    String compareToName() {
        return this.value();
    }
}
