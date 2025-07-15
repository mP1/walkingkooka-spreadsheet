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

import org.junit.jupiter.api.Test;
import walkingkooka.color.Color;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContextTesting;
import walkingkooka.tree.text.TextNode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetFormatterContextTesting<C extends SpreadsheetFormatterContext> extends ExpressionNumberConverterContextTesting<C> {

    @Override
    default C createCanConvert() {
        return this.createContext();
    }

    default void colorNumberAndCheck(final SpreadsheetFormatterContext context,
                                     final int number,
                                     final Optional<Color> color) {
        this.checkEquals(color,
            context.colorNumber(number),
            () -> "colorNumber " + number + " " + context);
    }

    default void colorNameAndCheck(final SpreadsheetFormatterContext context,
                                   final SpreadsheetColorName name,
                                   final Optional<Color> color) {
        this.checkEquals(color,
            context.colorName(name),
            () -> "colorName " + name + " " + context);
    }

    default void formatValueAndCheck(final Object value,
                                     final SpreadsheetText expected) {
        this.formatValueAndCheck(
            Optional.of(value),
            expected
        );
    }

    default void formatValueAndCheck(final Optional<Object> value,
                                     final SpreadsheetText expected) {
        this.formatValueAndCheck(
            value,
            expected.toTextNode()
        );
    }

    default void formatValueAndCheck(final Optional<Object> value,
                                     final TextNode expected) {
        this.formatValueAndCheck(
            value,
            Optional.of(expected)
        );
    }

    default void formatValueAndCheck(final Optional<Object> value,
                                     final Optional<TextNode> expected) {
        this.formatValueAndCheck(
            this.createContext(),
            value,
            expected
        );
    }

    default void formatValueAndCheck(final SpreadsheetFormatterContext context,
                                     final Optional<Object> value,
                                     final Optional<TextNode> expected) {
        this.checkEquals(
            expected,
            context.formatValue(value),
            () -> context + " " + CharSequences.quoteIfChars(value)
        );
    }

    @Test
    default void testSpreadsheetExpressionEvaluationContextWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createContext()
                .spreadsheetExpressionEvaluationContext(null)
        );
    }

    @Override
    default String typeNameSuffix() {
        return SpreadsheetFormatterContext.class.getSimpleName();
    }
}
