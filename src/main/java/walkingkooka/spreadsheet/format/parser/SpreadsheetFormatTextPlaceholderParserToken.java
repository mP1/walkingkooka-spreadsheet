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
import java.util.function.Predicate;

/**
 * Represents the text placeholder token.
 */
public final class SpreadsheetFormatTextPlaceholderParserToken extends SpreadsheetFormatNonSymbolParserToken<String> {

    static SpreadsheetFormatTextPlaceholderParserToken with(final String value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatTextPlaceholderParserToken(value, text);
    }

    private SpreadsheetFormatTextPlaceholderParserToken(final String value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetFormatTextPlaceholderParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetFormatTextPlaceholderParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetFormatTextPlaceholderParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SpreadsheetFormatTextPlaceholderParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatTextPlaceholderParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                      final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetFormatTextPlaceholderParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatTextPlaceholderParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                                 final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetFormatTextPlaceholderParserToken.class
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
        return SpreadsheetFormatParserTokenKind.TEXT_PLACEHOLDER.asOptional;
    }

    // equals ..........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatTextPlaceholderParserToken;
    }

}
