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
import walkingkooka.convert.Converters;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataPropertyValueHandlerLongTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<SpreadsheetMetadataPropertyValueHandlerLong, Long> {

    @Test
    public void testWithNullIntPredicateFails() {
        assertThrows(NullPointerException.class, () -> {
            SpreadsheetMetadataPropertyValueHandlerLong.with(null);
        });
    }
    
    @Test
    public void testCheckExcelOffset() {
        this.check(Converters.EXCEL_OFFSET);
    }

    @Test
    public void testCheckJavaEpochOffset() {
        this.check(Converters.JAVA_EPOCH_OFFSET);
    }

    @Test
    public void testFromJsonNode() {
        final Long value = Converters.EXCEL_OFFSET;
        this.fromJsonNodeAndCheck(this.toJsonNode(value), value);
    }

    @Test
    public void testToJsonNode() {
        final Long value = Converters.EXCEL_OFFSET;
        this.toJsonNodeAndCheck(value, this.toJsonNode(value));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerLong handler() {
        return SpreadsheetMetadataPropertyValueHandlerLong.with((i) -> true);
    }

    @Override
    SpreadsheetMetadataPropertyName<Long> propertyName() {
        return SpreadsheetMetadataPropertyName.DATETIME_OFFSET;
    }

    @Override
    Long propertyValue() {
        return Converters.EXCEL_OFFSET;
    }

    @Override
    String propertyValueType() {
        return Long.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return "Long";
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerLong> type() {
        return SpreadsheetMetadataPropertyValueHandlerLong.class;
    }

    @Override
    public String typeNameSuffix() {
        return this.expectedToString();
    }
}
