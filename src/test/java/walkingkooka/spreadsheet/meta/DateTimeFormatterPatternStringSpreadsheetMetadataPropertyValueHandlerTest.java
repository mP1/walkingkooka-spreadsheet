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
import walkingkooka.Cast;
import walkingkooka.tree.json.JsonNode;

import java.time.format.DateTimeFormatter;

public final class DateTimeFormatterPatternStringSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<DateTimeFormatterPatternStringSpreadsheetMetadataPropertyValueHandler, String> {

    @Test
    public void testCheckInvalidPatternFails() {
        this.checkFails("pattern \"", "Unknown pattern letter: t");
    }

    @Test
    public final void testFromJsonNode() {
        final String pattern = this.propertyValue();
        this.fromJsonNodeAndCheck(JsonNode.string(pattern), pattern);
    }

    @Test
    public final void testToJsonNode() {
        final String pattern = this.propertyValue();
        this.toJsonNodeAndCheck(pattern, JsonNode.string(pattern));
    }

    @Override
    DateTimeFormatterPatternStringSpreadsheetMetadataPropertyValueHandler handler() {
        return DateTimeFormatterPatternStringSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName propertyName() {
        return SpreadsheetMetadataPropertyName.DATE_PATTERN;
    }

    @Override
    String propertyValue() {
        return "dd/MM/yyyy hh:mm";
    }

    @Override
    String propertyValueType() {
        return String.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return DateTimeFormatter.class.getSimpleName() + " pattern";
    }

    @Override
    public String typeNamePrefix() {
        return DateTimeFormatter.class.getSimpleName() + "Pattern";
    }

    @Override
    public Class<DateTimeFormatterPatternStringSpreadsheetMetadataPropertyValueHandler> type() {
        return Cast.to(DateTimeFormatterPatternStringSpreadsheetMetadataPropertyValueHandler.class);
    }
}
