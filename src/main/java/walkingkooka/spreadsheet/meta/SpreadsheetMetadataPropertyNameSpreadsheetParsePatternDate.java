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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;

import java.util.Locale;
import java.util.Optional;

/**
 * A property that holds the default {@link SpreadsheetDateParsePattern}.
 */
final class SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate extends SpreadsheetMetadataPropertyNameSpreadsheetParsePattern<SpreadsheetDateParsePattern> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate() {
        super("date-parse-pattern");
    }

    @Override
    SpreadsheetDateParsePattern checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetDateParsePattern);
    }

    @Override
    String expected() {
        return "Date parse pattern";
    }

    @Override
    void accept(final SpreadsheetDateParsePattern value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitDateParsePattern(value);
    }

    @Override
    Optional<SpreadsheetDateParsePattern> extractLocaleAwareValue(final Locale locale) {
        return Optional.of(
                SpreadsheetPattern.dateParsePatternLocale(locale)
        );
    }

    @Override
    Class<SpreadsheetDateParsePattern> type() {
        return SpreadsheetDateParsePattern.class;
    }

    @Override
    public SpreadsheetDateParsePattern parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetPattern.parseDateParsePattern(value);
    }
}
