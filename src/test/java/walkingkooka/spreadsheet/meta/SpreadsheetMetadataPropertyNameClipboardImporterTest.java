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
import walkingkooka.spreadsheet.importer.SpreadsheetImporterSelector;

import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameClipboardImporterTest extends SpreadsheetMetadataPropertyNameTestCase<
        SpreadsheetMetadataPropertyNameClipboardImporter,
        SpreadsheetImporterSelector> {

    @Test
    public void testCheckValueSpreadsheetImporterSelector() {
        this.checkValue(
                SpreadsheetImporterSelector.parse("json")
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(
                Locale.ENGLISH,
                null
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameClipboardImporter.instance(),
                "clipboard-importer"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameClipboardImporter createName() {
        return SpreadsheetMetadataPropertyNameClipboardImporter.instance();
    }

    @Override
    SpreadsheetImporterSelector propertyValue() {
        return SpreadsheetImporterSelector.parse("json");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetImporterSelector.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameClipboardImporter> type() {
        return SpreadsheetMetadataPropertyNameClipboardImporter.class;
    }
}
