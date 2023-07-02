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
 * Represents the month name initial within a date or date/time.
 */
public final class SpreadsheetMonthNameInitialParserToken extends SpreadsheetNonSymbolParserToken<Integer> {

    static SpreadsheetMonthNameInitialParserToken with(final int value, final String text) {
        Objects.requireNonNull(text, "text");

        return new SpreadsheetMonthNameInitialParserToken(value, text);
    }

    private SpreadsheetMonthNameInitialParserToken(final int value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetMonthNameInitialParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetMonthNameInitialParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetMonthNameInitialParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SpreadsheetMonthNameInitialParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetMonthNameInitialParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                 final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetMonthNameInitialParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetMonthNameInitialParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                            final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetMonthNameInitialParserToken.class
        );
    }
    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetMonthNameInitialParserToken;
    }
}
