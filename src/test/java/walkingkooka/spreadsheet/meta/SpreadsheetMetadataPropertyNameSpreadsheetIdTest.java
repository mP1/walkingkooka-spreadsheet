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

import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameSpreadsheetIdTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetId, SpreadsheetId> {

    @Test
    public void testInvalidSpreadsheetIdFails() {
        this.checkValueFails("123-invalid", "Expected SpreadsheetId, but got \"123-invalid\" for \"spreadsheet-id\"");
    }

    @Test
    public void testExtractLocaleValue() {
        this.extractLocaleValueAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetId.INSTANCE, "spreadsheet-id");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetId createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetId.INSTANCE;
    }

    @Override
    SpreadsheetId propertyValue() {
        return SpreadsheetId.with(123);
    }

    @Override
    String propertyValueType() {
        return SpreadsheetId.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetId> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetId.class;
    }
}
