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
import walkingkooka.spreadsheet.SpreadsheetId;

public final class SpreadsheetIdSpreadsheetMetadataPropertyValueHandlerTest extends SpreadsheetMetadataPropertyValueHandlerTestCase3<SpreadsheetIdSpreadsheetMetadataPropertyValueHandler, SpreadsheetId> {

    @Test
    public void testFromJsonNode() {
        final SpreadsheetId spreadsheetId = this.propertyValue();
        this.fromJsonNodeAndCheck(spreadsheetId.toJsonNode(), spreadsheetId);
    }

    @Test
    public void testToJsonNode() {
        final SpreadsheetId spreadsheetId = this.propertyValue();
        this.toJsonNodeAndCheck(spreadsheetId, spreadsheetId.toJsonNode());
    }

    @Override
    SpreadsheetIdSpreadsheetMetadataPropertyValueHandler handler() {
        return SpreadsheetIdSpreadsheetMetadataPropertyValueHandler.INSTANCE;
    }

    @Override
    SpreadsheetMetadataPropertyName<SpreadsheetId> propertyName() {
        return SpreadsheetMetadataPropertyName.SPREADSHEET_ID;
    }

    @Override
    SpreadsheetId propertyValue() {
        return SpreadsheetId.with(123);
    }

    @Override
    String propertyValueType() {
        return SpreadsheetId.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetIdSpreadsheetMetadataPropertyValueHandler> type() {
        return SpreadsheetIdSpreadsheetMetadataPropertyValueHandler.class;
    }
}
