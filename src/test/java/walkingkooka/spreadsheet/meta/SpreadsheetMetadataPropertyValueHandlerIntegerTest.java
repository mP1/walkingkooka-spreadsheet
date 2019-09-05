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

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataPropertyValueHandlerIntegerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<SpreadsheetMetadataPropertyValueHandlerInteger, Integer> {

    @Test
    public void testWithNullIntPredicateFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetMetadataPropertyValueHandlerInteger.with(null);
        });
    }

    @Test
    public void testPredicateFails() {
        this.checkFails(-1, "Invalid value");
    }

    @Test
    public void testPredicatePass() {
        this.check(10);
    }

    @Test
    public void testFromJsonNode() {
        final Integer yy = this.propertyValue();
        this.fromJsonNodeAndCheck(this.toJsonNode(yy), yy);
    }

    @Test
    public void testToJsonNode() {
        final Integer yy = this.propertyValue();
        this.toJsonNodeAndCheck(yy, this.toJsonNode(yy));
    }

    @Override
    public void testTypeNaming() {
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerInteger handler() {
        return SpreadsheetMetadataPropertyValueHandlerInteger.with((i) -> i >= 0);
    }

    @Override
    SpreadsheetMetadataPropertyName<Integer> propertyName() {
        return SpreadsheetMetadataPropertyName.WIDTH;
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
    public Class<SpreadsheetMetadataPropertyValueHandlerInteger> type() {
        return SpreadsheetMetadataPropertyValueHandlerInteger.class;
    }

    @Override
    public String typeNameSuffix() {
        return Integer.class.getSimpleName();
    }
}
