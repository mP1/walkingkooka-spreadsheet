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

package walkingkooka.spreadsheet.reference;

import walkingkooka.Cast;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.visit.Visiting;
import walkingkooka.visit.Visitor;

import java.util.Objects;

/**
 * A {@link Visitor} for all known implementations of {@link SpreadsheetExpressionReference}.
 */
public abstract class SpreadsheetExpressionReferenceVisitor extends Visitor<ExpressionReference> {

    protected SpreadsheetExpressionReferenceVisitor() {
        super();
    }

    public final void accept(final ExpressionReference reference) {
        Objects.requireNonNull(reference, "reference");

        if (Visiting.CONTINUE == this.startVisit(reference)) {

            if (false == reference instanceof SpreadsheetExpressionReference) {
                throw new IllegalArgumentException("Unknown reference type: " + reference.getClass().getName() + "=" + reference);
            }
            this.traverse(Cast.to(reference));
        }
        this.endVisit(reference);
    }

    protected Visiting startVisit(final ExpressionReference reference) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final ExpressionReference reference) {
        // nop
    }

    private void traverse(final SpreadsheetExpressionReference reference) {
        if (Visiting.CONTINUE == this.startVisit(reference)) {
            reference.accept(this);
        }
        this.endVisit(reference);
    }

    protected Visiting startVisit(final SpreadsheetExpressionReference reference) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetExpressionReference reference) {
        // nop
    }

    protected void visit(final SpreadsheetCellReference reference) {
        // nop
    }

    protected void visit(final SpreadsheetLabelName label) {
        // nop
    }

    protected void visit(final SpreadsheetRange range) {
        // nop
    }
}
