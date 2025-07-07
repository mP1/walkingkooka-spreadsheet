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

package walkingkooka.spreadsheet.formula.parser;

import walkingkooka.collect.list.Lists;
import walkingkooka.collect.stack.Stack;
import walkingkooka.collect.stack.Stacks;
import walkingkooka.text.cursor.parser.ParserException;
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
 * A {@link SpreadsheetFormulaParserTokenVisitor} that a {@link SpreadsheetFormulaParserToken} into its {@link Expression}.
 */
final class SpreadsheetFormulaParserTokenVisitorToExpression extends SpreadsheetFormulaParserTokenVisitor {

    static Optional<Expression> toExpression(final SpreadsheetFormulaParserToken token,
                                             final ExpressionEvaluationContext context) {
        Objects.requireNonNull(context, "context");

        final SpreadsheetFormulaParserTokenVisitorToExpression visitor = new SpreadsheetFormulaParserTokenVisitorToExpression(context);
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
        throw new ParserException("Expected either 0 or 1 Expressions but got " + count + "=" + nodes);
    }

    // @VisibleForTesting
    SpreadsheetFormulaParserTokenVisitorToExpression(final ExpressionEvaluationContext context) {
        super();
        this.context = context;
    }

    @Override
    protected Visiting startVisit(final AdditionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final AdditionSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::add, token);
    }

    @Override
    protected Visiting startVisit(final BooleanSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final BooleanSpreadsheetFormulaParserToken token) {
        this.exit();
        this.add(
            Expression.value(token.toBoolean()),
            token
        );
    }

    @Override
    protected Visiting startVisit(final CellRangeSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final CellRangeSpreadsheetFormulaParserToken token) {
        this.exitReference(token.toCellRange(), token);
    }

    @Override
    protected Visiting startVisit(final CellSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final CellSpreadsheetFormulaParserToken token) {
        this.exitReference(token.cell(), token);
    }

    @Override
    protected Visiting startVisit(final ConditionRightEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightGreaterThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightGreaterThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightGreaterThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightLessThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightLessThanSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightLessThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightLessThanEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final ConditionRightNotEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void endVisit(final ConditionRightNotEqualsSpreadsheetFormulaParserToken token) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Visiting startVisit(final DateSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DateSpreadsheetFormulaParserToken token) {
        this.exit();
        this.add(
            Expression.value(token.toLocalDate(this.context)),
            token
        );
    }

    @Override
    protected Visiting startVisit(final DateTimeSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DateTimeSpreadsheetFormulaParserToken token) {
        this.exit();
        this.add(
            Expression.value(token.toLocalDateTime(this.context)),
            token
        );
    }

    private final ExpressionEvaluationContext context;

    @Override
    protected Visiting startVisit(final DivisionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final DivisionSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::divide, token);
    }

    @Override
    protected Visiting startVisit(final EqualsSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final EqualsSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::equalsExpression, token);
    }

    @Override
    protected Visiting startVisit(final ExpressionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final ExpressionSpreadsheetFormulaParserToken token) {
        this.exitUnary(token);
    }

    @Override
    protected Visiting startVisit(final GreaterThanSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::greaterThan, token);
    }

    @Override
    protected Visiting startVisit(final GreaterThanEqualsSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GreaterThanEqualsSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::greaterThanEquals, token);
    }

    @Override
    protected Visiting startVisit(final GroupSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final GroupSpreadsheetFormulaParserToken token) {
        this.exitUnary(token);
    }

    @Override
    protected Visiting startVisit(final LambdaFunctionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LambdaFunctionSpreadsheetFormulaParserToken token) {
        final List<Expression> children = this.children;

        final FunctionParametersSpreadsheetFormulaParserToken parametersTokens = token.parameters();
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
    protected Visiting startVisit(final LessThanSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::lessThan, token);
    }

    @Override
    protected Visiting startVisit(final LessThanEqualsSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final LessThanEqualsSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::lessThanEquals, token);
    }

    @Override
    protected Visiting startVisit(final MultiplicationSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final MultiplicationSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::multiply, token);
    }

    @Override
    protected Visiting startVisit(final NamedFunctionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NamedFunctionSpreadsheetFormulaParserToken token) {
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
    protected Visiting startVisit(final NegativeSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NegativeSpreadsheetFormulaParserToken token) {
        this.exitUnary(Expression::negative, token);
    }

    @Override
    protected Visiting startVisit(final NotEqualsSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NotEqualsSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::notEquals, token);
    }

    @Override
    protected Visiting startVisit(final NumberSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final NumberSpreadsheetFormulaParserToken token) {
        this.exit();
        this.add(
            Expression.value(token.toNumber(this.context)),
            token
        );
    }

    @Override
    protected Visiting startVisit(final PowerSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final PowerSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::power, token);
    }

    @Override
    protected Visiting startVisit(final SubtractionSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final SubtractionSpreadsheetFormulaParserToken token) {
        this.exitBinary(Expression::subtract, token);
    }

    @Override
    protected Visiting startVisit(final TextSpreadsheetFormulaParserToken token) {
        this.text = new StringBuilder();
        return this.enter();
    }

    @Override
    protected void endVisit(final TextSpreadsheetFormulaParserToken token) {
        this.exit();
        this.add(
            Expression.value(this.text.toString()),
            token
        );
        this.text = null;
    }

    /**
     * Collects the text within a {@link TextSpreadsheetFormulaParserToken}.
     */
    private StringBuilder text = new StringBuilder();

    @Override
    protected Visiting startVisit(final TimeSpreadsheetFormulaParserToken token) {
        return this.enter();
    }

    @Override
    protected void endVisit(final TimeSpreadsheetFormulaParserToken token) {
        this.exit();
        this.add(
            Expression.value(token.toLocalTime()),
            token
        );
    }

    // visit....................................................................................................
    // ignore all SymbolParserTokens, dont bother to collect them.

    @Override
    protected void visit(final ErrorSpreadsheetFormulaParserToken token) {
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
    protected void visit(final LabelSpreadsheetFormulaParserToken token) {
        this.addReference(
            token.value(),
            token
        );
    }

    @Override
    protected void visit(final TemplateValueNameSpreadsheetFormulaParserToken token) {
        this.addReference(
            token.value(),
            token
        );
    }

    @Override
    protected void visit(final TextLiteralSpreadsheetFormulaParserToken token) {
        this.text.append(token.value());
    }

    // GENERAL PURPOSE .................................................................................................

    @SuppressWarnings("SameReturnValue")
    private Visiting enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();

        return Visiting.CONTINUE;
    }

    private void exitBinary(final BiFunction<Expression, Expression, Expression> factory, final SpreadsheetFormulaParserToken token) {
        final Expression left = this.children.get(0);
        final Expression right = this.children.get(1);
        this.exit();
        this.add(factory.apply(left, right), token);
    }

    private void exitUnary(final ParentSpreadsheetFormulaParserToken token) {
        this.exitUnary(Function.identity(), token);
    }

    private void exitUnary(final Function<Expression, Expression> factory, final SpreadsheetFormulaParserToken token) {
        final Expression parameter = this.children.get(0);
        this.exit();
        this.add(factory.apply(parameter), token);
    }

    private void exit() {
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
    }

    private void exitReference(final ExpressionReference reference,
                               final SpreadsheetFormulaParserToken token) {
        final Expression node = Expression.reference(reference);
        this.exit();
        this.add(node, token);
    }

    private void addReference(final ExpressionReference reference,
                              final SpreadsheetFormulaParserToken token) {
        this.add(
            Expression.reference(reference),
            token
        );
    }

    private void add(final Expression node, final SpreadsheetFormulaParserToken token) {
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
