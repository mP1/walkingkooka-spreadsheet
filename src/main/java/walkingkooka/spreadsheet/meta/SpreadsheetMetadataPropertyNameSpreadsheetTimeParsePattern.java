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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePattern;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePattern extends SpreadsheetMetadataPropertyName<SpreadsheetTimeParsePattern> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePattern instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePattern();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePattern() {
        super();
    }

    @Override
    SpreadsheetTimeParsePattern checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetTimeParsePattern);
    }

    @Override
    String expected() {
        return "Time parse pattern";
    }

    @Override
    void accept(final SpreadsheetTimeParsePattern value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitTimeParsePattern(value);
    }

    Optional<SpreadsheetTimeParsePattern> extractLocaleValue(final Locale locale) {
        return Optional.of(
                SpreadsheetPattern.timeParsePatternLocale(locale)
        );
    }

    @Override
    Class<SpreadsheetTimeParsePattern> type() {
        return SpreadsheetTimeParsePattern.class;
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
    public SpreadsheetTimeParsePattern parseValue0(final String value) {
        return SpreadsheetPattern.parseTimeParsePattern(value);
    }
}
