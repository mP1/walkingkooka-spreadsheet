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

package walkingkooka.spreadsheet.format;

import walkingkooka.color.Color;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.math.DecimalNumberContext;

import java.util.Optional;

/**
 * Context that accompanies a value format, holding local sensitive attributes such as the decimal point character.
 */
public interface SpreadsheetFormatterContext extends DecimalNumberContext, DateTimeContext {

    /**
     * Returns the {@link Color} with the given number.
     */
    Optional<Color> colorNumber(final int number);

    /**
     * Returns the {@link Color} with the given name.
     */
    Optional<Color> colorName(final SpreadsheetColorName name);

    /**
     * The width of the "cell" in characters.
     * This value affects STAR operator.
     */
    int width();

    /**
     * Provides a default format text.
     */
    Optional<SpreadsheetText> defaultFormatText(final Object value);

    /**
     * Handles converting the given value to the target.
     */
    <T> T convert(final Object value, final Class<T> target);
}
