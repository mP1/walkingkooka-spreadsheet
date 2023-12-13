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
