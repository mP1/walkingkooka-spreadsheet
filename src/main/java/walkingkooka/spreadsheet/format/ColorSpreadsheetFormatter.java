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

import java.util.Optional;

/**
 * Adds a {@link Color} to a value formatted by another {@link SpreadsheetFormatter}.
 */
final class ColorSpreadsheetFormatter extends SpreadsheetPatternSpreadsheetFormatter<SpreadsheetFormatColorParserToken> {


    /**
     * Creates a {@link ColorSpreadsheetFormatter}
     */
    static ColorSpreadsheetFormatter with(final SpreadsheetFormatColorParserToken token,
                                          final SpreadsheetFormatter formatter) {
        checkParserToken(token);
        checkFormatter(formatter);

        return new ColorSpreadsheetFormatter(token,
                formatter instanceof ColorSpreadsheetFormatter ?
                        unwrap(Cast.to(formatter)) :
                        formatter);
    }

    private static SpreadsheetFormatter unwrap(final ColorSpreadsheetFormatter formatter) {
        final SpreadsheetFormatter wrapped = formatter.formatter;
        return wrapped instanceof ColorSpreadsheetFormatter ?
                unwrap(Cast.to(wrapped)) :
                wrapped;
    }

    /**
     * Private use factory
     */
    private ColorSpreadsheetFormatter(final SpreadsheetFormatColorParserToken token,
                                      final SpreadsheetFormatter formatter) {
        super(token);

        final ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor visitor = ColorSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor.colorNameOrNumberOrFail(token);
        this.source = visitor.source;
        this.sourceValue = visitor.nameOrNumber;
        this.formatter = formatter;
    }

    @Override
    public boolean canFormat(final Object value,
                             final SpreadsheetFormatterContext context) {
        return this.formatter.canFormat(value, context);
    }

    @Override
    Optional<SpreadsheetText> format0(final Object value,
                                      final SpreadsheetFormatterContext context) {
        return this.formatter.format(value, context)
                .map(t -> t.setColor(this.color(context)));
    }

    /**
     * The {@link SpreadsheetFormatter} that will have its color replaced if it was successful.
     */
    final SpreadsheetFormatter formatter;

    /**
     * Fetches the color to be added. While the color reference is static, the actual resolved {@link Color} is not.
     */
    private Optional<Color> color(final SpreadsheetFormatterContext context) {
        return this.source.resolve(this.sourceValue, context);
    }

    /**
     * Either the color index (int) or color name (String)
     */
    private final ColorSpreadsheetFormatterColorSource source;

    /**
     * Either an int or name.
     */
    private final Object sourceValue;

    @Override
    String toStringSuffix() {
        return " " + this.formatter;
    }
}
