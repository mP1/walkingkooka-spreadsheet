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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringBuilder;
import walkingkooka.ToStringTesting;
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.reflect.ClassTesting;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SpreadsheetNumberParsePatternsRequestTest extends SpreadsheetNumberParsePatternsTestCase2<SpreadsheetNumberParsePatternsRequest>
        implements ClassTesting<SpreadsheetNumberParsePatternsRequest>,
        ToStringTesting<SpreadsheetNumberParsePatternsRequest> {

    @Test
    public void testComputeValueNeitherNegativeOrPositiveZero() {
        this.computeValueAndCheck(null, BigDecimal.ZERO, true, 0, BigDecimal.ZERO.negate());
    }

    @Test
    public void testComputeValueNegativeZero() {
        this.computeValueAndCheck(true, BigDecimal.ZERO, true, 0, BigDecimal.ZERO.negate());
    }

    @Test
    public void testComputeValuePositiveZero() {
        this.computeValueAndCheck(false, BigDecimal.ZERO, true, 0, BigDecimal.ZERO);
    }

    @Test
    public void testComputeValueWithoutExponent() {
        this.computeValueAndCheck(false, "12.34", false, 0, "12.34");
    }

    @Test
    public void testComputeValueNegativeWithoutExponent() {
        this.computeValueAndCheck(true, "12.34", false, 0, "-12.34");
    }

    @Test
    public void testComputeValueWithExponent1() {
        this.computeValueAndCheck(false, "123.4", false, 1, "1234");
    }

    @Test
    public void testComputeValueWithExponent2() {
        this.computeValueAndCheck(false, "75", false, 2, "7500");
    }

    @Test
    public void testComputeValueNegativeExponent1() {
        this.computeValueAndCheck(false, "75", true, 1, "7.5");
    }

    private void computeValueAndCheck(final boolean negativeMantissa,
                                      final String mantissa,
                                      final boolean negativeExponent,
                                      final int exponent,
                                      final String expected) {
        this.computeValueAndCheck(negativeMantissa,
                new BigDecimal(mantissa),
                negativeExponent,
                exponent,
                new BigDecimal(expected));
    }

    private void computeValueAndCheck(final Boolean negativeMantissa,
                                      final BigDecimal mantissa,
                                      final boolean negativeExponent,
                                      final int exponent,
                                      final BigDecimal expected) {
        final SpreadsheetNumberParsePatternsRequest request = this.createRequest();
        request.negativeMantissa = negativeMantissa;
        request.mantissa = mantissa;
        request.negativeExponent = negativeExponent;
        request.exponent = exponent;

        assertEquals(expected.stripTrailingZeros(),
                request.computeValue().stripTrailingZeros(),
                () -> ToStringBuilder.empty()
                        .label("negativeMantissa").value(negativeMantissa)
                        .label("mantissa").value(mantissa)
                        .label("negativeExponent").value(negativeExponent)
                        .label("exponent").value(exponent)
                        .build());
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createRequest(), "INTEGER 0");
    }

    @Test
    public void testToString2() {
        final SpreadsheetNumberParsePatternsRequest request = this.createRequest();
        request.negativeMantissa = true;
        request.mantissa = BigDecimal.valueOf(123.5);

        this.toStringAndCheck(request, "INTEGER -123.5");
    }

    @Test
    public void testToStringPercent() {
        final SpreadsheetNumberParsePatternsRequest request = this.createRequest();
        request.mantissa = BigDecimal.valueOf(123.5);
        request.percentage = true;

        this.toStringAndCheck(request, "INTEGER 1.235");
    }

    private SpreadsheetNumberParsePatternsRequest createRequest() {
        return SpreadsheetNumberParsePatternsRequest.with(Iterators.fake(), DecimalNumberContexts.american(MathContext.UNLIMITED));
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetNumberParsePatternsRequest> type() {
        return SpreadsheetNumberParsePatternsRequest.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return "Request";
    }
}
