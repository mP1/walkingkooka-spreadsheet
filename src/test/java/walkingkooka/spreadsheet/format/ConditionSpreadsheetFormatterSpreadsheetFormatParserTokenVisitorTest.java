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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.predicate.PredicateTesting;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatConditionParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.text.cursor.parser.ParserToken;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

walkingkooka.reflect.*;

public final class ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitorTest extends SpreadsheetFormatParserTokenVisitorTestCase<ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor>
        implements PredicateTesting,
        ToStringTesting<ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testEquals() {
        this.predicateOrFailAndCheck(SpreadsheetFormatParserToken.equalsSymbol("=", "="),
                SpreadsheetFormatParserToken::equalsParserToken,
                10,
                10,
                11);
    }

    @Test
    public void testGreaterThan() {
        this.predicateOrFailAndCheck(SpreadsheetFormatParserToken.greaterThanSymbol("<", "<"),
                SpreadsheetFormatParserToken::greaterThan,
                11,
                12,
                9);
    }

    @Test
    public void testGreaterThanEquals() {
        this.predicateOrFailAndCheck(SpreadsheetFormatParserToken.greaterThanSymbol("<=", "<="),
                SpreadsheetFormatParserToken::greaterThanEquals,
                10,
                11,
                9);
    }

    @Test
    public void testLessThan() {
        this.predicateOrFailAndCheck(SpreadsheetFormatParserToken.lessThanSymbol("<", "<"),
                SpreadsheetFormatParserToken::lessThan,
                9,
                8,
                10);
    }

    @Test
    public void testLessThanEquals() {
        this.predicateOrFailAndCheck(SpreadsheetFormatParserToken.lessThanSymbol("<=", "<="),
                SpreadsheetFormatParserToken::lessThanEquals,
                9,
                10,
                11);
    }

    @Test
    public void testNotEquals() {
        this.predicateOrFailAndCheck(SpreadsheetFormatParserToken.notEqualsSymbol("!=", "!="),
                SpreadsheetFormatParserToken::notEquals,
                9,
                11,
                10);
    }

    private void predicateOrFailAndCheck(final SpreadsheetFormatParserToken symbol,
                                         final BiFunction<List<ParserToken>, String, SpreadsheetFormatConditionParserToken> factory,
                                         final Integer trueValue,
                                         final Integer trueValue2,
                                         final Integer falseValue) {
        final List<ParserToken> tokens = Lists.of(
                SpreadsheetFormatParserToken.bracketCloseSymbol("[", "["),
                symbol,
                SpreadsheetFormatParserToken.conditionNumber(BigDecimal.valueOf(10), "1.5"),
                SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]"));

        final SpreadsheetFormatConditionParserToken token = factory.apply(tokens, ParserToken.text(tokens));
        final Predicate<BigDecimal> predicate = ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor.predicateOrFail(token);

        this.testTrue(predicate, BigDecimal.valueOf(trueValue));
        this.testTrue(predicate, BigDecimal.valueOf(trueValue2));
        this.testFalse(predicate, BigDecimal.valueOf(falseValue));
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createVisitor(), "");
    }

    @Test
    public void testToString2() {
        final ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor visitor = this.createVisitor();

        final List<ParserToken> tokens = Lists.of(
                SpreadsheetFormatParserToken.bracketCloseSymbol("[", "["),
                SpreadsheetFormatParserToken.lessThanSymbol("<", "<"),
                SpreadsheetFormatParserToken.conditionNumber(BigDecimal.valueOf(1.5), "1.5"),
                SpreadsheetFormatParserToken.bracketCloseSymbol("]", "]")
        );

        visitor.accept(SpreadsheetFormatParserToken.lessThan(tokens, ParserToken.text(tokens)));
        this.toStringAndCheck(visitor, "LT 1.5");
    }

    @Override
    public ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor();
    }

    @Override
    public String typeNamePrefix() {
        return ConditionSpreadsheetFormatter.class.getSimpleName();
    }

    @Override
    public Class<ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor> type() {
        return ConditionSpreadsheetFormatterSpreadsheetFormatParserTokenVisitor.class;
    }
}
