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

package walkingkooka.spreadsheet.value;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.map.Maps;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionNumber;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.visit.Visiting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetValueVisitorTest implements SpreadsheetValueVisitorTesting<SpreadsheetValueVisitor> {

    private final static ExpressionNumberKind EXPRESSION_NUMBER_KIND = ExpressionNumberKind.DEFAULT;

    @Test
    public void testStartVisitSkip() {
        final StringBuilder b = new StringBuilder();
        final String value = "ab123";

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.SKIP;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

        }.accept(value);

        this.checkEquals("12", b.toString());
    }

    @Test
    public void testAcceptBigDecimal() {
        final StringBuilder b = new StringBuilder();
        final BigDecimal value = BigDecimal.valueOf(123);

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final BigDecimal v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptBigDecimal2() {
        new SpreadsheetValueVisitor() {
        }.accept(BigDecimal.valueOf(2));
    }

    @Test
    public void testAcceptBigInteger() {
        final StringBuilder b = new StringBuilder();
        final BigInteger value = BigInteger.valueOf(123);

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final BigInteger v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptBigInteger2() {
        new SpreadsheetValueVisitor() {
        }.accept(BigInteger.valueOf(2));
    }

    @Test
    public void testAcceptBoolean() {
        final StringBuilder b = new StringBuilder();
        final Boolean value = true;

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final Boolean v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptBoolean2() {
        new SpreadsheetValueVisitor() {
        }.accept(true);
    }

    @Test
    public void testAcceptByte() {
        final StringBuilder b = new StringBuilder();
        final Byte value = Byte.MAX_VALUE;

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final Byte v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptByte2() {
        new SpreadsheetValueVisitor() {
        }.accept(Byte.MAX_VALUE);
    }

    @Test
    public void testAcceptCellRange() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetCellRangeReference value = SpreadsheetSelection.parseCellRange("A1:B2");

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetCellRangeReference v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptCellRange2() {
        new SpreadsheetValueVisitor() {
        }.accept(SpreadsheetSelection.parseCellRange("A1:B2"));
    }

    @Test
    public void testAcceptCellReference() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetCellReference value = SpreadsheetSelection.parseCell("C3");

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetCellReference v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptCellReference2() {
        new SpreadsheetValueVisitor() {
        }.accept(SpreadsheetSelection.parseCell("C3"));
    }

    @Test
    public void testAcceptColumnReference() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetColumnReference value = SpreadsheetSelection.parseColumn("D");

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetColumnReference v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptColumnReference2() {
        new SpreadsheetValueVisitor() {
        }.accept(SpreadsheetSelection.parseColumn("C"));
    }

    @Test
    public void testAcceptColumnRangeReference() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetColumnRangeReference value = SpreadsheetSelection.parseColumnRange("E:F");

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetColumnRangeReference v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptColumnRangeReference2() {
        new SpreadsheetValueVisitor() {
        }.accept(SpreadsheetSelection.parseColumnRange("E:F"));
    }

    @Test
    public void testAcceptDouble() {
        final StringBuilder b = new StringBuilder();
        final Double value = 123.5;

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final Double v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptDouble2() {
        new SpreadsheetValueVisitor() {
        }.accept(2.0);
    }

    @Test
    public void testAcceptExpressionNumber() {
        final StringBuilder b = new StringBuilder();
        final ExpressionNumber value = EXPRESSION_NUMBER_KIND.create(123);

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final ExpressionNumber v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptExpressionNumber2() {
        new SpreadsheetValueVisitor() {
        }.accept(EXPRESSION_NUMBER_KIND.create(2));
    }

    @Test
    public void testAcceptFloat() {
        final StringBuilder b = new StringBuilder();
        final Float value = Float.MAX_VALUE;

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final Float v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptFloat2() {
        new SpreadsheetValueVisitor() {
        }.accept(Float.MAX_VALUE);
    }

    @Test
    public void testAcceptInteger() {
        final StringBuilder b = new StringBuilder();
        final Integer value = Integer.MAX_VALUE;

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final Integer v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptInteger2() {
        new SpreadsheetValueVisitor() {
        }.accept(Integer.MAX_VALUE);
    }

    @Test
    public void testAcceptLabel() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetLabelName value = SpreadsheetSelection.labelName("Label123");

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetLabelName v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLabel2() {
        new SpreadsheetValueVisitor() {
        }.accept(SpreadsheetSelection.labelName("Label123"));
    }

    @Test
    public void testAcceptLocalDate() {
        final StringBuilder b = new StringBuilder();
        final LocalDate value = LocalDate.of(2000, 1, 2);

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final LocalDate v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLocalDate2() {
        new SpreadsheetValueVisitor() {
        }.accept(LocalDate.MAX);
    }

    @Test
    public void testAcceptLocalDateTime() {
        final StringBuilder b = new StringBuilder();
        final LocalDateTime value = LocalDateTime.of(2000, 1, 2, 12, 58, 59);

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final LocalDateTime v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLocalDateTime2() {
        new SpreadsheetValueVisitor() {
        }.accept(LocalDateTime.MAX);
    }

    @Test
    public void testAcceptLocalTime() {
        final StringBuilder b = new StringBuilder();
        final LocalTime value = LocalTime.of(12, 58, 59);

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final LocalTime v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLocalTime2() {
        new SpreadsheetValueVisitor() {
        }.accept(LocalTime.MAX);
    }

    @Test
    public void testAcceptLong() {
        final StringBuilder b = new StringBuilder();
        final Long value = 123L;

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final Long v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptLong2() {
        new SpreadsheetValueVisitor() {
        }.accept(Long.MAX_VALUE);
    }

    @Test
    public void testAcceptRowReference() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetRowReference value = SpreadsheetSelection.parseRow("1");

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetRowReference v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptRowReference2() {
        new SpreadsheetValueVisitor() {
        }.accept(SpreadsheetSelection.parseRow("1"));
    }

    @Test
    public void testAcceptRowRangeReference() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetRowRangeReference value = SpreadsheetSelection.parseRowRange("2:3");

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetRowRangeReference v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptRowRangeReference2() {
        new SpreadsheetValueVisitor() {
        }.accept(SpreadsheetSelection.parseRowRange("2:3"));
    }

    @Test
    public void testAcceptShort() {
        final StringBuilder b = new StringBuilder();
        final Short value = Short.MAX_VALUE;

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final Short v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptShort2() {
        new SpreadsheetValueVisitor() {
        }.accept(Short.MAX_VALUE);
    }

    @Test
    public void testAcceptSpreadsheetError() {
        final StringBuilder b = new StringBuilder();
        final SpreadsheetError value = SpreadsheetErrorKind.VALUE.setMessage("Bad value!");

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final SpreadsheetError v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptSpreadsheetError2() {
        new SpreadsheetValueVisitor() {
        }.accept(SpreadsheetErrorKind.VALUE.setMessage("Bad value!"));
    }

    @Test
    public void testAcceptString() {
        final StringBuilder b = new StringBuilder();
        final String value = "abc123";

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final String v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptString2() {
        new SpreadsheetValueVisitor() {
        }.accept("a1");
    }

    @Test
    public void testAcceptUnknownValueType() {
        final StringBuilder b = new StringBuilder();
        final Map<?, ?> value = Maps.of("1", "2");

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final Object v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptUnknownValueType2() {
        new SpreadsheetValueVisitor() {
        }.accept(this);
    }

    @Test
    public void testAcceptUnknownNumberType() {
        final StringBuilder b = new StringBuilder();
        final Object value = new TestNumber();

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visit(final Object v) {
                assertSame(value, v);
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Test
    public void testAcceptUnknownNumberType2() {
        new SpreadsheetValueVisitor() {
        }.accept(new TestNumber());
    }

    static class TestNumber extends Number {
        @Override
        public int intValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long longValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public float floatValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public double doubleValue() {
            throw new UnsupportedOperationException();
        }

        private final static long serialVersionUID = 1L;
    }

    @Test
    @Override
    public void testAcceptWithNull() {
        final StringBuilder b = new StringBuilder();
        final Object value = null;

        new FakeSpreadsheetValueVisitor() {
            @Override
            protected Visiting startVisit(final Object v) {
                assertSame(value, v);
                b.append("1");
                return Visiting.CONTINUE;
            }

            @Override
            protected void endVisit(final Object v) {
                assertSame(value, v);
                b.append("2");
            }

            @Override
            protected void visitNull() {
                b.append("3");
            }
        }.accept(value);

        this.checkEquals("132", b.toString());
    }

    @Override
    public void testCheckToStringOverridden() {
        // using FakeSpreadsheetValueVisitor disable test
    }

    @Override
    public SpreadsheetValueVisitor createVisitor() {
        return new FakeSpreadsheetValueVisitor();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetValueVisitor> type() {
        return SpreadsheetValueVisitor.class;
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
