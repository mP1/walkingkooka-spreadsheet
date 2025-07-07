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

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.RepeatedParserToken;
import walkingkooka.text.cursor.parser.SequenceParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * The DateTime {@link Parser} initially parses all M tokens into {@link MonthSpreadsheetFormatParserToken}, however some of these particularly those after HOURS, need to be converted into {@link MinuteSpreadsheetFormatParserToken}.
 */
final class SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static DateTimeSpreadsheetFormatParserToken fixMinutes(final ParserToken token) {
        final SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return SpreadsheetFormatParserToken.dateTime(
            visitor.tokens,
            token.text()
        );
    }

    // @VisibleForTesting
    SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final ParserToken token) {
        this.add = token;
        return Visiting.CONTINUE;
    }

    @Override
    protected void endVisit(final ParserToken token) {
        final ParserToken add = this.add;
        if (null != add) {
            this.tokens.add(add);
        }
    }

    @Override
    protected void endVisit(final RepeatedParserToken token) {
        this.add = null;
    }

    @Override
    protected void endVisit(final SequenceParserToken token) {
        this.add = null;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
        return token.isParent() ?
            Visiting.SKIP : // leave color etc alone
            Visiting.CONTINUE;
    }

    @Override
    protected void visit(final AmPmSpreadsheetFormatParserToken token) {
        this.minute = true;
    }

    @Override
    protected void visit(final DaySpreadsheetFormatParserToken token) {
        this.minute = false;
    }

    // any month after an hour should be replaced by a minute.
    @Override
    protected void visit(final HourSpreadsheetFormatParserToken token) {
        this.minute = true;
    }

    @Override
    protected void visit(final MinuteSpreadsheetFormatParserToken token) {
        throw new UnsupportedOperationException(); // minutes cannot appear in a DATETIME_FORMAT or DATETIME_PARSE
    }

    @Override
    protected void visit(final MonthSpreadsheetFormatParserToken token) {
        if (this.minute) {
            this.add = SpreadsheetFormatParserToken.minute(
                token.value(),
                token.text()
            );
        }
    }

    @Override
    protected void visit(final SecondSpreadsheetFormatParserToken token) {
        this.minute = true;
    }

    // any month after a year should stay as a month
    @Override
    protected void visit(final YearSpreadsheetFormatParserToken token) {
        this.minute = false;
    }

    /**
     * When true any {@link MonthSpreadsheetFormatParserToken} will be replaced by a {@link MinuteSpreadsheetFormatParserToken},
     * with the same text.
     */
    private boolean minute = false;

    /**
     * The token that will be added by {@link #endVisit(ParserToken)}.
     */
    private ParserToken add;

    /**
     * Collects all tokens including the replacement of MONTHS with MINUTES as necessary.
     */
    private final List<ParserToken> tokens = Lists.array();

    @Override
    public String toString() {
        return this.tokens.toString();
    }
}
