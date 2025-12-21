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
import walkingkooka.locale.LocaleContext;
import walkingkooka.naming.Name;
import walkingkooka.spreadsheet.color.SpreadsheetColors;
import walkingkooka.text.CharSequences;

import java.util.Optional;
import java.util.stream.IntStream;

/**
 * The {@link Name} of metadata property for numbered colors.
 */
final class SpreadsheetMetadataPropertyNameNumberedColor extends SpreadsheetMetadataPropertyName<Color> {

    /**
     * Retrieves a {@link SpreadsheetMetadataPropertyNameNumberedColor} for a numbered {@link Color}.
     */
    static SpreadsheetMetadataPropertyNameNumberedColor withNumber(final int number) {
        SpreadsheetColors.checkNumber(number);

        return NUMBER_TO_COLOR[number - SpreadsheetColors.MIN];
    }

    /**
     * Cache of {@link SpreadsheetColors#MIN} to {@link SpreadsheetColors#MAX} values.
     */
    private static final SpreadsheetMetadataPropertyNameNumberedColor[] NUMBER_TO_COLOR = new SpreadsheetMetadataPropertyNameNumberedColor[
        SpreadsheetColors.MAX -
            SpreadsheetColors.MIN
            + 1
        ];

    /*
     * Fills the cache of {@link SpreadsheetMetadataPropertyNameNumberedColor} for color numbers 0 to {@link #SpreadsheetColors.MAX}.
     */
    static {
        IntStream.range(
            SpreadsheetColors.MIN,
            1 + SpreadsheetColors.MAX
        ).forEach(SpreadsheetMetadataPropertyNameNumberedColor::registerColor);
    }

    private static void registerColor(final int i) {
        final SpreadsheetMetadataPropertyNameNumberedColor name = new SpreadsheetMetadataPropertyNameNumberedColor(i);
        NUMBER_TO_COLOR[i - SpreadsheetColors.MIN] = name;
        CONSTANTS.put(name.value(), name);
    }

    /**
     * Private constructor use factory.
     */
    private SpreadsheetMetadataPropertyNameNumberedColor(final int number) {
        super(COLOR_PREFIX + number);
        this.number = number;
        this.compareToValue = COLOR_PREFIX + CharSequences.padLeft(String.valueOf(number), 5, '0');
    }

    final int number;

    @Override
    Color checkValueNonNull(final Object value) {
        return this.checkValueType(
            value,
            v -> v instanceof Color
        );
    }

    @Override
    String expected() {
        return Color.class.getSimpleName();
    }

    @Override
    Optional<Color> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty(); // colours are not Locale aware
    }

    @Override
    public Class<Color> type() {
        return Color.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    Color parseUrlFragmentSaveValueNonNull(final String value) {
        return Color.parse(value);
    }

    // SpreadsheetMetadataVisitor.......................................................................................

    @Override
    void accept(final Color value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitNumberedColor(this.number, value);
    }

    final String compareToValue;
}
