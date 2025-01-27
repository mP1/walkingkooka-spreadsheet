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

import walkingkooka.spreadsheet.format.parser.BracketCloseSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.BracketOpenSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ColorLiteralSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ColorNameSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ColorNumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ConditionNumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.EqualsSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.EscapeSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GeneralSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanEqualsSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanEqualsSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NotEqualsSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.QuotedTextSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SeparatorSymbolSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.StarSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TextLiteralSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.TextPlaceholderSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.UnderscoreSpreadsheetFormatParserToken;

/**
 * A base {@link SpreadsheetFormatParserTokenVisitor} where most (almost all overrides fail when an unexpected or invalid
 * {@link SpreadsheetFormatParserToken} is found.
 */
abstract class SpreadsheetParsePatternSpreadsheetFormatParserTokenVisitor<T extends SpreadsheetFormatParserToken> extends SpreadsheetPatternSpreadsheetFormatParserTokenVisitor {

    SpreadsheetParsePatternSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected final void visit(final BracketCloseSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final BracketOpenSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final ColorLiteralSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final ColorNameSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final ColorNumberSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final EqualsSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final EscapeSpreadsheetFormatParserToken token) {
        this.text(String.valueOf(token.value()));
    }

    @Override
    protected final void visit(final GeneralSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final GreaterThanEqualsSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final GreaterThanSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final LessThanEqualsSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final LessThanSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final NotEqualsSymbolSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final QuotedTextSpreadsheetFormatParserToken token) {
        this.text(token.value());
    }

    @Override
    protected final void visit(final SeparatorSymbolSpreadsheetFormatParserToken token) {
        // separators do not contribute any required or optional text in anything being parsed.
    }

    @Override
    protected final void visit(final StarSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final TextLiteralSpreadsheetFormatParserToken token) {
        this.text(token.text());
    }

    @Override
    protected final void visit(final TextPlaceholderSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final UnderscoreSpreadsheetFormatParserToken token) {
        this.failInvalid();
    }

    abstract void text(final String text);
}
