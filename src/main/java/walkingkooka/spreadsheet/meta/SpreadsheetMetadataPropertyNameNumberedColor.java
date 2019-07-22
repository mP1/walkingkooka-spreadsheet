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
import walkingkooka.naming.Name;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * The {@link Name} of metadata property for numbered colors.
 */
final class SpreadsheetMetadataPropertyNameNumberedColor extends SpreadsheetMetadataPropertyName<Color> {

    /**
     * Retrieves a {@link SpreadsheetMetadataPropertyNameNumberedColor} for a numbered {@link Color}.
     */
    static SpreadsheetMetadataPropertyNameNumberedColor color0(final int number) {
        SpreadsheetMetadata.checkColorNumber(number);

        return number < MAX_NUMBER ?
                NUMBER_TO_COLOR[number] :
                new SpreadsheetMetadataPropertyNameNumberedColor(number);
    }

    final static int MAX_NUMBER = 32;

    /**
     * Cache of 0 to {@link #MAX_NUMBER} names.
     */
    private final static SpreadsheetMetadataPropertyNameNumberedColor[] NUMBER_TO_COLOR = new SpreadsheetMetadataPropertyNameNumberedColor[MAX_NUMBER];

    /**
     * Fills the cache of {@link SpreadsheetMetadataPropertyNameNumberedColor} for color numbers 0 to {@link #MAX_NUMBER}.
     */
    static {
        IntStream.range(0, MAX_NUMBER)
                .forEach(SpreadsheetMetadataPropertyNameNumberedColor::registerColor);
    }

    private static void registerColor(final int i) {
        final SpreadsheetMetadataPropertyNameNumberedColor name = new SpreadsheetMetadataPropertyNameNumberedColor(i);
        NUMBER_TO_COLOR[i] = name;
        CONSTANTS.put(name.value(), name);
    }

    /**
     * Private constructor use factory.
     */
    private SpreadsheetMetadataPropertyNameNumberedColor(final int number) {
        super(COLOR_PREFIX + number);
        this.number = number;
    }

    final int number;

    @Override
    SpreadsheetMetadataPropertyValueHandler<Color> handler() {
        return SpreadsheetMetadataPropertyValueHandler.color();
    }

    // NonEmptySpreadsheetMetadata......................................................................................

    @Override
    void addNumberedColor(final Object value, final Map<Integer, Color> numberToColor) {
        numberToColor.put(this.number, Color.class.cast(value));
    }

    // SpreadsheetMetadataVisitor.......................................................................................

    @Override
    void accept(final Object value, final SpreadsheetMetadataVisitor visitor) {
        visitor.visitNumberedColor(this.number, Color.class.cast(value));
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetMetadataPropertyNameNumberedColor;
    }
}
