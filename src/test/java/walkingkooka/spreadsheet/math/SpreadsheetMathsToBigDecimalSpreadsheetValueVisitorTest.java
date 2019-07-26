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

package walkingkooka.spreadsheet.math;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetValueVisitorTesting;
import walkingkooka.text.CharSequences;
import walkingkooka.type.JavaVisibility;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetMathsToBigDecimalSpreadsheetValueVisitorTest implements SpreadsheetValueVisitorTesting<SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor> {

    @Test
    public void testBigDecimal() {
        this.bigDecimalAndCheck(BigDecimal.valueOf(123.5), BigDecimal.valueOf(123.5));
    }

    @Test
    public void testBigInteger() {
        this.bigDecimalAndCheck(BigInteger.valueOf(123), BigDecimal.valueOf(123));
    }

    @Test
    public void testBooleanFails() {
        this.bigDecimalAndCheck(true, null);
    }

    @Test
    public void testDouble() {
        this.bigDecimalAndCheck(123.5, BigDecimal.valueOf(123.5));
    }

    @Test
    public void testLocalDateFails() {
        this.bigDecimalAndCheck(LocalDate.EPOCH, null);
    }

    @Test
    public void testLocalDateTimeFails() {
        this.bigDecimalAndCheck(LocalDateTime.MAX, null);
    }

    @Test
    public void testLocalTimeFails() {
        this.bigDecimalAndCheck(LocalTime.MAX, null);
    }

    @Test
    public void testLong() {
        this.bigDecimalAndCheck(123L, BigDecimal.valueOf(123));
    }

    @Test
    public void testOtherFails() {
        this.bigDecimalAndCheck(this, null);
    }

    @Test
    public void testStringFails() {
        this.bigDecimalAndCheck("abc", null);
    }

    private void bigDecimalAndCheck(final Object value, final BigDecimal expected) {
        assertEquals(Optional.ofNullable(expected),
                SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor.bigDecimal(value),
                () -> "" + CharSequences.quoteIfChars(value));
    }

    @Test
    public void testToString() {
        final SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor visitor = new SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor();
        visitor.accept(123L);
        this.toStringAndCheck(visitor, "123");
    }

    @Override
    public SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor createVisitor() {
        return new SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor();
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetMaths.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor> type() {
        return SpreadsheetMathsToBigDecimalSpreadsheetValueVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
