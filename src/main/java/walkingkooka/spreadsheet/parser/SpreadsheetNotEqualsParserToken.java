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

import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * Represents a not equals test operation with its parameters.
 */
public final class SpreadsheetNotEqualsParserToken extends SpreadsheetConditionParserToken {

    static SpreadsheetNotEqualsParserToken with(final List<ParserToken> value, final String text) {
        return new SpreadsheetNotEqualsParserToken(copyAndCheckTokens(value), checkText(text));
    }

    private SpreadsheetNotEqualsParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    @Override
    SpreadsheetConditionRightNotEqualsParserToken toSpreadsheetConditionRightParserToken0(final List<ParserToken> tokens,
                                                                                          final String text) {

        return SpreadsheetParserToken.conditionRightNotEquals(
                tokens,
                text
        );
    }

    // children.........................................................................................................

    @Override
    public SpreadsheetNotEqualsParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                SpreadsheetNotEqualsParserToken::with
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
