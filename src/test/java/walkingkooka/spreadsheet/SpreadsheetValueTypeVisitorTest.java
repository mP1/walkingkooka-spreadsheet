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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReferenceOrRange;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.visit.Visiting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertSame;

public class SpreadsheetValueTypeVisitorTest implements SpreadsheetValueTypeVisitorTesting<SpreadsheetValueTypeVisitor> {

    @Test
    public void testStartVisitSkip() {
        final StringBuilder b = new StringBuilder();
        final Class<?> type = this.getClass();

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

        }.accept(type);

        this.checkEquals("12", b.toString());
    }

    @Test
    public void testAcceptBigDecimal() {
        final StringBuilder b = new StringBuilder();
        final Class<BigDecimal> type = BigDecimal.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitBigDecimal() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptBigDecimal2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(BigDecimal.class);
    }

    @Test
    public void testAcceptBigInteger() {
        final StringBuilder b = new StringBuilder();
        final Class<BigInteger> type = BigInteger.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitBigInteger() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptBigInteger2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(BigInteger.class);
    }

    @Test
    public void testAcceptBoolean() {
        final StringBuilder b = new StringBuilder();
        final Class<Boolean> type = Boolean.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitBoolean() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptBoolean2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Boolean.class);
    }

    @Test
    public void testAcceptByte() {
        final StringBuilder b = new StringBuilder();
        final Class<Byte> type = Byte.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitByte() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptByte2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Byte.class);
    }

    @Test
    public void testAcceptCellRange() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetCellRangeReference> type = SpreadsheetCellRangeReference.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitCellRange() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptCellRange2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(SpreadsheetCellRangeReference.class);
    }

    @Test
    public void testAcceptCellReference() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetCellReference> type = SpreadsheetCellReference.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitCellReference() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptCellReference2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(SpreadsheetCellReference.class);
    }

    @Test
    public void testAcceptCellReferenceOrRange() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetCellReferenceOrRange> type = SpreadsheetCellReferenceOrRange.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitCellReferenceOrRange() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptCellReferenceOrRange2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(SpreadsheetCellReferenceOrRange.class);
    }

    @Test
    public void testAcceptCharacter() {
        final StringBuilder b = new StringBuilder();
        final Class<Character> type = Character.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitCharacter() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptCharacter2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Character.class);
    }

    @Test
    public void testAcceptColumnReference() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetColumnReference> type = SpreadsheetColumnReference.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitColumnReference() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptColumnReference2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(SpreadsheetColumnReference.class);
    }

    @Test
    public void testAcceptColumnOrRowReferenceOrRange() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetColumnOrRowReferenceOrRange> type = SpreadsheetColumnOrRowReferenceOrRange.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitColumnOrRowReferenceOrRange() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptColumnRangeReference() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetColumnRangeReference> type = SpreadsheetColumnRangeReference.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitColumnRangeReference() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptColumnRangeReference2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(SpreadsheetColumnRangeReference.class);
    }

    @Test
    public void testAcceptDouble() {
        final StringBuilder b = new StringBuilder();
        final Class<Double> type = Double.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitDouble() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptDouble2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Double.class);
    }

    @Test
    public void testExpressionNumber() {
        this.acceptExpressionNumber(ExpressionNumber.class);
    }

    @Test
    public void testExpressionNumberBigDecimal() {
        this.acceptExpressionNumber(
            ExpressionNumberKind.BIG_DECIMAL.zero()
                .getClass()
        );
    }

    @Test
    public void testExpressionNumberDouble() {
        this.acceptExpressionNumber(
            ExpressionNumberKind.DOUBLE.zero()
                .getClass()
        );
    }

    private void acceptExpressionNumber(final Class<? extends ExpressionNumber> number) {
        final StringBuilder b = new StringBuilder();

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(number, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(number, t);
                b.append("2");
            }

            @Override
            protected void visitExpressionNumber() {
                b.append("3");
            }
        }.accept(number);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptExpressionReference() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetExpressionReference> type = SpreadsheetExpressionReference.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitExpressionReference() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptExpressionReference2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(SpreadsheetExpressionReference.class);
    }

    @Test
    public void testAcceptFloat() {
        final StringBuilder b = new StringBuilder();
        final Class<Float> type = Float.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitFloat() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptFloat2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Float.class);
    }

    @Test
    public void testAcceptInteger() {
        final StringBuilder b = new StringBuilder();
        final Class<Integer> type = Integer.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitInteger() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptInteger2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Integer.class);
    }

    @Test
    public void testAcceptLabel() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetLabelName> type = SpreadsheetLabelName.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitLabel() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLabel2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(SpreadsheetLabelName.class);
    }

    @Test
    public void testAcceptLocalDate() {
        final StringBuilder b = new StringBuilder();
        final Class<LocalDate> type = LocalDate.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitLocalDate() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLocalDate2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(LocalDate.class);
    }

    @Test
    public void testAcceptLocalDateTime() {
        final StringBuilder b = new StringBuilder();
        final Class<LocalDateTime> type = LocalDateTime.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitLocalDateTime() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLocalDateTime2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(LocalDateTime.class);
    }

    @Test
    public void testAcceptLocalTime() {
        final StringBuilder b = new StringBuilder();
        final Class<LocalTime> type = LocalTime.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitLocalTime() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLocalTime2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(LocalTime.class);
    }

    @Test
    public void testAcceptLong() {
        final StringBuilder b = new StringBuilder();
        final Class<Long> type = Long.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitLong() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLong2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Long.class);
    }

    @Test
    public void testAcceptNumber() {
        final StringBuilder b = new StringBuilder();
        final Class<Number> type = Number.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitNumber() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptNumber2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Number.class);
    }

    @Test
    public void testAcceptRowReferenceOrRange() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetRowReferenceOrRange> type = SpreadsheetRowReferenceOrRange.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitRowReferenceOrRange() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptRowRangeReference() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetRowRangeReference> type = SpreadsheetRowRangeReference.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitRowRangeReference() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptRowRangeReference2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(SpreadsheetRowRangeReference.class);
    }

    @Test
    public void testAcceptShort() {
        final StringBuilder b = new StringBuilder();
        final Class<Short> type = Short.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitShort() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptShort2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Short.class);
    }

    @Test
    public void testAcceptSpreadsheetError() {
        final StringBuilder b = new StringBuilder();
        final Class<SpreadsheetError> type = SpreadsheetError.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitSpreadsheetError() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptSpreadsheetError2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(SpreadsheetError.class);
    }

    @Test
    public void testAcceptString() {
        final StringBuilder b = new StringBuilder();
        final Class<String> type = String.class;

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitString() {
                b.append("3");
            }
        }.accept(type);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptString2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(String.class);
    }

    @Test
    public void testAcceptUnknown() {
        final StringBuilder b = new StringBuilder();
        final Class<?> type = this.getClass();

        new FakeSpreadsheetValueTypeVisitor() {
            @Override
            protected Visiting startVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Class<?> t) {
                assertSame(type, t);
                b.append("2");
            }

            @Override
            protected void visitUnknown(final String typeName) {
                b.append("3")
                    .append(typeName);
            }
        }.accept(type);

        this.checkEquals(
            "13" + type.getName() + "2",
            b.toString()
        );
    }

    @Test
    public void testAcceptUnknown2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(this.getClass());
    }

    @Override
    public void testCheckToStringOverridden() {
    }

    @Override
    public SpreadsheetValueTypeVisitor createVisitor() {
        return new FakeSpreadsheetValueTypeVisitor();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetValueTypeVisitor> type() {
        return SpreadsheetValueTypeVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return "";
    }
}
