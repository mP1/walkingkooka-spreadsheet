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

package walkingkooka.spreadsheet.engine;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.spreadsheet.parser.SpreadsheetAdditionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetAmPmParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetApostropheSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetBetweenSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetCellRangeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetCellReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetColumnReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetCurrencySymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDateParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDateTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDayNameAbbreviationParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDayNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDayNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDecimalSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDigitsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDivideSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDivisionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetDoubleQuoteSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetExponentSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetExpressionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetFunctionNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGreaterThanSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGroupParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetGroupSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetHourParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLabelNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetLessThanSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMillisecondParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMinusSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMinuteParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNameAbbreviationParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNameInitialParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNameParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMonthNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMultiplicationParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetMultiplySymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNamedFunctionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNegativeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNotEqualsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNotEqualsSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetNumberParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParentParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParenthesisCloseSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParenthesisOpenSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserTokenVisitor;
import walkingkooka.spreadsheet.parser.SpreadsheetPercentSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPlusSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPowerParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetPowerSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetRowReferenceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetSecondsParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetSubtractionParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTextLiteralParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTextParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetTimeParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetValueSeparatorSymbolParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetWhitespaceParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetYearParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A {@link SpreadsheetParserTokenVisitor} that captures some comment functionality required to visit and change a {@link SpreadsheetParserToken}
 */
abstract class BasicSpreadsheetEngineSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor {

    /**
     * Package private ctor use static method.
     */
    BasicSpreadsheetEngineSpreadsheetParserTokenVisitor() {
        super();
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetAdditionParserToken token) {
        this.exit(token, SpreadsheetParserToken::addition);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetCellRangeParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetCellRangeParserToken token) {
        this.exit(token, SpreadsheetParserToken::cellRange);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetCellReferenceParserToken token) {
        this.exit(token, SpreadsheetParserToken::cellReference);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetDateParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetDateParserToken token) {
        this.exit(token, SpreadsheetParserToken::date);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetDateTimeParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetDateTimeParserToken token) {
        this.exit(token, SpreadsheetParserToken::dateTime);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetDivisionParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetDivisionParserToken token) {
        this.exit(token, SpreadsheetParserToken::division);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::equalsParserToken);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetExpressionParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetExpressionParserToken token) {
        this.exit(token, SpreadsheetParserToken::expression);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGreaterThanParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetGreaterThanParserToken token) {
        this.exit(token, SpreadsheetParserToken::greaterThan);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::greaterThanEquals);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetGroupParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetGroupParserToken token) {
        this.exit(token, SpreadsheetParserToken::group);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetLessThanParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetLessThanParserToken token) {
        this.exit(token, SpreadsheetParserToken::lessThan);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetLessThanEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::lessThanEquals);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetMultiplicationParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetMultiplicationParserToken token) {
        this.exit(token, SpreadsheetParserToken::multiplication);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNamedFunctionParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetNamedFunctionParserToken token) {
        this.exit(token, SpreadsheetParserToken::namedFunction);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNegativeParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetNegativeParserToken token) {
        this.exit(token, SpreadsheetParserToken::negative);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNotEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetNotEqualsParserToken token) {
        this.exit(token, SpreadsheetParserToken::notEquals);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetNumberParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetNumberParserToken token) {
        this.exit(token, SpreadsheetParserToken::number);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetPowerParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetPowerParserToken token) {
        this.exit(token, SpreadsheetParserToken::power);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetSubtractionParserToken token) {
        this.exit(token, SpreadsheetParserToken::subtraction);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetTextParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetTextParserToken token) {
        this.exit(token, SpreadsheetParserToken::text);
    }

    @Override
    protected final Visiting startVisit(final SpreadsheetTimeParserToken token) {
        return this.enter();
    }

    @Override
    protected final void endVisit(final SpreadsheetTimeParserToken token) {
        this.exit(token, SpreadsheetParserToken::time);
    }

    // leaf ......................................................................................................

    @Override
    protected final void visit(final SpreadsheetAmPmParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetApostropheSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetBetweenSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetColumnReferenceParserToken token) {
        this.leaf(this.visitColumn(token));
    }

    abstract Optional<SpreadsheetColumnReferenceParserToken> visitColumn(final SpreadsheetColumnReferenceParserToken token);

    @Override
    protected final void visit(final SpreadsheetCurrencySymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetDayNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetDayNameAbbreviationParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetDayNumberParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetDecimalSeparatorSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetDigitsParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetDivideSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetDoubleQuoteSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetExponentSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetFunctionNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetGreaterThanSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetGreaterThanEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetGroupSeparatorSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetHourParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLabelNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLessThanSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetLessThanEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMillisecondParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMinusSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMinuteParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMonthNameParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMonthNameAbbreviationParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMonthNameInitialParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMonthNumberParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetMultiplySymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetNotEqualsSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetParenthesisCloseSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetParenthesisOpenSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetPercentSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetPlusSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetPowerSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetRowReferenceParserToken token) {
        this.leaf(this.visitRow(token));
    }

    abstract Optional<SpreadsheetRowReferenceParserToken> visitRow(final SpreadsheetRowReferenceParserToken token);

    @Override
    protected final void visit(final SpreadsheetSecondsParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetTextLiteralParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetValueSeparatorSymbolParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetWhitespaceParserToken token) {
        this.leaf(token);
    }

    @Override
    protected final void visit(final SpreadsheetYearParserToken token) {
        this.leaf(token);
    }

    // helpers..........................................................................................................

    @SuppressWarnings("SameReturnValue")
    private Visiting enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();
        this.enter0();

        return Visiting.CONTINUE;
    }

    abstract void enter0();

    private <PP extends SpreadsheetParentParserToken> void exit(final PP parent,
                                                                final BiFunction<List<ParserToken>, String, PP> factory) {
        final List<ParserToken> children = this.children;
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
        this.add(this.exit0(parent, children, factory));
    }

    abstract <PP extends SpreadsheetParentParserToken> SpreadsheetParserToken exit0(final PP parent,
                                                                                    final List<ParserToken> children,
                                                                                    final BiFunction<List<ParserToken>, String, PP> factory);

    abstract void leaf(final Optional<? extends SpreadsheetParserToken> token);

    final void leaf(final ParserToken token) {
        this.add(token);
    }

    final void add(final ParserToken child) {
        Objects.requireNonNull(child, "child");
        this.children.add(child);
    }

    Stack<List<ParserToken>> previousChildren = Stacks.arrayList();

    List<ParserToken> children = Lists.array();
}
