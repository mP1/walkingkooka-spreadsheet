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
 * Holds literal text which may be empty. Together with a {@link SpreadsheetApostropheSymbolParserToken} this is a component of
 * a {@link SpreadsheetTextParserToken}.
 */
public final class SpreadsheetTextLiteralParserToken extends SpreadsheetNonSymbolParserToken<String> {

    static SpreadsheetTextLiteralParserToken with(final String value, final String text) {
        checkValue(value);
        Objects.requireNonNull(text, "text"); // empty is ok! eg apostrophe string

        return new SpreadsheetTextLiteralParserToken(value, text);
    }

    private SpreadsheetTextLiteralParserToken(final String value, final String text) {
        super(value, text);
    }
    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetTextLiteralParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetTextLiteralParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetTextLiteralParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SpreadsheetTextLiteralParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetTextLiteralParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                            final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetTextLiteralParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetTextLiteralParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                       final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetTextLiteralParserToken.class
        );
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetTextLiteralParserToken;
    }
}
