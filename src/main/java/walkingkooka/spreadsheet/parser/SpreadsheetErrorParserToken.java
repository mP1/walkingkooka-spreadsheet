
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

import walkingkooka.spreadsheet.SpreadsheetError;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * A token that holds a {@link SpreadsheetError}.
 */
public final class SpreadsheetErrorParserToken extends SpreadsheetNonSymbolParserToken<SpreadsheetError> {

    static SpreadsheetErrorParserToken with(final SpreadsheetError value,
                                            final String text) {
        return new SpreadsheetErrorParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetErrorParserToken(final SpreadsheetError value,
                                        final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetErrorParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetErrorParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetErrorParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                      final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetErrorParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetErrorParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                 final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetErrorParserToken.class
        );
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetErrorParserToken;
    }
}
