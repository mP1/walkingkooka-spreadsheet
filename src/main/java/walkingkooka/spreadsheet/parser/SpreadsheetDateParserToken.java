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

import walkingkooka.datetime.DateTimeContext;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.time.LocalDate;
import java.util.List;

/**
 * Holds a date value along with the tokens and original text.
 */
public final class SpreadsheetDateParserToken extends SpreadsheetValueParserToken {

    static SpreadsheetDateParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetDateParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private SpreadsheetDateParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    /**
     * Creates a {@link LocalDate} parse the tokens in this {@link SpreadsheetDateParserToken}.
     */
    public LocalDate toLocalDate(final DateTimeContext context) {
        return SpreadsheetParserTokenVisitorLocalDateTime.acceptSpreadsheetParentParserToken(
                this,
                context.defaultYear()
        ).toLocalDate(context);
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetDateParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetDateParserToken::with
        );
    }

    // SpreadsheetParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetDateParserToken;
    }
}
