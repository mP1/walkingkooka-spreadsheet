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

import walkingkooka.compare.CompareResult;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents a less than test with its parameters.
 */
public final class SpreadsheetFormatLessThanParserToken extends SpreadsheetFormatConditionParserToken {

    static SpreadsheetFormatLessThanParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetFormatLessThanParserToken(copyAndCheckTokensFailIfEmpty(value),
                checkTextNotEmptyOrWhitespace(text));
    }

    private SpreadsheetFormatLessThanParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    @Override
    public CompareResult compareResult() {
        return CompareResult.LT;
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetFormatLessThanParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetFormatLessThanParserToken::with
        );
    }

    // removeFirstIf....................................................................................................

    @Override
    public Optional<SpreadsheetFormatLessThanParserToken> removeFirstIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeFirstIfParent(
                this,
                predicate,
                SpreadsheetFormatLessThanParserToken.class
        );
    }

    // removeIf.........................................................................................................

    @Override
    public Optional<SpreadsheetFormatLessThanParserToken> removeIf(final Predicate<ParserToken> predicate) {
        return ParserToken.removeIfParent(
                this,
                predicate,
                SpreadsheetFormatLessThanParserToken.class
        );
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatLessThanParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                               final ParserToken token) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                token,
                SpreadsheetFormatLessThanParserToken.class
        );
    }

    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatLessThanParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                          final ParserToken token) {
        return ParserToken.replaceIf(
                this,
                predicate,
                token,
                SpreadsheetFormatLessThanParserToken.class
        );
    }

    // SpreadsheetFormatParserTokenVisitor..............................................................................

    @Override
    void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatLessThanParserToken;
    }

}
