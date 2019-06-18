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

public final class SpreadsheetExpressionReferenceVisitorTest implements VisitorTesting<SpreadsheetExpressionReferenceVisitor, ExpressionReference> {

    @Test
    public void testStartVisitSkip() {
        final StringBuilder b = new StringBuilder();

        final SpreadsheetCellReference label = SpreadsheetExpressionReference.parseCellReference("A1");
        new FakeSpreadsheetExpressionReferenceVisitor() {
            @Override
            protected Visiting startVisit(final ExpressionReference reference) {
                assertSame(label, reference);
                b.append("1");
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final ExpressionReference reference) {
                assertSame(label, reference);
                b.append("2");
            }
        }.accept(label);

        assertEquals("12", b.toString());
    }

    @Test
    public void testAcceptSpreadsheetCellReference() {
        final StringBuilder b = new StringBuilder();

        final SpreadsheetCellReference label = SpreadsheetExpressionReference.parseCellReference("A1");
        new FakeSpreadsheetExpressionReferenceVisitor() {
            @Override
            protected Visiting startVisit(final ExpressionReference reference) {
                assertSame(label, reference);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExpressionReference reference) {
                assertSame(label, reference);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetCellReference l) {
                assertSame(label, l);
                b.append("3");
            }

        }.accept(label);

        assertEquals("132", b.toString());
    }

    @Test
    public void testAcceptSpreadsheetLabelName() {
        final StringBuilder b = new StringBuilder();

        final SpreadsheetLabelName label = SpreadsheetExpressionReference.labelName("label");
        new FakeSpreadsheetExpressionReferenceVisitor() {
            @Override
            protected Visiting startVisit(final ExpressionReference reference) {
                assertSame(label, reference);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExpressionReference reference) {
                assertSame(label, reference);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetLabelName l) {
                assertSame(label, l);
                b.append("3");
            }

        }.accept(label);

        assertEquals("132", b.toString());
    }

    @Test
    public void testAcceptSpreadsheetRange() {
        final StringBuilder b = new StringBuilder();

        final SpreadsheetRange range = SpreadsheetRange.parse("A1:B2");

        new FakeSpreadsheetExpressionReferenceVisitor() {
            @Override
            protected Visiting startVisit(final ExpressionReference reference) {
                assertSame(range, reference);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final ExpressionReference reference) {
                assertSame(range, reference);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetRange r) {
                assertSame(range, r);
                b.append("3");
            }

        }.accept(range);

        assertEquals("132", b.toString());
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
