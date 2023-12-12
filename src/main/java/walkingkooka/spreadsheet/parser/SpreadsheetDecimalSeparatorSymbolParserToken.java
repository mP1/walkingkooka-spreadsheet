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
 * Represents the decimal separator symbol token with number.
 */
public final class SpreadsheetDecimalSeparatorSymbolParserToken extends SpreadsheetSymbolParserToken {

    static SpreadsheetDecimalSeparatorSymbolParserToken with(final String value,
                                                             final String text) {
        return new SpreadsheetDecimalSeparatorSymbolParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetDecimalSeparatorSymbolParserToken(final String value, final String text) {
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

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetDecimalSeparatorSymbolParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                       final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetDecimalSeparatorSymbolParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetDecimalSeparatorSymbolParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                                  final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetDecimalSeparatorSymbolParserToken.class
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
        return other instanceof SpreadsheetDecimalSeparatorSymbolParserToken;
    }
}
