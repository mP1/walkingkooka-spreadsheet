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

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.FunctionExpressionName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link SpreadsheetParserTokenVisitor} that a {@link SpreadsheetParserToken} into its {@link Expression}.
 */
final class SpreadsheetParserTokenToExpressionSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor {

    static Optional<Expression> accept(final SpreadsheetParserToken token) {
        final SpreadsheetParserTokenToExpressionSpreadsheetParserTokenVisitor visitor = new SpreadsheetParserTokenToExpressionSpreadsheetParserTokenVisitor();
        token.accept(visitor);

        final List<Expression> nodes = visitor.children;
        final int count = nodes.size();
        return count == 1 ?
                Optional.of(nodes.get(0)) :
                count == 0 ?
                        Optional.empty() :
                        fail(count, nodes);
    }

    private static Optional<Expression> fail(final int count, final List<Expression> nodes) {
        throw new SpreadsheetParserException("Expected either 0 or 1 Expressions but got " + count + "=" + nodes);
    }

    // @VisibleForTesting
    SpreadsheetParserTokenToExpressionSpreadsheetParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetAdditionParserToken token) {
        this.exitBinary(Expression::add, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetCellReferenceParserToken token) {
        this.exitReference(token.cell(), token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetDivisionParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetDivisionParserToken token) {
        this.exitBinary(Expression::divide, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetEqualsParserToken token) {
        this.exitBinary(Expression::equalsExpression, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFunctionParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetFunctionParserToken token) {
        final Expression function = Expression.function(
                FunctionExpressionName.with(token.functionName().value()),
                this.children);
        this.exit();
        this.add(function, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGreaterThanParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetGreaterThanParserToken token) {
        this.exitBinary(Expression::greaterThan, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.exitBinary(Expression::greaterThanEquals, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGroupParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetGroupParserToken token) {
        final Expression parameter = this.children.get(0);
        this.exit();
        this.add(parameter, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetLessThanParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetLessThanParserToken token) {
        this.exitBinary(Expression::lessThan, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetLessThanEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.exitBinary(Expression::lessThanEquals, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetMultiplicationParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetMultiplicationParserToken token) {
        this.exitBinary(Expression::multiply, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNegativeParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetNegativeParserToken token) {
        this.exitUnary(Expression::negative, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNotEqualsParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetNotEqualsParserToken token) {
        this.exitBinary(Expression::notEquals, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetPercentageParserToken token) {
        return this.enter();
    }

    /**
     * Replace the percentage and value with a multiply value by 100.
     */
    @Override
    protected void endVisit(final SpreadsheetPercentageParserToken token) {
        final Expression parameter = this.children.get(0);
        this.exit();
        this.add(Expression.divide(parameter, Expression.longExpression(100L)), token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetPowerParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetPowerParserToken token) {
        this.exitBinary(Expression::power, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetRangeParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetRangeParserToken token) {
        this.exit();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SpreadsheetSubtractionParserToken token) {
        this.exitBinary(Expression::subtract, token);
    }

    // visit....................................................................................................
    // ignore all SymbolParserTokens, dont bother to collect them.

    @Override
    protected void visit(final SpreadsheetBigDecimalParserToken token) {
        this.add(Expression.bigDecimal(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetBigIntegerParserToken token) {
        this.add(Expression.bigInteger(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetDoubleParserToken token) {
        this.add(Expression.doubleExpression(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetLabelNameParserToken token) {
        this.addReference(token.value(), token);
    }

    @Override
    protected void visit(final SpreadsheetLocalDateParserToken token) {
        this.add(Expression.localDate(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetLocalDateTimeParserToken token) {
        this.add(Expression.localDateTime(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetLocalTimeParserToken token) {
        this.add(Expression.localTime(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetLongParserToken token) {
        this.add(Expression.longExpression(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetTextParserToken token) {
        this.add(Expression.string(token.value()), token);
    }

    // GENERAL PURPOSE .................................................................................................

    @SuppressWarnings("SameReturnValue")
    private Visiting enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();

        return Visiting.CONTINUE;
    }

    private void exitBinary(final BiFunction<Expression, Expression, Expression> factory, final SpreadsheetParserToken token) {
        final Expression left = this.children.get(0);
        final Expression right = this.children.get(1);
        this.exit();
        this.add(factory.apply(left, right), token);
    }

    private void exitUnary(final Function<Expression, Expression> factory, final SpreadsheetParserToken token) {
        final Expression parameter = this.children.get(0);
        this.exit();
        this.add(factory.apply(parameter), token);
    }

    private void exit() {
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
    }

    private void exitReference(final ExpressionReference reference, final SpreadsheetParserToken token) {
        final Expression node = Expression.reference(reference);
        this.exit();
        this.add(node, token);
    }

    private void addReference(final ExpressionReference reference, final SpreadsheetParserToken token) {
        this.add(Expression.reference(reference), token);
    }

    private void add(final Expression node, final SpreadsheetParserToken token) {
        if (null == node) {
            throw new NullPointerException("Null node returned for " + token);
        }
        this.children.add(node);
    }

    private Stack<List<Expression>> previousChildren = Stacks.arrayList();

    private List<Expression> children = Lists.array();

    @Override
    public String toString() {
        return this.children + "," + this.previousChildren;
    }
}
