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
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionEvaluationContext;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.visit.Visiting;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link SpreadsheetParserTokenVisitor} that a {@link SpreadsheetParserToken} into its {@link Expression}.
 */
final class SpreadsheetParserTokenVisitorToExpression extends SpreadsheetParserTokenVisitor {

    static Optional<Expression> toExpression(final SpreadsheetParserToken token,
                                             final ExpressionEvaluationContext context) {
        Objects.requireNonNull(context, "context");

        final SpreadsheetParserTokenVisitorToExpression visitor = new SpreadsheetParserTokenVisitorToExpression(context);
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
    SpreadsheetParserTokenVisitorToExpression(final ExpressionEvaluationContext context) {
        super();
        this.context = context;
    }

    @Override
    protected Visiting startVisit(final AdditionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final AdditionSpreadsheetParserToken token) {
        this.exitBinary(Expression::add, token);
    }

    @Override
    protected Visiting startVisit(final CellRangeSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final CellRangeSpreadsheetParserToken token) {
        this.exitReference(token.toCellRange(), token);
    }

    @Override
    protected Visiting startVisit(final CellReferenceSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final CellReferenceSpreadsheetParserToken token) {
        this.exitReference(token.cell(), token);
    }

    @Override
    protected Visiting startVisit(final ConditionRightEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightGreaterThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightGreaterThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightGreaterThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightGreaterThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightLessThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightLessThanSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightLessThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightLessThanEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightNotEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightNotEqualsSpreadsheetParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final DateSpreadsheetParserToken token) {
        this.enter();
        return Visiting.SKIP;
    }

    @Override
    protected void endVisit(final DateSpreadsheetParserToken token) {
        this.exit();
        this.add(
                Expression.value(token.toLocalDate(this.context)),
                token
        );
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetParserToken token) {
        this.enter();
        return Visiting.SKIP;
    }

    @Override
    protected void endVisit(final DateTimeSpreadsheetParserToken token) {
        this.exit();
        this.add(
                Expression.value(token.toLocalDateTime(this.context)),
                token
        );
    }

    private final ExpressionEvaluationContext context;

    @Override
    protected Visiting startVisit(final DivisionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DivisionSpreadsheetParserToken token) {
        this.exitBinary(Expression::divide, token);
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final EqualsSpreadsheetParserToken token) {
        this.exitBinary(Expression::equalsExpression, token);
    }

    @Override
    protected Visiting startVisit(final ExpressionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final ExpressionSpreadsheetParserToken token) {
        this.exitUnary(token);
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanSpreadsheetParserToken token) {
        this.exitBinary(Expression::greaterThan, token);
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanEqualsSpreadsheetParserToken token) {
        this.exitBinary(Expression::greaterThanEquals, token);
    }

    @Override
    protected Visiting startVisit(final GroupSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GroupSpreadsheetParserToken token) {
        this.exitUnary(token);
    }

    @Override
    protected Visiting startVisit(final LambdaFunctionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LambdaFunctionSpreadsheetParserToken token) {
        final List<Expression> children = this.children;

        final FunctionParametersSpreadsheetParserToken parametersTokens = token.parameters();
        final int parameterCount = parametersTokens.parameters().size();

        final Expression call = Expression.call(
                Expression.call(
                        Expression.namedFunction(
                                token.functionName()
                                        .toExpressionFunctionName()
                        ),
                        children.subList(0, parameterCount)
                ),
                children.subList(parameterCount, children.size())
        );

        this.exit();
        this.add(call, token);
    }

    @Override
    protected Visiting startVisit(final LessThanSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanSpreadsheetParserToken token) {
        this.exitBinary(Expression::lessThan, token);
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanEqualsSpreadsheetParserToken token) {
        this.exitBinary(Expression::lessThanEquals, token);
    }

    @Override
    protected Visiting startVisit(final MultiplicationSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final MultiplicationSpreadsheetParserToken token) {
        this.exitBinary(Expression::multiply, token);
    }

    @Override
    protected Visiting startVisit(final NamedFunctionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NamedFunctionSpreadsheetParserToken token) {
        final Expression callNamedFunction = Expression.call(
                Expression.namedFunction(
                        token.functionName()
                                .toExpressionFunctionName()
                ),
                this.children
        );
        this.exit();
        this.add(callNamedFunction, token);
    }

    @Override
    protected Visiting startVisit(final NegativeSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NegativeSpreadsheetParserToken token) {
        this.exitUnary(Expression::negative, token);
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NotEqualsSpreadsheetParserToken token) {
        this.exitBinary(Expression::notEquals, token);
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NumberSpreadsheetParserToken token) {
        this.exit();
        this.add(
                Expression.value(token.toNumber(this.context)),
                token
        );
    }

    @Override
    protected Visiting startVisit(final PowerSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final PowerSpreadsheetParserToken token) {
        this.exitBinary(Expression::power, token);
    }

    @Override
    protected Visiting startVisit(final SubtractionSpreadsheetParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SubtractionSpreadsheetParserToken token) {
        this.exitBinary(Expression::subtract, token);
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetParserToken token) {
        this.text = new StringBuilder();
        return this.enter();
    }

    @Override
    protected void endVisit(final TextSpreadsheetParserToken token) {
        this.exit();
        this.add(
                Expression.value(this.text.toString()),
                token
        );
        this.text = null;
    }

    /**
     * Collects the text within a {@link TextSpreadsheetParserToken}.
     */
    private StringBuilder text = new StringBuilder();

    @Override
    protected Visiting startVisit(final TimeSpreadsheetParserToken token) {
        this.enter();
        return Visiting.SKIP;
    }

    @Override
    protected void endVisit(final TimeSpreadsheetParserToken token) {
        this.exit();
        this.add(
                Expression.value(token.toLocalTime()),
                token
        );
    }

    // visit....................................................................................................
    // ignore all SymbolParserTokens, dont bother to collect them.

    @Override
    protected void visit(final ErrorSpreadsheetParserToken token) {
        this.add(
                Expression.value(
                        token.value()
                                .kind()
                                .toError()
                ),
                token
        );
    }

    @Override
    protected void visit(final LabelNameSpreadsheetParserToken token) {
        this.addReference(token.value(), token);
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetParserToken token) {
        this.text.append(token.value());
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

    private void exitUnary(final ParentSpreadsheetParserToken token) {
        this.exitUnary(Function.identity(), token);
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
