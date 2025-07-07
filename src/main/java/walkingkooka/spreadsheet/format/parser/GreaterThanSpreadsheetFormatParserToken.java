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

/**
 * Represents a greater than test with its parameters.
 */
public final class GreaterThanSpreadsheetFormatParserToken extends ConditionSpreadsheetFormatParserToken {

    static GreaterThanSpreadsheetFormatParserToken with(final List<ParserToken> value, final String text) {
        return new GreaterThanSpreadsheetFormatParserToken(copyAndCheckTokensFailIfEmpty(value),
            checkTextNotEmptyOrWhitespace(text));
    }

    private GreaterThanSpreadsheetFormatParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    @Override
    public CompareResult compareResult() {
        return CompareResult.GT;
    }

    // children.........................................................................................................

    @Override
    public GreaterThanSpreadsheetFormatParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
            this,
            children,
            GreaterThanSpreadsheetFormatParserToken::with
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
}
