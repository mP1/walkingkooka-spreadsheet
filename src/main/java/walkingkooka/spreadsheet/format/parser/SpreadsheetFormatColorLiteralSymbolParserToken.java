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
package walkingkooka.spreadsheet.format.parser;

import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents the COLOR (symbol) token.
 */
public final class SpreadsheetFormatColorLiteralSymbolParserToken extends SpreadsheetFormatSymbolParserToken {

    static SpreadsheetFormatColorLiteralSymbolParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatColorLiteralSymbolParserToken(value, text);
    }

    private SpreadsheetFormatColorLiteralSymbolParserToken(final String value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetFormatColorLiteralSymbolParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetFormatColorLiteralSymbolParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetFormatColorLiteralSymbolParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SpreadsheetFormatColorLiteralSymbolParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatColorLiteralSymbolParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                         final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetFormatColorLiteralSymbolParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatColorLiteralSymbolParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                                    final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetFormatColorLiteralSymbolParserToken.class
        );
    }
    // SpreadsheetFormatParserTokenVisitor..............................................................................

    @Override
    void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetFormatParserTokenKind ................................................................................

    @Override
    public Optional<SpreadsheetFormatParserTokenKind> kind() {
        return EMPTY_KIND;
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatColorLiteralSymbolParserToken;
    }

}
