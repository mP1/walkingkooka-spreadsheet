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
import walkingkooka.tree.json.JsonNode;

public final class SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandler, String> {

    @Override
    public void testTypeNaming() {
    }

    @Test
    public void testEmptyString() {
        this.check("");
    }

    @Test
    public void testInvalidPatternFails() {
        this.checkFails("//", null);
    }

    @Test
    public void testFromJsonNode() {
        final String value = this.propertyValue();
        this.fromJsonNodeAndCheck(JsonNode.string(value), value);
    }

    @Test
    public void testToJsonNode() {
        final String value = this.propertyValue();
        this.toJsonNodeAndCheck(value, JsonNode.string(value));
    }

    @Override
    SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandler handler() {
        return SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<String> propertyName() {
        return SpreadsheetMetadataPropertyName.DEFAULT_PATTERN;
    }

    @Override
    String propertyValue() {
        return "#0.00";
    }

    @Override
    String propertyValueType() {
        return String.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return "pattern";
    }

    @Override
    public Class<SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandler> type() {
        return SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandler.class;
    }

    @Override
    public String typeNamePrefix() {
        return this.expectedToString();
    }
}
