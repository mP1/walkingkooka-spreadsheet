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
package walkingkooka.spreadsheet.parser;

import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Holds the combination of whitespace or comments.
 */
public final class SpreadsheetWhitespaceParserToken extends SpreadsheetSymbolParserToken {

    static SpreadsheetWhitespaceParserToken with(final String value, final String text) {
        return new SpreadsheetWhitespaceParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetWhitespaceParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    int operatorPriority() {
        return IGNORED;
    }

    @Override
    SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        throw new UnsupportedOperationException();
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetWhitespaceParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetWhitespaceParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetWhitespaceParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                           final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetWhitespaceParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetWhitespaceParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                      final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetWhitespaceParserToken.class
        );
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetWhitespaceParserToken;
    }
}
