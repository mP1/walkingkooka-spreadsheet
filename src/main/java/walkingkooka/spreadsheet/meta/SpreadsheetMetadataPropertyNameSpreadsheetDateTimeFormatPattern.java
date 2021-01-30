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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateTimeFormatPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern extends SpreadsheetMetadataPropertyName<SpreadsheetDateTimeFormatPattern> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern() {
        super("date-time-format-pattern");
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

    @Override
    Optional<SpreadsheetDateTimeFormatPattern> extractLocaleValue(final Locale locale) {
        return this.extractLocaleSimpleDateFormat(locale,
                (l) -> DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, l),
                SpreadsheetMetadataPropertyNameSpreadsheetDateTimeFormatPattern::parseDateTimeFormatPattern);
    }

    private static SpreadsheetDateTimeFormatPattern parseDateTimeFormatPattern(final String text) {
        final SpreadsheetDateTimeFormatPattern pattern = SpreadsheetPattern.parseDateTimeFormatPattern(text);

        return SpreadsheetPattern.dateTimeFormatPattern(
                SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatternsSpreadsheetFormatParserTokenVisitor.fix(
                        pattern.value(),
                        SpreadsheetFormatParserToken::dateTime
                )
        );
    }

    @Override
    Class<SpreadsheetDateTimeFormatPattern> type() {
        return SpreadsheetDateTimeFormatPattern.class;
    }
}
