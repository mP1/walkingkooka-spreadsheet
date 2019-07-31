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
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatterPattern;
import walkingkooka.tree.json.JsonNode;

public final class SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase2<SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandler, SpreadsheetTextFormatterPattern> {

    @Override
    public void testTypeNaming() {
    }

    @Test
    public void testGeneralFails() {
        this.checkFails(SpreadsheetTextFormatterPattern.with("GENERAL"), "Invalid pattern \"GENERAL\"");
    }

    @Test
    public void testFromJsonNode() {
        final SpreadsheetTextFormatterPattern value = this.propertyValue();
        this.fromJsonNodeAndCheck(JsonNode.string(value.value()), value);
    }

    @Test
    public void testToJsonNode() {
        final SpreadsheetTextFormatterPattern value = this.propertyValue();
        this.toJsonNodeAndCheck(value, JsonNode.string(value.value()));
    }

    @Override
    SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandler handler() {
        return SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<SpreadsheetTextFormatterPattern> propertyName() {
        return SpreadsheetMetadataPropertyName.BIG_DECIMAL_FORMAT_PATTERN;
    }

    @Override
    SpreadsheetTextFormatterPattern propertyValue() {
        return SpreadsheetTextFormatterPattern.with("#0.00");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetTextFormatterPattern.class.getSimpleName();
    }

    @Override
    String expectedToString() {
        return this.propertyValueType();
    }

    @Override
    public Class<SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandler> type() {
        return SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandler.class;
    }

    @Override
    public String typeNamePrefix() {
        return this.expectedToString();
    }
}
