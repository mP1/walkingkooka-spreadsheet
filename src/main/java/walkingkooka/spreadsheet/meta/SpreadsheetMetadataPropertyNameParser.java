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

import walkingkooka.spreadsheet.format.SpreadsheetParserSelector;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;

import java.util.Locale;
import java.util.Optional;

/**
 * Base class for any property that holds a {@link SpreadsheetParserSelector}.
 */
abstract class SpreadsheetMetadataPropertyNameParser extends SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetMetadataPropertyNameParser(final String name,
                                          final SpreadsheetPatternKind spreadsheetPatternKind) {
        super(name);
        this.spreadsheetPatternKind = spreadsheetPatternKind;
    }

    // @see SpreadsheetMetadataPropertyName.patternKind
    final SpreadsheetPatternKind spreadsheetPatternKind;

    @Override
    final SpreadsheetParserSelector checkValue0(final Object value) {
        return this.checkValueType(
                value,
                v -> v instanceof SpreadsheetParserSelector
        );
    }

    @Override
    final String expected() {
        return SpreadsheetParserSelector.class.getSimpleName();
    }

    @Override
    final Optional<SpreadsheetParserSelector> extractLocaleAwareValue(final Locale locale) {
        return this.extractLocaleAwareValueSpreadsheetParsePattern(locale)
                .map(SpreadsheetParsePattern::spreadsheetParserSelector);
    }

    /**
     * For the given {@link Locale} return the default {@link SpreadsheetParsePattern}, this will then be
     * converted into its {@link SpreadsheetParserSelector} equivalent.
     */
    abstract Optional<SpreadsheetParsePattern> extractLocaleAwareValueSpreadsheetParsePattern(final Locale locale);

    @Override
    final Class<SpreadsheetParserSelector> type() {
        return SpreadsheetParserSelector.class;
    }

    @Override
    final String compareToName() {
        return this.value();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public final boolean isParseUrlFragmentSaveValueSupported() {
        return true;
    }

    @Override
    public final SpreadsheetParserSelector parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetParserSelector.parse(value);
    }
}
