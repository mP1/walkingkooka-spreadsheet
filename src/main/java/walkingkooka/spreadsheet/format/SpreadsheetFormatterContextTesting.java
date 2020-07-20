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

import walkingkooka.Either;
import walkingkooka.color.Color;
import walkingkooka.convert.ConverterContextTesting;
import walkingkooka.text.CharSequences;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface SpreadsheetFormatterContextTesting<C extends SpreadsheetFormatterContext> extends ConverterContextTesting<C> {

    default void colorNumberAndCheck(final SpreadsheetFormatterContext context,
                                     final int number,
                                     final Optional<Color> color) {
        assertEquals(color,
                context.colorNumber(number),
                () -> "colorNumber " + number + " " + context);
    }

    default void colorNameAndCheck(final SpreadsheetFormatterContext context,
                                   final SpreadsheetColorName name,
                                   final Optional<Color> color) {
        assertEquals(color,
                context.colorName(name),
                () -> "colorName " + name + " " + context);
    }

    default <T> void canConvertAndCheck(final Object value,
                                        final Class<?> target,
                                        final boolean expected) {
        this.canConvertAndCheck(this.createContext(),
                value,
                target,
                expected);
    }

    default <T> void canConvertAndCheck(final SpreadsheetFormatterContext context,
                                        final Object value,
                                        final Class<?> target,
                                        final boolean expected) {
        assertEquals(expected,
                context.canConvert(value, target),
                () -> context + " canConvert " + CharSequences.quoteIfChars(value) + " target: " + target.getName());
    }

    default <T> void convertAndCheck(final Object value,
                                     final Class<T> target,
                                     final T expected) {
        this.convertAndCheck(this.createContext(),
                value,
                target,
                expected);
    }

    default <T> void convertAndCheck(final SpreadsheetFormatterContext context,
                                     final Object value,
                                     final Class<T> target,
                                     final T expected) {
        this.canConvertAndCheck(context, value, target, true);
        assertEquals(Either.left(expected),
                context.convert(value, target),
                () -> "convert " + CharSequences.quoteIfChars(value) + " target: " + target.getName());
    }

    default void defaultFormatTextAndCheck(final Object value,
                                           final Optional<SpreadsheetText> formattedText) {
        this.defaultFormatTextAndCheck(this.createContext(),
                value,
                formattedText);
    }

    default void defaultFormatTextAndCheck(final SpreadsheetFormatterContext context,
                                           final Object value,
                                           final Optional<SpreadsheetText> formattedText) {
        assertEquals(formattedText,
                context.defaultFormatText(value),
                () -> context + " " + CharSequences.quoteIfChars(value));
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetFormatterContext.class.getSimpleName();
    }
}
