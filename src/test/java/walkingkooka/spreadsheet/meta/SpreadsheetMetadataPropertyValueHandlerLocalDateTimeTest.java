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

import java.time.LocalDateTime;

public final class SpreadsheetMetadataPropertyValueHandlerLocalDateTimeTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<SpreadsheetMetadataPropertyValueHandlerLocalDateTime, LocalDateTime> {

    @Test
    public void testJsonNodeUnmarshall() {
        final LocalDateTime dateTime = this.propertyValue();
        this.unmarshallAndCheck(this.marshall(dateTime), dateTime);
    }

    @Test
    public void testJsonNodeMarshall() {
        final LocalDateTime dateTime = this.propertyValue();
        this.marshallAndCheck(dateTime, this.marshall(dateTime));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerLocalDateTime handler() {
        return SpreadsheetMetadataPropertyValueHandlerLocalDateTime.INSTANCE;
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
    public Class<SpreadsheetMetadataPropertyValueHandlerLocalDateTime> type() {
        return SpreadsheetMetadataPropertyValueHandlerLocalDateTime.class;
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNameSuffix() {
        return LocalDateTime.class.getSimpleName();
    }
}
