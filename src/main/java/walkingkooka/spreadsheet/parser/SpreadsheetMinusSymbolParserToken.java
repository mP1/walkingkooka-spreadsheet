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
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a minus symbol token.
 */
public final class SpreadsheetMinusSymbolParserToken extends SpreadsheetSymbolParserToken {

    static SpreadsheetMinusSymbolParserToken with(final String value,
                                                  final String text) {
        return new SpreadsheetMinusSymbolParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetMinusSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    int operatorPriority() {
        return ADDITION_SUBTRACTION_PRIORITY;
    }

    @Override
    SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        return subtraction(tokens, text);
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetMinusSymbolParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                            final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetMinusSymbolParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetMinusSymbolParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                       final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetMinusSymbolParserToken.class
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
        return other instanceof SpreadsheetMinusSymbolParserToken;
    }
}
