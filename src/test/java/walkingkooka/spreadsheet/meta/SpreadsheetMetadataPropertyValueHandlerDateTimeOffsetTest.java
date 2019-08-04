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
import walkingkooka.tree.json.HasJsonNode;

public final class SpreadsheetMetadataPropertyValueHandlerDateTimeOffsetTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<SpreadsheetMetadataPropertyValueHandlerDateTimeOffset, Long> {

    @Test
    public void testInvalidOffsetFails() {
        this.checkFails(123L, "Invalid date time offset 123");
    }

    @Test
    public void testCheckExcelffset() {
        this.check(Converters.EXCEL_OFFSET);
    }

    @Test
    public void testCheckJavaEpochOffset() {
        this.check(Converters.JAVA_EPOCH_OFFSET);
    }

    @Test
    public void testFromJsonNode() {
        final Long value = Converters.EXCEL_OFFSET;
        this.fromJsonNodeAndCheck(HasJsonNode.toJsonNodeObject(value), value);
    }

    @Test
    public void testToJsonNode() {
        final Long value = Converters.EXCEL_OFFSET;
        this.toJsonNodeAndCheck(value, HasJsonNode.toJsonNodeObject(value));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerDateTimeOffset handler() {
        return SpreadsheetMetadataPropertyValueHandlerDateTimeOffset.INSTANCE;
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
        return "DateTimeOffset";
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerDateTimeOffset> type() {
        return SpreadsheetMetadataPropertyValueHandlerDateTimeOffset.class;
    }

    @Override
    public String typeNameSuffix() {
        return this.expectedToString();
    }
}
