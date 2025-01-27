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
package walkingkooka.spreadsheet.formula;

import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * Represents a condition less than equals test with a right hand parameter.
 */
public final class ConditionRightLessThanEqualsSpreadsheetFormulaParserToken extends ConditionRightSpreadsheetFormulaParserToken {

    static ConditionRightLessThanEqualsSpreadsheetFormulaParserToken with(final List<ParserToken> value,
                                                                          final String text) {
        return new ConditionRightLessThanEqualsSpreadsheetFormulaParserToken(
                Lists.immutable(value),
                checkText(text)
        );
    }

    private ConditionRightLessThanEqualsSpreadsheetFormulaParserToken(final List<ParserToken> value, final String text) {
        super(value, text);
    }

    @Override
    LessThanEqualsSpreadsheetFormulaParserToken setConditionLeft0(final List<ParserToken> tokens,
                                                                  final String text) {
        return lessThanEquals(
                tokens,
                text
        );
    }

    // children.........................................................................................................

    @Override
    public ConditionRightLessThanEqualsSpreadsheetFormulaParserToken setChildren(final List<ParserToken> children) {
        return ParserToken.parentSetChildren(
                this,
                children,
                ConditionRightLessThanEqualsSpreadsheetFormulaParserToken::with
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
