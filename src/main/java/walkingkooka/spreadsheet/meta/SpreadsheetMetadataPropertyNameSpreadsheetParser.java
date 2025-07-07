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

import walkingkooka.locale.LocaleContext;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.spreadsheet.parser.SpreadsheetParserSelector;

import java.util.Locale;
import java.util.Optional;

/**
 * Base class for any property that holds a {@link SpreadsheetParserSelector}.
 */
abstract class SpreadsheetMetadataPropertyNameSpreadsheetParser extends SpreadsheetMetadataPropertyName<SpreadsheetParserSelector> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameSpreadsheetParser(final String name,
                                                     final SpreadsheetPatternKind spreadsheetPatternKind) {
        super(name);
        this.spreadsheetPatternKind = spreadsheetPatternKind;
    }

    // @see SpreadsheetMetadataPropertyName.patternKind
    final SpreadsheetPatternKind spreadsheetPatternKind;

    @Override final SpreadsheetParserSelector checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof SpreadsheetParserSelector
        );
    }

    @Override final String expected() {
        return SpreadsheetParserSelector.class.getSimpleName();
    }

    @Override final Optional<SpreadsheetParserSelector> extractLocaleAwareValue(final LocaleContext context) {
        return this.extractLocaleAwareValueSpreadsheetParsePattern(
            context.locale()
        ).map(SpreadsheetParsePattern::spreadsheetParserSelector);
    }

    /**
     * For the given {@link Locale} return the default {@link SpreadsheetParsePattern}, this will then be
     * converted into its {@link SpreadsheetParserSelector} equivalent.
     */
    abstract Optional<SpreadsheetParsePattern> extractLocaleAwareValueSpreadsheetParsePattern(final Locale locale);

    @Override
    public final Class<SpreadsheetParserSelector> type() {
        return SpreadsheetParserSelector.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override final SpreadsheetParserSelector parseUrlFragmentSaveValueNonNull(final String value) {
        return SpreadsheetParserSelector.parse(value);
    }
}
