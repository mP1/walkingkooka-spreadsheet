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
import walkingkooka.spreadsheet.format.parser.ColorNameSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ColorNumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.EscapeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.QuotedTextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.StarSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TextLiteralSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TextPlaceholderSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.UnderscoreSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.WhitespaceSpreadsheetFormatParserToken;
import walkingkooka.text.CharSequences;

import java.util.Optional;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} is used exclusively by {@link SpreadsheetFormatter#format(Optional, SpreadsheetFormatterContext)}
 * to assemble a {@link SpreadsheetFormatter} that handles formatting text, all other tokens are ignored.
 */
final class SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor extends SpreadsheetPatternSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor {

    /**
     * Visits all the individual tokens in the given token which was compiled parse the given pattern.
     */
    static SpreadsheetText format(final SpreadsheetFormatParserToken token,
                                  final String value,
                                  final SpreadsheetFormatterContext context) {
        final SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor(value, context);
        visitor.accept(token);
        return SpreadsheetText.with(
            visitor.text.toString()
        ).setColor(visitor.color);
    }

    /**
     * Private ctor use static method.
     */
    SpreadsheetPatternSpreadsheetFormatterTextSpreadsheetFormatParserTokenVisitor(final String value,
                                                                                  final SpreadsheetFormatterContext context) {
        super();
        this.context = context;
        this.value = value;
    }

    @Override
    protected void visit(final ColorNameSpreadsheetFormatParserToken token) {
        this.color = this.context.colorName(
            token.colorName()
        );
    }

    @Override
    protected void visit(final ColorNumberSpreadsheetFormatParserToken token) {
        this.color = this.context.colorNumber(
            token.value()
        );
    }

    @Override
    protected void visit(final EscapeSpreadsheetFormatParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final QuotedTextSpreadsheetFormatParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final StarSpreadsheetFormatParserToken token) {
        final int fill = this.context.cellCharacterWidth() - this.text.length();
        final char c = token.value();

        for (int i = 0; i < fill; i++) {
            this.text.append(c);
        }
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetFormatParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final TextPlaceholderSpreadsheetFormatParserToken token) {
        final String value = this.value;
        if (null != value) {
            this.append(value);
        }
    }

    private final String value;

    @Override
    protected void visit(final UnderscoreSpreadsheetFormatParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final WhitespaceSpreadsheetFormatParserToken token) {
        this.append(
            CharSequences.repeating(
                ' ',
                token.value().length()
            )
        );
    }

    private void append(final char c) {
        this.text.append(c);
    }

    private void append(final CharSequence text) {
        this.text.append(text);
    }

    private final SpreadsheetFormatterContext context;

    /**
     * Aggregates the formatted output text.
     */
    private final StringBuilder text = new StringBuilder();

    private Optional<Color> color = SpreadsheetText.WITHOUT_COLOR;

    @Override
    public String toString() {
        return this.text.toString();
    }
}
