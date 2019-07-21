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

import java.time.LocalDateTime;

public final class LocalDateTimeSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase<LocalDateTimeSpreadsheetMetadataPropertyValueHandler, LocalDateTime> {

    @Test
    public void testFromJsonNode() {
        final LocalDateTime dateTime = this.propertyValue();
        this.fromJsonNodeAndCheck(HasJsonNode.toJsonNodeObject(dateTime), dateTime);
    }

    @Test
    public void testToJsonNode() {
        final LocalDateTime dateTime = this.propertyValue();
        this.toJsonNodeAndCheck(dateTime, HasJsonNode.toJsonNodeObject(dateTime));
    }
    
    @Override
    LocalDateTimeSpreadsheetMetadataPropertyValueHandler handler() {
        return LocalDateTimeSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<LocalDateTime> propertyName() {
        return SpreadsheetMetadataPropertyName.CREATE_DATE_TIME;
    }

    @Override
    LocalDateTime propertyValue() {
        return LocalDateTime.of(2000, 1, 31, 12, 58, 59);
    }

    @Override
    String propertyValueType() {
        return LocalDateTime.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return LocalDateTime.class.getSimpleName();
    }

    @Override
    public Class<LocalDateTimeSpreadsheetMetadataPropertyValueHandler> type() {
        return LocalDateTimeSpreadsheetMetadataPropertyValueHandler.class;
    }
}
