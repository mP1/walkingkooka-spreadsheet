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

package walkingkooka.spreadsheet.datavalidation;

import org.junit.jupiter.api.Test;
import walkingkooka.tree.expression.ExpressionNode;

public final class CustomFormulaSpreadsheetDataValidatorTest extends SpreadsheetDataValidatorTemplateTestCase<CustomFormulaSpreadsheetDataValidator, Object> {

    private final static long VALUE = 123;

    @Test
    public void testCustomFormulaTrue() {
        this.validatePassCheck(VALUE + 1);
    }

    @Test
    public void testCustomFormulaFalse() {
        this.validateFailCheck(VALUE - 1);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createSpreadsheetDataValidator(), "B3>123");
    }

    @Override
    public CustomFormulaSpreadsheetDataValidator createSpreadsheetDataValidator() {
        return CustomFormulaSpreadsheetDataValidator.with(this.expression());
    }

    private ExpressionNode expression() {
        return ExpressionNode.greaterThan(
                ExpressionNode.reference(this.cellReference()),
                ExpressionNode.valueOrFail(VALUE));
    }

    @Override
    public Long value() {
        return VALUE;
    }

    @Override
    public Class<Object> valueType() {
        return Object.class;
    }

    @Override
    public Class<CustomFormulaSpreadsheetDataValidator> type() {
        return CustomFormulaSpreadsheetDataValidator.class;
    }
}
