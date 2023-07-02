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
package walkingkooka.spreadsheet.format.parser;

import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a right bracket ']' symbol token.
 */
public final class SpreadsheetFormatBracketCloseSymbolParserToken extends SpreadsheetFormatSymbolParserToken {

    static SpreadsheetFormatBracketCloseSymbolParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatBracketCloseSymbolParserToken(value, text);
    }

    private SpreadsheetFormatBracketCloseSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetFormatBracketCloseSymbolParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetFormatBracketCloseSymbolParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatBracketCloseSymbolParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                         final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetFormatBracketCloseSymbolParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatBracketCloseSymbolParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                                    final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetFormatBracketCloseSymbolParserToken.class
        );
    }

    // SpreadsheetFormatParserTokenVisitor..............................................................................

    @Override
    void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetFormatParserTokenKind ................................................................................

    @Override
    public Optional<SpreadsheetFormatParserTokenKind> kind() {
        return SpreadsheetFormatParserTokenKind.TEXT_LITERAL.asOptional;
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatBracketCloseSymbolParserToken;
    }

}
