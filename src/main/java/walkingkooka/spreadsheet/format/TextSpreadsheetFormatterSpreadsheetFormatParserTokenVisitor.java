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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatStarParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextPlaceholderParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatUnderscoreParserToken;

/**
 * A {@link SpreadsheetFormatParserTokenVisitor} is used exclusively by {@link SpreadsheetFormatter#format(Object, SpreadsheetFormatterContext)}
 * to assemble a {@link SpreadsheetFormatter} that handles formatting text, all other tokens are ignored.
 */
final class TextSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatter3SpreadsheetFormatParserTokenVisitor {

    /**
     * Visits all the individual tokens in the given token which was compiled from the given pattern.
     */
    static SpreadsheetText format(final SpreadsheetFormatTextParserToken token, final String value, final SpreadsheetFormatterContext context) {
        final TextSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor visitor = new TextSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor(value, context);
        visitor.accept(token);
        return SpreadsheetText.with(SpreadsheetText.WITHOUT_COLOR, visitor.text.toString());
    }

    /**
     * Private ctor use static method.
     */
    TextSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor(final String value, final SpreadsheetFormatterContext context) {
        super();
        this.context = context;
        this.value = value;
    }

    @Override
    protected void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final SpreadsheetFormatStarParserToken token) {
        final int fill = this.context.width() - this.text.length();
        final char c = token.value();

        for (int i = 0; i < fill; i++) {
            this.text.append(c);
        }
    }

    @Override
    protected void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.append(token.value());
    }

    @Override
    protected void visit(final SpreadsheetFormatTextPlaceholderParserToken token) {
        this.append(this.value);
    }

    private final String value;

    @Override
    protected void visit(final SpreadsheetFormatUnderscoreParserToken token) {
        this.append(token.value());
    }

    private void append(final char c) {
        this.text.append(c);
    }

    private void append(final String text) {
        this.text.append(text);
    }

    private final SpreadsheetFormatterContext context;

    /**
     * Aggregates the formatted output text.
     */
    private final StringBuilder text = new StringBuilder();

    @Override
    public String toString() {
        return this.text.toString();
    }
}
