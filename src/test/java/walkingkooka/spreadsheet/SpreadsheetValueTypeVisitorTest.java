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
import walkingkooka.type.JavaVisibility;
import walkingkooka.visit.Visiting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        assertEquals("12", b.toString());
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

        assertEquals("132", b.toString());
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

        assertEquals("132", b.toString());
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

        assertEquals("132", b.toString());
    }

    @Test
    public void testAcceptBoolean2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Boolean.class);
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

        assertEquals("132", b.toString());
    }

    @Test
    public void testAcceptDouble2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Double.class);
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

        assertEquals("132", b.toString());
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

        assertEquals("132", b.toString());
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

        assertEquals("132", b.toString());
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

        assertEquals("132", b.toString());
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

        assertEquals("132", b.toString());
    }

    @Test
    public void testAcceptNumber2() {
        new SpreadsheetValueTypeVisitor() {
        }.accept(Number.class);
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

        assertEquals("132", b.toString());
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
            protected void visitUnknown() {
                b.append("3");
            }
        }.accept(type);

        assertEquals("132", b.toString());
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
