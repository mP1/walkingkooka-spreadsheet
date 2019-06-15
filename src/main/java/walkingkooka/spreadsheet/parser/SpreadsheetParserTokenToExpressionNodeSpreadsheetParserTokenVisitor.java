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
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.Visiting;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@link SpreadsheetParserTokenVisitor} that a {@link SpreadsheetParserToken} into its {@link ExpressionNode}.
 */
final class SpreadsheetParserTokenToExpressionNodeSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor {

    static Optional<ExpressionNode> accept(final SpreadsheetParserToken token) {
        final SpreadsheetParserTokenToExpressionNodeSpreadsheetParserTokenVisitor visitor = new SpreadsheetParserTokenToExpressionNodeSpreadsheetParserTokenVisitor();
        token.accept(visitor);

        final List<ExpressionNode> nodes = visitor.children;
        final int count = nodes.size();
        return count == 1 ?
                Optional.of(nodes.get(0)) :
                count == 0 ?
                        Optional.empty() :
                        fail(count, nodes);
    }

    private static Optional<ExpressionNode> fail(final int count, final List<ExpressionNode> nodes) {
        throw new SpreadsheetParserException("Expected either 0 or 1 ExpressionNodes but got " + count + "=" + nodes);
    }

    // @VisibleForTesting
    SpreadsheetParserTokenToExpressionNodeSpreadsheetParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetAdditionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetAdditionParserToken token) {
        this.exitBinary(ExpressionNode::addition, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetCellReferenceParserToken token) {
        this.exitReference(token.cell(), token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetDivisionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetDivisionParserToken token) {
        this.exitBinary(ExpressionNode::division, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetEqualsParserToken token) {
        this.exitBinary(ExpressionNode::equalsNode, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFunctionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetFunctionParserToken token) {
        final ExpressionNode function = ExpressionNode.function(
                ExpressionNodeName.with(token.functionName().value()),
                this.children);
        this.exit();
        this.add(function, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGreaterThanParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetGreaterThanParserToken token) {
        this.exitBinary(ExpressionNode::greaterThan, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetGreaterThanEqualsParserToken token) {
        this.exitBinary(ExpressionNode::greaterThanEquals, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetGroupParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetGroupParserToken token) {
        final ExpressionNode parameter = this.children.get(0);
        this.exit();
        this.add(parameter, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetLessThanParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetLessThanParserToken token) {
        this.exitBinary(ExpressionNode::lessThan, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetLessThanEqualsParserToken token) {
        this.exitBinary(ExpressionNode::lessThanEquals, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetMultiplicationParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetMultiplicationParserToken token) {
        this.exitBinary(ExpressionNode::multiplication, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNegativeParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetNegativeParserToken token) {
        this.exitUnary(ExpressionNode::negative, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetNotEqualsParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetNotEqualsParserToken token) {
        this.exitBinary(ExpressionNode::notEquals, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetPercentageParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    /**
     * Replace the percentage and value with a multiply value by 100.
     */
    @Override
    protected void endVisit(final SpreadsheetPercentageParserToken token) {
        final ExpressionNode parameter = this.children.get(0);
        this.exit();
        this.add(ExpressionNode.multiplication(parameter, ExpressionNode.longNode(100L)), token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetPowerParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetPowerParserToken token) {
        this.exitBinary(ExpressionNode::power, token);
    }

    @Override
    protected Visiting startVisit(final SpreadsheetRangeParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetRangeParserToken token) {
        this.exit();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetSubtractionParserToken token) {
        this.enter();
        return super.startVisit(token);
    }

    @Override
    protected void endVisit(final SpreadsheetSubtractionParserToken token) {
        this.exitBinary(ExpressionNode::subtraction, token);
    }

    // visit....................................................................................................
    // ignore all SymbolParserTokens, dont bother to collect them.

    @Override
    protected void visit(final SpreadsheetBigDecimalParserToken token) {
        this.add(ExpressionNode.bigDecimal(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetBigIntegerParserToken token) {
        this.add(ExpressionNode.bigInteger(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetDoubleParserToken token) {
        this.add(ExpressionNode.doubleNode(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetLabelNameParserToken token) {
        this.addReference(token.value(), token);
    }

    @Override
    protected void visit(final SpreadsheetLocalDateParserToken token) {
        this.add(ExpressionNode.localDate(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetLocalDateTimeParserToken token) {
        this.add(ExpressionNode.localDateTime(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetLocalTimeParserToken token) {
        this.add(ExpressionNode.localTime(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetLongParserToken token) {
        this.add(ExpressionNode.longNode(token.value()), token);
    }

    @Override
    protected void visit(final SpreadsheetTextParserToken token) {
        this.add(ExpressionNode.text(token.value()), token);
    }

    // GENERAL PURPOSE .................................................................................................

    private void enter() {
        this.previousChildren = this.previousChildren.push(this.children);
        this.children = Lists.array();
    }

    private void exitBinary(final BiFunction<ExpressionNode, ExpressionNode, ExpressionNode> factory, final SpreadsheetParserToken token) {
        final ExpressionNode left = this.children.get(0);
        final ExpressionNode right = this.children.get(1);
        this.exit();
        this.add(factory.apply(left, right), token);
    }

    private void exitUnary(final Function<ExpressionNode, ExpressionNode> factory, final SpreadsheetParserToken token) {
        final ExpressionNode parameter = this.children.get(0);
        this.exit();
        this.add(factory.apply(parameter), token);
    }

    private void exit() {
        this.children = this.previousChildren.peek();
        this.previousChildren = this.previousChildren.pop();
    }

    private void exitReference(final ExpressionReference reference, final SpreadsheetParserToken token) {
        final ExpressionNode node = ExpressionNode.reference(reference);
        this.exit();
        this.add(node, token);
    }

    private void addReference(final ExpressionReference reference, final SpreadsheetParserToken token) {
        final ExpressionNode node = ExpressionNode.reference(reference);
        this.add(node, token);
    }

    private void add(final ExpressionNode node, final SpreadsheetParserToken token) {
        if (null == node) {
            throw new NullPointerException("Null node returned for " + token);
        }
        this.children.add(node);
    }

    private Stack<List<ExpressionNode>> previousChildren = Stacks.arrayList();

    private List<ExpressionNode> children = Lists.array();

    public String toString() {
        return this.children + "," + this.previousChildren;
    }
}
