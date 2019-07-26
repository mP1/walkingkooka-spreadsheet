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

import walkingkooka.Cast;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorParserToken;

import java.util.Objects;
import java.util.Optional;

/**
 * Adds a {@link Color} to a value formatted by another {@link SpreadsheetTextFormatter}.
 */
final class ColorSpreadsheetTextFormatter extends SpreadsheetTextFormatter3<SpreadsheetFormatColorParserToken> {


    /**
     * Creates a {@link ColorSpreadsheetTextFormatter}
     */
    static ColorSpreadsheetTextFormatter with(final SpreadsheetFormatColorParserToken token,
                                              final SpreadsheetTextFormatter formatter) {
        check(token);
        Objects.requireNonNull(formatter, "formatter");

        return new ColorSpreadsheetTextFormatter(token,
                formatter instanceof ColorSpreadsheetTextFormatter ?
                        unwrap(Cast.to(formatter)) :
                        formatter);
    }

    private static SpreadsheetTextFormatter unwrap(final ColorSpreadsheetTextFormatter formatter) {
        final SpreadsheetTextFormatter wrapped = formatter.formatter;
        return wrapped instanceof ColorSpreadsheetTextFormatter ?
                unwrap(Cast.to(wrapped)) :
                wrapped;
    }

    /**
     * Private use factory
     */
    private ColorSpreadsheetTextFormatter(final SpreadsheetFormatColorParserToken token,
                                          final SpreadsheetTextFormatter formatter) {
        super(token);

        final ColorSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor visitor = ColorSpreadsheetTextFormatterSpreadsheetFormatParserTokenVisitor.colorNameOrNumberOrFail(token);
        this.source = visitor.source;
        this.sourceValue = visitor.numberOfName;
        this.formatter = formatter;
    }

    @Override
    public boolean canFormat(final Object value) {
        return this.formatter.canFormat(value);
    }

    @Override
    Optional<SpreadsheetFormattedText> format0(final Object value, final SpreadsheetTextFormatContext context) {
        return this.formatter.format(value, context)
                .map(t -> t.setColor(this.color(context)));
    }

    /**
     * The {@link SpreadsheetTextFormatter} that will have its color replaced if it was successful.
     */
    final SpreadsheetTextFormatter formatter;

    /**
     * Fetches the color to be added. While the color reference is static, the actual resolved {@link Color} is not.
     */
    private Optional<Color> color(final SpreadsheetTextFormatContext context) {
        return this.source.resolve(this.sourceValue, context);
    }

    /**
     * Either the color index (int) or color name (String)
     */
    private final ColorSpreadsheetTextFormatterColorSource source;

    /**
     * Either an int or name.
     */
    private final Object sourceValue;

    @Override
    final String toStringSuffix() {
        return " " + this.formatter;
    }
}
