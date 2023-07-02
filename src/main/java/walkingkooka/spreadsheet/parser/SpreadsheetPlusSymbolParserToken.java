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
 * Represents a plus symbol token.
 */
public final class SpreadsheetPlusSymbolParserToken extends SpreadsheetSymbolParserToken {

    static SpreadsheetPlusSymbolParserToken with(final String value,
                                                 final String text) {
        return new SpreadsheetPlusSymbolParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetPlusSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    int operatorPriority() {
        return ADDITION_SUBTRACTION_PRIORITY;
    }

    @Override
    SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        return addition(tokens, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetPlusSymbolParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetPlusSymbolParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetPlusSymbolParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SpreadsheetPlusSymbolParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetPlusSymbolParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                           final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetPlusSymbolParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetPlusSymbolParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                      final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetPlusSymbolParserToken.class
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
        return other instanceof SpreadsheetPlusSymbolParserToken;
    }
}
