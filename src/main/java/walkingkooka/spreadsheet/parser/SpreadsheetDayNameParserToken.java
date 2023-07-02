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
 * Represents the week day name within a date or date/time.
 */
public final class SpreadsheetDayNameParserToken extends SpreadsheetNonSymbolParserToken<Integer> {

    static SpreadsheetDayNameParserToken with(final int value, final String text) {
        Objects.requireNonNull(text, "text");

        return new SpreadsheetDayNameParserToken(value, text);
    }

    private SpreadsheetDayNameParserToken(final int value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetDayNameParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetDayNameParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetDayNameParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                        final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetDayNameParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetDayNameParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                   final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetDayNameParserToken.class
        );
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetDayNameParserToken;
    }
}
