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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBracketCloseSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBracketOpenSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorLiteralSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorNameParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatColorNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEqualsSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEscapeParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGeneralSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanEqualsSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanEqualsSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNotEqualsSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatQuotedTextParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatStarParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextLiteralParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatTextPlaceholderParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatUnderscoreParserToken;

/**
 * A base {@link SpreadsheetFormatParserTokenVisitor} where most (almost all overrides fail when an unexpected or invalid
 * {@link SpreadsheetFormatParserToken} is found.
 */
abstract class SpreadsheetParsePatternSpreadsheetFormatParserTokenVisitor<T extends SpreadsheetFormatParserToken> extends SpreadsheetPatternSpreadsheetFormatParserTokenVisitor {

    SpreadsheetParsePatternSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected final void visit(final SpreadsheetFormatBracketCloseSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatBracketOpenSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatColorLiteralSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatColorNameParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatColorNumberParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatConditionNumberParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatEqualsSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatEscapeParserToken token) {
        this.text(String.valueOf(token.value()));
    }

    @Override
    protected final void visit(final SpreadsheetFormatGeneralSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatGreaterThanEqualsSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatGreaterThanSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatLessThanEqualsSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatLessThanSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatNotEqualsSymbolParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatQuotedTextParserToken token) {
        this.text(token.value());
    }

    @Override
    protected final void visit(final SpreadsheetFormatSeparatorSymbolParserToken token) {
        // separators do not contribute any required or optional text in anything being parsed.
    }

    @Override
    protected final void visit(final SpreadsheetFormatStarParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatTextLiteralParserToken token) {
        this.text(token.text());
    }

    @Override
    protected final void visit(final SpreadsheetFormatTextPlaceholderParserToken token) {
        this.failInvalid();
    }

    @Override
    protected final void visit(final SpreadsheetFormatUnderscoreParserToken token) {
        this.failInvalid();
    }

    abstract void text(final String text);
}
