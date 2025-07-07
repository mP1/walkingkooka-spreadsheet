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
import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Wraps another {@link SpreadsheetPatternSpreadsheetFormatter} and adds a {@link Color} to any formatted result.
 */
final class SpreadsheetPatternSpreadsheetFormatterColor implements SpreadsheetPatternSpreadsheetFormatter {


    /**
     * Creates a {@link SpreadsheetPatternSpreadsheetFormatterColor}
     */
    static SpreadsheetPatternSpreadsheetFormatterColor with(final ColorSpreadsheetFormatParserToken token,
                                                            final SpreadsheetPatternSpreadsheetFormatter formatter) {
        Objects.requireNonNull(token, "token");
        Objects.requireNonNull(formatter, "formatter");

        return new SpreadsheetPatternSpreadsheetFormatterColor(token,
            formatter instanceof SpreadsheetPatternSpreadsheetFormatterColor ?
                unwrap(Cast.to(formatter)) :
                formatter);
    }

    private static SpreadsheetPatternSpreadsheetFormatter unwrap(final SpreadsheetPatternSpreadsheetFormatterColor formatter) {
        final SpreadsheetPatternSpreadsheetFormatter wrapped = formatter.formatter;
        return wrapped instanceof SpreadsheetPatternSpreadsheetFormatterColor ?
            unwrap(Cast.to(wrapped)) :
            wrapped;
    }

    /**
     * Private use factory
     */
    private SpreadsheetPatternSpreadsheetFormatterColor(final ColorSpreadsheetFormatParserToken token,
                                                        final SpreadsheetPatternSpreadsheetFormatter formatter) {
        super();

        this.token = token;

        final SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor visitor = SpreadsheetPatternSpreadsheetFormatterColorSpreadsheetFormatParserTokenVisitor.colorNameOrNumberOrFail(token);
        this.source = visitor.source;
        this.sourceValue = visitor.nameOrNumber;
        this.formatter = formatter;
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> value,
                                                           final SpreadsheetFormatterContext context) {
        return this.formatter.formatSpreadsheetText(
            value,
            context
        ).map(t -> t.setColor(this.color(context)));
    }

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");

        return SpreadsheetFormatterSelectorToken.tokens(this.token);
    }

    /**
     * The {@link SpreadsheetFormatter} that will have its color replaced if it was successful.
     */
    final SpreadsheetPatternSpreadsheetFormatter formatter;

    /**
     * Fetches the color to be added. While the color reference is static, the actual resolved {@link Color} is not.
     */
    private Optional<Color> color(final SpreadsheetFormatterContext context) {
        return this.source.resolve(this.sourceValue, context);
    }

    /**
     * Either the color index (int) or color name (String)
     */
    private final SpreadsheetPatternSpreadsheetFormatterColorColorSource source;

    /**
     * Either an int or name.
     */
    private final Object sourceValue;

    // Object...........................................................................................................

    @Override
    public int hashCode() {
        return this.token.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
            other instanceof SpreadsheetPatternSpreadsheetFormatterColor && this.equals0((SpreadsheetPatternSpreadsheetFormatterColor) other);
    }

    private boolean equals0(final SpreadsheetPatternSpreadsheetFormatterColor other) {
        return this.sourceValue.equals(other.sourceValue) &&
            this.formatter.equals(other.formatter);
    }

    @Override
    public String toString() {
        return this.token.text() + " " + this.formatter;
    }

    private final ColorSpreadsheetFormatParserToken token;
}
