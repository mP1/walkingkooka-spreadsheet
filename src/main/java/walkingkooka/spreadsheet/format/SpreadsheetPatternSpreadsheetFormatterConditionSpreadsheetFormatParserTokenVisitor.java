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

package walkingkooka.spreadsheet.format;

import walkingkooka.ToStringBuilder;
import walkingkooka.compare.CompareResult;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionNumberParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatGreaterThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatLessThanParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNotEqualsParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;

import java.math.BigDecimal;
import java.util.function.Predicate;

/**
 * Finds the condition and number parameter in the {@link SpreadsheetFormatConditionParserToken}.
 */
final class SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static Predicate<BigDecimal> predicateOrFail(final SpreadsheetFormatConditionParserToken token) {
        final SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor();
        token.accept(visitor);
        return visitor.relation.predicate(visitor.number);
    }

    // @VisibleForTesting.
    SpreadsheetPatternSpreadsheetFormatterConditionSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected void endVisit(final SpreadsheetFormatEqualsParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanEqualsParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatGreaterThanParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanEqualsParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatLessThanParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFormatNotEqualsParserToken token) {
        this.setRelation(token);
    }

    @Override
    protected void visit(final SpreadsheetFormatConditionNumberParserToken token) {
        this.number = token.value();
    }

    private void setRelation(final SpreadsheetFormatConditionParserToken token) {
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
