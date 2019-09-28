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
 * Represents a less than symbol token.
 */
public final class SpreadsheetLessThanSymbolParserToken extends SpreadsheetSymbolParserToken {

    static SpreadsheetLessThanSymbolParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetLessThanSymbolParserToken(value, text);
    }

    private SpreadsheetLessThanSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    @Override
    final int operatorPriority() {
        return GREATER_THAN_LESS_THAN_PRIORITY;
    }

    @Override
    final SpreadsheetParserToken binaryOperand(final List<ParserToken> tokens, final String text) {
        return lessThan(tokens, text);
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetLessThanSymbolParserToken;
    }
}
