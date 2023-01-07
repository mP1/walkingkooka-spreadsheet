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

import walkingkooka.tree.text.TextStyle;

import java.util.Locale;
import java.util.Optional;

/**
 * A property that holds a {@link TextStyle} holding defaults such as the default column width.
 */
final class SpreadsheetMetadataPropertyNameStyle extends SpreadsheetMetadataPropertyName<TextStyle> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameStyle instance() {
        return new SpreadsheetMetadataPropertyNameStyle();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameStyle() {
        super();
    }

    @Override
    Class<TextStyle> type() {
        return TextStyle.class;
    }

    @Override
    TextStyle checkValue0(final Object value) {
        return this.checkValueType(value, v -> v instanceof TextStyle);
    }

    @Override
    String expected() {
        return "TextStyle";
    }

    @Override
    Optional<TextStyle> extractLocaleValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    void accept(final TextStyle value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitStyle(value);
    }

    @Override
    String compareToName() {
        return this.value();
    }
}
