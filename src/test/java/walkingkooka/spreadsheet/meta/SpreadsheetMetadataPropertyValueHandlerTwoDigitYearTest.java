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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.tree.json.HasJsonNode;

public final class SpreadsheetMetadataPropertyValueHandlerTwoDigitYearTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<SpreadsheetMetadataPropertyValueHandlerTwoDigitYear, Integer> {

    @Test
    public void testCheckNegativeFails() {
        this.checkFails(-1, "Expected value between 0 and 99 but got -1");
    }

    @Test
    public void testCheckZero() {
        this.check(0);
    }

    @Test
    public void testCheckOne() {
        this.check(1);
    }

    @Test
    public void testCheckThreeDigits() {
        this.checkFails(100, "Expected value between 0 and 99 but got 100");
    }

    @Test
    public void testFromJsonNode() {
        final Integer yy = this.propertyValue();
        this.fromJsonNodeAndCheck(HasJsonNode.toJsonNodeObject(yy), yy);
    }

    @Test
    public void testToJsonNode() {
        final Integer yy = this.propertyValue();
        this.toJsonNodeAndCheck(yy, HasJsonNode.toJsonNodeObject(yy));
    }

    @Override
    public void testTypeNaming() {
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerTwoDigitYear handler() {
        return SpreadsheetMetadataPropertyValueHandlerTwoDigitYear.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<Integer> propertyName() {
        return SpreadsheetMetadataPropertyName.TWO_DIGIT_YEAR_INTERPRETATION;
    }

    @Override
    Integer propertyValue() {
        return 45;
    }

    @Override
    String propertyValueType() {
        return Integer.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return this.typeNameSuffix();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerTwoDigitYear> type() {
        return SpreadsheetMetadataPropertyValueHandlerTwoDigitYear.class;
    }

    @Override
    public String typeNameSuffix() {
        return "TwoDigitYear";
    }
}
