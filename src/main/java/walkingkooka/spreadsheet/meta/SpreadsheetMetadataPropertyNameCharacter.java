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
import walkingkooka.text.CharSequences;

import java.text.DecimalFormatSymbols;
import java.util.Optional;

/**
 * A property that requires a character that is not a control character, whitespace, letter or digit.
 */
abstract class SpreadsheetMetadataPropertyNameCharacter extends SpreadsheetMetadataPropertyName<Character> {

    /**
     * Package private to limit subclassing.
     */
    SpreadsheetMetadataPropertyNameCharacter(final String name) {
        super(name);
    }

    @Override final Character checkValueNonNull(final Object value) {
        final Character c = this.checkValueType(value, v -> v instanceof Character);
        if (c < 0x20 || Character.isWhitespace(c) || Character.isLetter(c) || Character.isDigit(c)) {
            throw this.spreadsheetMetadataPropertyValueException(value);
        }
        return c;
    }

    @Override final String expected() {
        return Character.class.getSimpleName() + " symbol, not control character, whitespace, letter or digit";
    }

    @Override final Optional<Character> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.of(
            this.extractLocaleValueCharacter(
                DecimalFormatSymbols.getInstance(
                    context.locale()
                )
            )
        );
    }

    /**
     * Template method that supports subclassing retrieving a single property parse the {@link DecimalFormatSymbols}
     */
    abstract Character extractLocaleValueCharacter(final DecimalFormatSymbols symbols);

    @Override
    public Class<Character> type() {
        return Character.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override final Character parseUrlFragmentSaveValueNonNull(final String value) {
        if (value.length() != 1) {
            throw new IllegalArgumentException("Invalid value " + CharSequences.quoteAndEscape(value) + " expected a single character");
        }

        return value.charAt(0);
    }
}
