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
import walkingkooka.visit.Visiting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetValueVisitorTestingTest implements SpreadsheetValueVisitorTesting<SpreadsheetValueVisitorTestingTest.TestSpreadsheetValueVisitor> {

    @Test
    public void testVisitBigDecimal() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final BigDecimal value) {
                    visit = value;
                }
            },
            BigDecimal.valueOf(123)
        );
    }

    @Test
    public void testVisitBigDecimal2() {
        new SpreadsheetValueVisitor() {
        }.accept(BigDecimal.valueOf(123));
    }

    @Test
    public void testVisitBigInteger() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final BigInteger value) {
                    visit = value;
                }
            },
            BigInteger.valueOf(123));
    }

    @Test
    public void testVisitBigInteger2() {
        new SpreadsheetValueVisitor() {
        }.accept(BigInteger.valueOf(123));
    }

    @Test
    public void testVisitBoolean() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final Boolean value) {
                    visit = value;
                }
            },
            Boolean.TRUE);
    }

    @Test
    public void testVisitBoolean2() {
        new SpreadsheetValueVisitor() {
        }.accept(Boolean.TRUE);
    }

    @Test
    public void testVisitByte() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final Byte value) {
                    visit = value;
                }
            },
            Byte.MAX_VALUE);
    }

    @Test
    public void testVisitByte2() {
        new SpreadsheetValueVisitor() {
        }.accept(Byte.MAX_VALUE);
    }

    @Test
    public void testVisitCharacter() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final Character value) {
                    visit = value;
                }
            },
            'A'
        );
    }

    @Test
    public void testVisitDouble() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final Double value) {
                    visit = value;
                }
            },
            Double.MAX_VALUE
        );
    }

    @Test
    public void testVisitDouble2() {
        new SpreadsheetValueVisitor() {
        }.accept(Double.MAX_VALUE);
    }

    @Test
    public void testVisitFloat() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final Float value) {
                    visit = value;
                }
            },
            Float.MAX_VALUE
        );
    }

    @Test
    public void testVisitFloat2() {
        new SpreadsheetValueVisitor() {
        }.accept(Float.MAX_VALUE);
    }

    @Test
    public void testVisitInteger() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final Integer value) {
                    visit = value;
                }
            },
            Integer.MAX_VALUE
        );
    }

    @Test
    public void testVisitInteger2() {
        new SpreadsheetValueVisitor() {
        }.accept(Integer.MAX_VALUE);
    }


    @Test
    public void testVisitLocalDate() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final LocalDate value) {
                    visit = value;
                }
            },
            LocalDate.MAX
        );
    }

    @Test
    public void testVisitLocalDate2() {
        new SpreadsheetValueVisitor() {
        }.accept(LocalDate.MAX);
    }

    @Test
    public void testVisitLocalDateTime() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final LocalDateTime value) {
                    visit = value;
                }
            },
            LocalDateTime.MAX
        );
    }

    @Test
    public void testVisitLocalDateTime2() {
        new SpreadsheetValueVisitor() {
        }.accept(LocalDateTime.MAX);
    }


    @Test
    public void testVisitLocalTime() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final LocalTime value) {
                    visit = value;
                }
            },
            LocalTime.MAX
        );
    }

    @Test
    public void testVisitLocalTime2() {
        new SpreadsheetValueVisitor() {
        }.accept(LocalTime.MAX);
    }

    @Test
    public void testVisitLong() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final Long value) {
                    visit = value;
                }
            },
            Long.MAX_VALUE
        );
    }

    @Test
    public void testVisitLong2() {
        new SpreadsheetValueVisitor() {
        }.accept(Long.MAX_VALUE);
    }

    @Test
    public void testVisitShort() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final Short value) {
                    visit = value;
                }
            },
            Short.MAX_VALUE
        );
    }

    @Test
    public void testVisitShort2() {
        new SpreadsheetValueVisitor() {
        }.accept(Short.MAX_VALUE);
    }

    @Test
    public void testVisitString() {
        this.acceptAndCheck(
            new TestSpreadsheetValueVisitor() {
                @Override
                protected void visit(final String value) {
                    visit = value;
                }
            },
            this.getClass().getName()
        );
    }

    @Test
    public void testVisitString2() {
        new SpreadsheetValueVisitor() {
        }.accept(this.getClass().getName());
    }

    // SpreadsheetValueVisitorTesting....................................................................................

    @Override
    public TestSpreadsheetValueVisitor createVisitor() {
        return new TestSpreadsheetValueVisitor() {
        };
    }

    private void acceptAndCheck(final TestSpreadsheetValueVisitor visitor,
                                final Object value) {
        this.start = null;
        this.visit = null;
        this.end = null;

        visitor.accept(value);

        assertSame(value, this.start, "start");
        this.checkEquals(value, this.visit, "visit");
        assertSame(value, this.end, "end");
    }

    abstract class TestSpreadsheetValueVisitor extends FakeSpreadsheetValueVisitor {
        TestSpreadsheetValueVisitor() {
            super();
        }

        @Override
        protected Visiting startVisit(final Object value) {
            start = value;
            return Visiting.CONTINUE;
        }

        @Override
        protected void endVisit(final Object value) {
            end = value;
        }

        @Override
        protected void visitNull() {
            // nop
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    private Object start;
    private Object visit;
    private Object end;

    // class............................................................................................................

    @Override
    public void testTestNaming() {
    }

    // class............................................................................................................

    @Override
    public Class<TestSpreadsheetValueVisitor> type() {
        return TestSpreadsheetValueVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return "Test";
    }
}
