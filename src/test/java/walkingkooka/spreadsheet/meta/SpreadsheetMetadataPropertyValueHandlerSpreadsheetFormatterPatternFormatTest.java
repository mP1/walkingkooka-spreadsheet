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
import walkingkooka.spreadsheet.format.SpreadsheetFormatterPattern;
import walkingkooka.tree.json.JsonNode;

public final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormatTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormat, SpreadsheetFormatterPattern> {

    @Override
    public void testTypeNaming() {
    }

    @Test
    public void testGeneralFails() {
        this.checkFails(SpreadsheetFormatterPattern.parse("GENERAL"), "Invalid pattern \"GENERAL\"");
    }

    @Test
    public void testFromJsonNode() {
        final SpreadsheetFormatterPattern value = this.propertyValue();
        this.fromJsonNodeAndCheck(JsonNode.string(value.value().text()), value);
    }

    @Test
    public void testToJsonNode() {
        final SpreadsheetFormatterPattern value = this.propertyValue();
        this.toJsonNodeAndCheck(value, JsonNode.string(value.value().text()));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormat handler() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormat.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<SpreadsheetFormatterPattern> propertyName() {
        return SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN;
    }

    @Override
    SpreadsheetFormatterPattern propertyValue() {
        return SpreadsheetFormatterPattern.parse("#0.00");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetFormatterPattern.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return this.propertyValueType();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormat> type() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatterPatternFormat.class;
    }

    @Override
    public String typeNameSuffix() {
        return this.expectedToString();
    }
}
