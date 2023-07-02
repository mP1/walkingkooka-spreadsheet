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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a number or sequence of digits without a decimal point or sign.
 */
public final class SpreadsheetDigitsParserToken extends SpreadsheetNonSymbolParserToken<String> {

    static SpreadsheetDigitsParserToken with(final String value, final String text) {
        checkValue(value);
        Objects.requireNonNull(text, "text");

        return new SpreadsheetDigitsParserToken(value, text);
    }

    private SpreadsheetDigitsParserToken(final String value, final String text) {
        super(value, text);
    }
    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetDigitsParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetDigitsParserToken.class
        );
    }
    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetDigitsParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                       final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetDigitsParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetDigitsParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                  final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetDigitsParserToken.class
        );
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetDigitsParserToken;
    }
}
