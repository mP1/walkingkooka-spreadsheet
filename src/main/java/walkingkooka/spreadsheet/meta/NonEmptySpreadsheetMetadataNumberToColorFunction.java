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

import java.util.Map;
import java.util.Optional;

/**
 * A {@link java.util.function.Function} that uses a map using the number as a key.
 */
final class NonEmptySpreadsheetMetadataNumberToColorFunction extends SpreadsheetMetadataNumberToColorFunction {

    static NonEmptySpreadsheetMetadataNumberToColorFunction with(final Map<Integer, Color> numberToColor) {
        return new NonEmptySpreadsheetMetadataNumberToColorFunction(numberToColor);
    }

    private NonEmptySpreadsheetMetadataNumberToColorFunction(final Map<Integer, Color> numberToColor) {
        super();
        this.numberToColor = numberToColor;
    }

    @Override
    Optional<Color> apply0(final Integer number) {
        return Optional.ofNullable(this.numberToColor.get(number));
    }

    final Map<Integer, Color> numberToColor;

    @Override
    public String toString() {
        return this.numberToColor.toString();
    }
}
