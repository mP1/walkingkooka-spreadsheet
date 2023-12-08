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

import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A token that holds a row reference.
 */
public final class SpreadsheetRowReferenceParserToken extends SpreadsheetNonSymbolParserToken<SpreadsheetRowReference> {

    static SpreadsheetRowReferenceParserToken with(final SpreadsheetRowReference value,
                                                   final String text) {
        return new SpreadsheetRowReferenceParserToken(
                checkValue(value),
                checkText(text)
        );
    }

    private SpreadsheetRowReferenceParserToken(final SpreadsheetRowReference value, final String text) {
        super(value, text);
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetRowReferenceParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfLeaf(
                this,
                predicate,
                SpreadsheetRowReferenceParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetRowReferenceParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfLeaf(
                this,
                predicate,
                SpreadsheetRowReferenceParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetRowReferenceParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                             final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetRowReferenceParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetRowReferenceParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                        final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetRowReferenceParserToken.class
        );
    }
    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetRowReferenceParserToken;
    }
}
