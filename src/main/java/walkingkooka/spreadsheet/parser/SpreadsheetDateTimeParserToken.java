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

import java.time.LocalDateTime;
import java.util.List;

/**
 * Holds a date/time value along with the tokens and original text.
 */
public final class SpreadsheetDateTimeParserToken extends SpreadsheetValueParserToken {

    static SpreadsheetDateTimeParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetDateTimeParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private SpreadsheetDateTimeParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    /**
     * Creates a {@link LocalDateTime} parse the tokens in this {@link SpreadsheetDateTimeParserToken}.
     */
    public LocalDateTime toLocalDateTime(final DateTimeContext context) {
        return SpreadsheetParserTokenVisitorLocalDateTime.acceptSpreadsheetParentParserToken(
                this,
                context.defaultYear()
        ).toLocalDateTime(context);
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetDateTimeParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetDateTimeParserToken::with
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
}
