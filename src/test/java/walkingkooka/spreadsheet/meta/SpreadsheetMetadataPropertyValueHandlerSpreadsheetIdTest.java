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

public final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetIdTest extends SpreadsheetMetadataPropertyValueHandlerTestCase3<SpreadsheetMetadataPropertyValueHandlerSpreadsheetId, SpreadsheetId> {

    @Test
    public void testJsonNodeUnmarshall() {
        final SpreadsheetId spreadsheetId = this.propertyValue();
        this.unmarshallAndCheck(this.marshall(spreadsheetId), spreadsheetId);
    }

    @Test
    public void testJsonNodeMarshall() {
        final SpreadsheetId spreadsheetId = this.propertyValue();
        this.marshallAndCheck(spreadsheetId, this.marshall(spreadsheetId));
    }

    @Override
    SpreadsheetMetadataPropertyValueHandlerSpreadsheetId handler() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetId.INSTANCE;
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
    public Class<SpreadsheetMetadataPropertyValueHandlerSpreadsheetId> type() {
        return SpreadsheetMetadataPropertyValueHandlerSpreadsheetId.class;
    }
}
