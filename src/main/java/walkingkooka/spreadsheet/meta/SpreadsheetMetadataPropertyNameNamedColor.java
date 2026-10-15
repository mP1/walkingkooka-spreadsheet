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

import walkingkooka.color.Color;
import walkingkooka.color.IndexedColor;
import walkingkooka.color.NamedColor;
import walkingkooka.currency.CurrencyCodeLanguageTagContext;
import walkingkooka.currency.CurrencyLocaleContext;
import walkingkooka.naming.Name;
import walkingkooka.spreadsheet.color.SpreadsheetColors;

import java.util.Optional;

/**
 * The {@link Name} of metadata property, with a custom handler and visitor to handle dispatching to a {@link SpreadsheetMetadataVisitor} method.
 */
final class SpreadsheetMetadataPropertyNameNamedColor extends SpreadsheetMetadataPropertyName<IndexedColor> {

    /**
     * Factory used to create a new {@link SpreadsheetMetadataPropertyNameNamedColor} constant.
     */
    static SpreadsheetMetadataPropertyNameNamedColor withNamedColor(final NamedColor name) {
        return new SpreadsheetMetadataPropertyNameNamedColor(
            SpreadsheetColors.checkNamedColor(name)
        );
    }

    /**
     * Private constructor use factory.
     */
    private SpreadsheetMetadataPropertyNameNamedColor(final NamedColor name) {
        super(COLOR_PREFIX + name.value());
        this.name = name;
    }

    final NamedColor name;

    @Override
    IndexedColor checkValueNonNull(final Object value) {
        final IndexedColor indexedColor = this.checkValueType(
            value,
            v -> v instanceof IndexedColor
        );
        return SpreadsheetColors.checkIndexedColor(indexedColor);
    }

    @Override
    String expected() {
        return IndexedColor.class.getSimpleName();
    }

    @Override //
    Optional<IndexedColor> extractLocaleAwareValue(final CurrencyLocaleContext context) {
        return Optional.empty(); // colours are not Locale aware
    }

    @Override
    public Class<IndexedColor> type() {
        return IndexedColor.class;
    }

    // parseValueText...................................................................................................

    @Override
    IndexedColor parseValueTextNonNull(final String value,
                                       final CurrencyCodeLanguageTagContext context) {
        return Color.parseIndexed(value); // TODO should use Context
    }

    @Override
    void accept(final IndexedColor value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitNamedColor(
            this.name,
            value
        );
    }
}
