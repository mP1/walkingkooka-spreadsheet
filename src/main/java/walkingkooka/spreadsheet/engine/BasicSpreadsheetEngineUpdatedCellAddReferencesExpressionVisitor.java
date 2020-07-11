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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.ExpressionVisitor;
import walkingkooka.tree.expression.ReferenceExpression;

/**
 * Accepts an {@link Expression} passes all {@link ExpressionReference} to a {@link SpreadsheetExpressionReferenceVisitor}.
 */
final class BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionVisitor extends ExpressionVisitor {

    static void processReferences(final Expression node,
                                  final SpreadsheetCellReference target,
                                  final BasicSpreadsheetEngine engine) {
        new BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionVisitor(target, engine).accept(node);
    }

    // VisibleForTesting
    BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionVisitor(final SpreadsheetCellReference target,
                                                                    final BasicSpreadsheetEngine engine) {
        super();
        this.target = target;
        this.engine = engine;
    }

    @Override
    protected void visit(final ReferenceExpression node) {
        if (null == this.visitor) {
            this.visitor = BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionVisitorSpreadsheetExpressionReferenceVisitor.with(this.target,
                    this.engine);
        }
        this.visitor.accept(node.value());
    }

    /**
     * The target cell.
     */
    private final SpreadsheetCellReference target;

    /**
     * The engine holds stores which will have references to this cell updated.
     */
    private final BasicSpreadsheetEngine engine;

    /**
     * Cache of the {@link BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionVisitorSpreadsheetExpressionReferenceVisitor} that will process each and every encountered {@link ReferenceExpression}.
     */
    private BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionVisitorSpreadsheetExpressionReferenceVisitor visitor;

    @Override
    public String toString() {
        return this.target.toString();
    }
}
