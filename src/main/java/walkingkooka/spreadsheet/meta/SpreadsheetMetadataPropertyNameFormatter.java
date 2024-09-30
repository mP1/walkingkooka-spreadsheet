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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;

import java.util.Locale;
import java.util.Optional;

/**
 * Base class for any property that holds a {@link SpreadsheetFormatterSelector}.
 */
abstract class SpreadsheetMetadataPropertyNameFormatter extends SpreadsheetMetadataPropertyName<SpreadsheetFormatterSelector> {

    /**
     * Package private to limit sub classing.
     */
    SpreadsheetMetadataPropertyNameFormatter(final String name,
                                             final SpreadsheetPatternKind spreadsheetPatternKind) {
        super(name);
        this.spreadsheetPatternKind = spreadsheetPatternKind;
    }

    // @see SpreadsheetMetadataPropertyName.patternKind
    final SpreadsheetPatternKind spreadsheetPatternKind;

    @Override
    final SpreadsheetFormatterSelector checkValue0(final Object value) {
        return this.checkValueType(
                value,
                v -> v instanceof SpreadsheetFormatterSelector
        );
    }

    @Override
    final String expected() {
        return SpreadsheetFormatterSelector.class.getSimpleName();
    }

    @Override
    final Optional<SpreadsheetFormatterSelector> extractLocaleAwareValue(final Locale locale) {
        return this.extractLocaleAwareValueSpreadsheetFormatPattern(locale)
                .map(SpreadsheetFormatPattern::spreadsheetFormatterSelector);
    }

    /**
     * For the given {@link Locale} return the default {@link SpreadsheetFormatPattern}, this will then be
     * converted into its {@link SpreadsheetFormatterSelector} equivalent.
     */
    abstract Optional<SpreadsheetFormatPattern> extractLocaleAwareValueSpreadsheetFormatPattern(final Locale locale);

    @Override
    final Class<SpreadsheetFormatterSelector> type() {
        return SpreadsheetFormatterSelector.class;
    }

    @Override
    final String compareToName() {
        return this.value();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public final SpreadsheetFormatterSelector parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetFormatterSelector.parse(value);
    }
}
