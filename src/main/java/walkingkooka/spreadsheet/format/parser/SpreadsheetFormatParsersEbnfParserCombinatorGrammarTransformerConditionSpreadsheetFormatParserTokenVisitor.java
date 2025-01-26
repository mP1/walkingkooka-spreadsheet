
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
import java.util.function.BiFunction;

/**
 * Examines the given {@link ParserToken} that belong to a condition attempting to identify the correct condition and creating the {@link SpreadsheetFormatConditionParserToken}.
 */
final class SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerConditionSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static SpreadsheetFormatConditionParserToken condition(final List<ParserToken> value,
                                                           final String text) {
        final SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerConditionSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerConditionSpreadsheetFormatParserTokenVisitor(
                value,
                text
        );
        SpreadsheetFormatConditionParserToken condition = null;

        for(final ParserToken token : value) {
            visitor.accept(token);
            condition = visitor.condition;
            if(null != condition) {
                break;
            }
        }

        if(null == condition) {
            throw new IllegalStateException("Missing condition");
        }
        return condition;
    }

    // @VisibleForTesting
    SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerConditionSpreadsheetFormatParserTokenVisitor(final List<ParserToken> value,
                                                                                                               final String text) {
        super();
        this.value = value;
        this.text = text;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatColorParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatDateTimeParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatEqualsParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatExponentParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatExpressionParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatFractionParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGeneralParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatLessThanParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNumberParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTextParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatTimeParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected void visit(final SpreadsheetFormatEqualsSymbolParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::equalsParserToken);
    }

    @Override
    protected void visit(final SpreadsheetFormatGreaterThanEqualsSymbolParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::greaterThanEquals);
    }

    @Override
    protected void visit(final SpreadsheetFormatGreaterThanSymbolParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::greaterThan);
    }

    @Override
    protected void visit(final SpreadsheetFormatLessThanSymbolParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::lessThan);
    }

    @Override
    protected void visit(final SpreadsheetFormatLessThanEqualsSymbolParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::lessThanEquals);
    }

    @Override
    protected void visit(final SpreadsheetFormatNotEqualsSymbolParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::notEquals);
    }

    private void setCondition(final BiFunction<List<ParserToken>, String, SpreadsheetFormatConditionParserToken> condition) {
        this.condition = condition.apply(
                this.value,
                this.text
        );
    }

    // Object...........................................................................................................

    @Override
    public String toString() {
        return String.valueOf(this.condition);
    }

    private final List<ParserToken> value;
    private final String text;

    private SpreadsheetFormatConditionParserToken condition;
}
