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
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;

/**
 * A token that contains a text formatting tokens.
 */
public final class SpreadsheetFormatTextParserToken extends SpreadsheetFormatParentParserToken<SpreadsheetFormatTextParserToken> {

    /**
     * Factory that creates a new {@link SpreadsheetFormatTextParserToken}.
     */
    static SpreadsheetFormatTextParserToken with(final List<ParserToken> value, final String text) {
        final List<ParserToken> copy = copyAndCheckTokens(value);
        Objects.requireNonNull(text, "text");

        return new SpreadsheetFormatTextParserToken(copy, text);
    }

    /**
     * Private ctor use helper.
     */
    private SpreadsheetFormatTextParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    // isXXX...........................................................................................................

    @Override
    public boolean isColor() {
        return false;
    }

    @Override
    public boolean isCondition() {
        return false;
    }

    @Override
    public boolean isDate() {
        return false;
    }

    @Override
    public boolean isDateTime() {
        return false;
    }

    @Override
    public boolean isEquals() {
        return false;
    }

    @Override
    public boolean isExponent() {
        return false;
    }

    @Override
    public boolean isExpression() {
        return false;
    }

    @Override
    public boolean isFraction() {
        return false;
    }

    @Override
    public boolean isGeneral() {
        return false;
    }

    @Override
    public boolean isGreaterThan() {
        return false;
    }

    @Override
    public boolean isGreaterThanEquals() {
        return false;
    }

    @Override
    public boolean isLessThan() {
        return false;
    }

    @Override
    public boolean isLessThanEquals() {
        return false;
    }

    @Override
    public boolean isNotEquals() {
        return false;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isText() {
        return true;
    }

    @Override
    public boolean isTime() {
        return false;
    }

    // SpreadsheetFormatParserTokenVisitor..............................................................................

    @Override
    public void accept(SpreadsheetFormatParserTokenVisitor visitor) {
        if (Visiting.CONTINUE == visitor.startVisit(this)) {
            this.acceptValues(visitor);
        }
        visitor.endVisit(this);
    }

    // Object...........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatTextParserToken;
    }

}
