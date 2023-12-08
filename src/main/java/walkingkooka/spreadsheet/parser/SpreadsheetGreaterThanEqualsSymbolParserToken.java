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
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a greater than equals symbol token.
 */
public final class SpreadsheetGreaterThanEqualsSymbolParserToken extends SpreadsheetSymbolParserToken {

    static SpreadsheetGreaterThanEqualsSymbolParserToken with(final String value,
                                                              final String text) {
        return new SpreadsheetGreaterThanEqualsSymbolParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetGreaterThanEqualsSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    int operatorPriority() {
        return GREATER_THAN_LESS_THAN_PRIORITY;
    }

    @Override
    SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        return greaterThanEquals(tokens, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetGreaterThanEqualsSymbolParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetGreaterThanEqualsSymbolParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetGreaterThanEqualsSymbolParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SpreadsheetGreaterThanEqualsSymbolParserToken.class
        );
    }
    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetGreaterThanEqualsSymbolParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                        final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetGreaterThanEqualsSymbolParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetGreaterThanEqualsSymbolParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                                   final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetGreaterThanEqualsSymbolParserToken.class
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
        return other instanceof SpreadsheetGreaterThanEqualsSymbolParserToken;
    }
}
