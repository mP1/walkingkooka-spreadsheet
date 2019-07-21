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

public final class PositiveIntegerSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase<PositiveIntegerSpreadsheetMetadataPropertyValueHandler, Integer> {

    @Test
    public void testCheckNegativeFails() {
        this.checkFails(-1, "Expected positive integer got -1");
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
    public void testCheckTwo() {
        this.check(2);
    }

    @Test
    public void testFromJsonNode() {
        final Integer positiveInteger = this.propertyValue();
        this.fromJsonNodeAndCheck(HasJsonNode.toJsonNodeObject(positiveInteger), positiveInteger);
    }

    @Test
    public void testToJsonNode() {
        final Integer positiveInteger = this.propertyValue();
        this.toJsonNodeAndCheck(positiveInteger, HasJsonNode.toJsonNodeObject(positiveInteger));
    }

    @Override
    public void testTypeNaming() {
    }

    @Override
    PositiveIntegerSpreadsheetMetadataPropertyValueHandler handler() {
        return PositiveIntegerSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<Integer> propertyName() {
        return SpreadsheetMetadataPropertyName.PRECISION;
    }

    @Override
    Integer propertyValue() {
        return 234;
    }

    @Override
    String propertyValueType() {
        return Integer.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return "+" + Integer.class.getSimpleName();
    }

    @Override
    public Class<PositiveIntegerSpreadsheetMetadataPropertyValueHandler> type() {
        return PositiveIntegerSpreadsheetMetadataPropertyValueHandler.class;
    }
}
