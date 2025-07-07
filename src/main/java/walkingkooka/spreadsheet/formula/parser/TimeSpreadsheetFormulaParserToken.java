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
package walkingkooka.spreadsheet.formula.parser;

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.time.LocalTime;
import java.util.List;

/**
 * Holds a time value along with the tokens and original text.
 */
public final class TimeSpreadsheetFormulaParserToken extends ValueSpreadsheetFormulaParserToken {

    static TimeSpreadsheetFormulaParserToken with(final List<ParserToken> value, final String text) {
        return new TimeSpreadsheetFormulaParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private TimeSpreadsheetFormulaParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    /**
     * Creates a {@link LocalTime} parse the tokens in this {@link TimeSpreadsheetFormulaParserToken}.
     */
    public LocalTime toLocalTime() {
        return TemporalSpreadsheetFormulaParserTokenVisitor.acceptSpreadsheetParentParserToken(this, Integer.MAX_VALUE)
            .toLocalTime();
    }

    // children.........................................................................................................

    @Override
    public TimeSpreadsheetFormulaParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
            this,
            children,
            TimeSpreadsheetFormulaParserToken::with
        );
    }

    // SpreadsheetFormulaParserTokenVisitor....................................................................................

    @Override
    void accept(final SpreadsheetFormulaParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }
}
