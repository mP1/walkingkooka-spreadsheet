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

package walkingkooka.spreadsheet.format.pattern;

import walkingkooka.ToStringBuilder;
import walkingkooka.compare.CompareResult;
import walkingkooka.spreadsheet.format.parser.ConditionNumberSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.ConditionSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.EqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.GreaterThanSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanEqualsSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.LessThanSpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.NotEqualsSpreadsheetFormatParserToken;

import java.math.BigDecimal;
import java.util.function.Predicate;

/**
 * Finds the condition and number parameter in the {@link ConditionSpreadsheetFormatParserToken}.
 */
final class SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor extends SpreadsheetPatternSpreadsheetFormatParserTokenVisitor {

    static Predicate<BigDecimal> predicateOrFail(final ConditionSpreadsheetFormatParserToken token) {
        final SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor();
        token.accept(visitor);
        return visitor.relation.predicate(visitor.number);
    }

    // @VisibleForTesting.
    SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected void endVisit(final EqualsSpreadsheetFormatParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final GreaterThanEqualsSpreadsheetFormatParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final GreaterThanSpreadsheetFormatParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final LessThanEqualsSpreadsheetFormatParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final LessThanSpreadsheetFormatParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final NotEqualsSpreadsheetFormatParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void visit(final ConditionNumberSpreadsheetFormatParserToken token) {
        this.number = token.value();
    }

    private void setRelation(final ConditionSpreadsheetFormatParserToken token) {
        this.relation = token.compareResult();
    }

    private CompareResult relation;
    private BigDecimal number;

    @Override
    public String toString() {
        return ToStringBuilder.empty()
            .valueSeparator(" ")
            .value(this.relation)
            .value(this.number)
            .build();
    }
}
