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
 * Represents a greater than symbol token.
 */
public final class SpreadsheetGreaterThanSymbolParserToken extends SpreadsheetSymbolParserToken {

    static SpreadsheetGreaterThanSymbolParserToken with(final String value,
                                                        final String text) {
        return new SpreadsheetGreaterThanSymbolParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetGreaterThanSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    int operatorPriority() {
        return GREATER_THAN_LESS_THAN_PRIORITY;
    }

    @Override
    SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        return greaterThan(tokens, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetGreaterThanSymbolParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetGreaterThanSymbolParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetGreaterThanSymbolParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SpreadsheetGreaterThanSymbolParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetGreaterThanSymbolParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                  final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetGreaterThanSymbolParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetGreaterThanSymbolParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                             final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetGreaterThanSymbolParserToken.class
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
        return other instanceof SpreadsheetGreaterThanSymbolParserToken;
    }
}
