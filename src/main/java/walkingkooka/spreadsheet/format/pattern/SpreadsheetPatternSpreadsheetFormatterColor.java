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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.Cast;
import walkingkooka.color.Color;
import walkingkooka.spreadsheet.format.SpreadsheetFormatter;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterContext;
import walkingkooka.spreadsheet.format.SpreadsheetText;
import walkingkooka.spreadsheet.format.parser.ColorSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;

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
        this.color = visitor.color;
        this.formatter = formatter;
    }

    @Override
    public Optional<SpreadsheetText> formatSpreadsheetText(final Optional<Object> value,
                                                           final SpreadsheetFormatterContext context) {
        return this.formatter.formatSpreadsheetText(
            value,
            context
        ).map(t -> t.setColor(
                context.lookupColor(this.color)
            )
        );
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
     * The {@link Color}.
     */
    private final Color color;

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
        return this.color.equals(other.color) &&
            this.formatter.equals(other.formatter);
    }

    @Override
    public String toString() {
        return this.token.text() + " " + this.formatter;
    }

    private final ColorSpreadsheetFormatParserToken token;
}
