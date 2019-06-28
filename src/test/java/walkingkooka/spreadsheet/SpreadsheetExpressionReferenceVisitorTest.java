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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.Visiting;
import walkingkooka.tree.visit.Visitor;
import walkingkooka.tree.visit.VisitorTesting;
import walkingkooka.type.JavaVisibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionReferenceVisitorTest implements VisitorTesting<SpreadsheetExpressionReferenceVisitor, ExpressionReference> {

    @Test
    public void testExpressionReferenceNotSpreadsheetExpressionReferenceFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.createVisitor().accept(new ExpressionReference() {
            });
        });
    }

    @Test
    public void testAcceptSpreadsheetCellReference() {
        this.createVisitor().accept(SpreadsheetExpressionReference.parseCellReference("A1"));
    }

    @Test
    public void testAcceptSpreadsheetLabelName() {
        this.createVisitor().accept(SpreadsheetExpressionReference.labelName("Label123"));
    }

    @Test
    public void testAcceptSpreadsheetRange() {
        this.createVisitor().accept(SpreadsheetExpressionReference.parseRange("A1:B2"));
    }

    @Test
    public void testStartVisitExpressionReferenceSkip() {
        final StringBuilder b = new StringBuilder();

        final SpreadsheetCellReference cell = SpreadsheetExpressionReference.parseCellReference("A1");
        new FakeSpreadsheetExpressionReferenceVisitor() {
            @Override
            protected Visiting startVisit(final ExpressionReference reference) {
                assertSame(cell, reference);
                b.append("1");
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final ExpressionReference reference) {
                assertSame(cell, reference);
                b.append("2");
            }
        }.accept(cell);

        assertEquals("12", b.toString());
    }

    @Test
    public void testStartVisitSpreadsheetExpressionReferenceSkip() {
        final StringBuilder b = new StringBuilder();

        final SpreadsheetCellReference cell = SpreadsheetExpressionReference.parseCellReference("A1");
        new FakeSpreadsheetExpressionReferenceVisitor() {
            @Override
            protected Visiting startVisit(final ExpressionReference reference) {
                assertSame(cell, reference);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExpressionReference reference) {
                assertSame(cell, reference);
                b.append("2");
            }

            @Override
            protected Visiting startVisit(final SpreadsheetExpressionReference reference) {
                assertSame(cell, reference);
                b.append("3");
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final SpreadsheetExpressionReference reference) {
                assertSame(cell, reference);
                b.append("4");
            }
        }.accept(cell);

        assertEquals("1342", b.toString());
    }

    @Override
    public void testCheckToStringOverridden() {
    }

    @Override
    public SpreadsheetExpressionReferenceVisitor createVisitor() {
        return new SpreadsheetExpressionReferenceVisitor() {
        };
    }

    // ClassTesting.........................................................................

    @Override
    public Class<SpreadsheetExpressionReferenceVisitor> type() {
        return SpreadsheetExpressionReferenceVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting.........................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetExpressionReference.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return Visitor.class.getSimpleName();
    }
}
