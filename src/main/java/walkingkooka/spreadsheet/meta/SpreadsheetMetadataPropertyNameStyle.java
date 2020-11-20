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

import walkingkooka.collect.set.Sets;
import walkingkooka.tree.text.TextStyle;
import walkingkooka.tree.text.TextStylePropertyName;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * A property that holds a {@link TextStyle} holding defaults such as the default column width.
 */
final class SpreadsheetMetadataPropertyNameStyle extends SpreadsheetMetadataPropertyName<TextStyle> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameStyle instance() {
        return new SpreadsheetMetadataPropertyNameStyle();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameStyle() {
        super("style");
    }

    @Override
    Class<TextStyle> type() {
        return TextStyle.class;
    }

    @Override
    TextStyle checkValue0(final Object value) {
        final TextStyle style = this.checkValueType(value, v -> v instanceof TextStyle);

        final Set<TextStylePropertyName<?>> missing = Sets.ordered();
        for (final TextStylePropertyName<?> required : REQUIRED) {
            if (false == style.get(required).isPresent()) {
                missing.add(required);
            }
        }

        if (false == missing.isEmpty()) {
            throw new SpreadsheetMetadataPropertyValueException("Missing required properties " + missing,
                    this,
                    value);
        }
        return style;
    }

    private final static TextStylePropertyName[] REQUIRED = new TextStylePropertyName[] {
            TextStylePropertyName.FONT_FAMILY_NAME,
            TextStylePropertyName.FONT_SIZE,
            TextStylePropertyName.HEIGHT,
            TextStylePropertyName.WIDTH
    };

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
}
