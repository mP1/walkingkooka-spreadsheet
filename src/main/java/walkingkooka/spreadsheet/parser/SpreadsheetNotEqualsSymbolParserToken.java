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
 * Represents a equals symbol token.
 */
public final class SpreadsheetNotEqualsSymbolParserToken extends SpreadsheetSymbolParserToken {

    static SpreadsheetNotEqualsSymbolParserToken with(final String value,
                                                      final String text) {
        return new SpreadsheetNotEqualsSymbolParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetNotEqualsSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    int operatorPriority() {
        return GREATER_THAN_LESS_THAN_PRIORITY;
    }

    @Override
    SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        return notEquals(tokens, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetNotEqualsSymbolParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetNotEqualsSymbolParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetNotEqualsSymbolParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SpreadsheetNotEqualsSymbolParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetNotEqualsSymbolParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetNotEqualsSymbolParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetNotEqualsSymbolParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                           final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetNotEqualsSymbolParserToken.class
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
        return other instanceof SpreadsheetNotEqualsSymbolParserToken;
    }
}
