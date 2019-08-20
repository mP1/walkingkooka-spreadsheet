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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetFormatPattern;
import walkingkooka.tree.json.JsonNode;

public final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPatternTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern, SpreadsheetFormatPattern> {

    @Override
    public void testTypeNaming() {
    }

    @Test
    public void testGeneralFails() {
        this.checkFails(SpreadsheetFormatPattern.parse("GENERAL"), "Invalid pattern \"GENERAL\"");
    }

    @Test
    public void testFromJsonNode() {
        final SpreadsheetFormatPattern value = this.propertyValue();
        this.fromJsonNodeAndCheck(JsonNode.string(value.value().text()), value);
    }

    @Test
    public void testToJsonNode() {
        final SpreadsheetFormatPattern value = this.propertyValue();
        this.toJsonNodeAndCheck(value, JsonNode.string(value.value().text()));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern handler() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<SpreadsheetFormatPattern> propertyName() {
        return SpreadsheetMetadataPropertyName.NUMBER_FORMAT_PATTERN;
    }

    @Override
    SpreadsheetFormatPattern propertyValue() {
        return SpreadsheetFormatPattern.parse("#0.00");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetFormatPattern.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return this.propertyValueType();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern> type() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern.class;
    }

    @Override
    public String typeNameSuffix() {
        return this.expectedToString();
    }
}
