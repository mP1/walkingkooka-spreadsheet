
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
 * Examines the given {@link ParserToken} that belong to a condition attempting to identify the correct condition and creating the {@link ConditionSpreadsheetFormatParserToken}.
 */
final class SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerConditionSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static ConditionSpreadsheetFormatParserToken condition(final List<ParserToken> value,
                                                           final String text) {
        final SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerConditionSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetFormatParsersEbnfParserCombinatorGrammarTransformerConditionSpreadsheetFormatParserTokenVisitor(
            value,
            text
        );
        ConditionSpreadsheetFormatParserToken condition = null;

        for (final ParserToken token : value) {
            visitor.accept(token);
            condition = visitor.condition;
            if (null != condition) {
                break;
            }
        }

        if (null == condition) {
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
    protected Visiting startVisit(final ColorSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final DateSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final ExponentSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final ExpressionSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final FractionSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final GeneralSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final LessThanSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormatParserToken token) {
        return Visiting.SKIP;
    }

    @Override
    protected void visit(final EqualsSymbolSpreadsheetFormatParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::equalsSpreadsheetFormatParserToken);
    }

    @Override
    protected void visit(final GreaterThanEqualsSymbolSpreadsheetFormatParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::greaterThanEquals);
    }

    @Override
    protected void visit(final GreaterThanSymbolSpreadsheetFormatParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::greaterThan);
    }

    @Override
    protected void visit(final LessThanSymbolSpreadsheetFormatParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::lessThan);
    }

    @Override
    protected void visit(final LessThanEqualsSymbolSpreadsheetFormatParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::lessThanEquals);
    }

    @Override
    protected void visit(final NotEqualsSymbolSpreadsheetFormatParserToken token) {
        this.setCondition(SpreadsheetFormatParserToken::notEquals);
    }

    private void setCondition(final BiFunction<List<ParserToken>, String, ConditionSpreadsheetFormatParserToken> condition) {
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

    private ConditionSpreadsheetFormatParserToken condition;
}
