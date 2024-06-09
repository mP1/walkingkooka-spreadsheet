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

import walkingkooka.spreadsheet.format.SpreadsheetFormatterSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;

import java.util.Locale;
import java.util.Optional;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds the default formatter for {@link java.time.LocalTime} values.
 */
final class SpreadsheetMetadataPropertyNameFormatterTime extends SpreadsheetMetadataPropertyNameFormatter {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameFormatterTime instance() {
        return new SpreadsheetMetadataPropertyNameFormatterTime();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameFormatterTime() {
        super(
                "time-formatter",
                SpreadsheetPatternKind.TIME_FORMAT_PATTERN
        );
    }

    @Override
    void accept(final SpreadsheetFormatterSelector value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitTimeFormatter(value);
    }

    @Override
    Optional<SpreadsheetFormatPattern> extractLocaleAwareValueSpreadsheetFormatPattern(final Locale locale) {
        return Optional.of(
                SpreadsheetPattern.timeFormatPatternLocale(locale)
        );
    }
}
