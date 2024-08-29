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

import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.export.SpreadsheetExporterInfo;
import walkingkooka.spreadsheet.export.SpreadsheetExporterInfoSet;
import walkingkooka.spreadsheet.export.SpreadsheetExporterName;

public final class SpreadsheetMetadataPropertyNamePluginSpreadsheetExportersTest extends SpreadsheetMetadataPropertyNamePluginTestCase<SpreadsheetMetadataPropertyNamePluginSpreadsheetExporters, SpreadsheetExporterInfoSet, SpreadsheetExporterInfo, SpreadsheetExporterName> {

    @Override
    SpreadsheetMetadataPropertyNamePluginSpreadsheetExporters createName() {
        return SpreadsheetMetadataPropertyNamePluginSpreadsheetExporters.instance();
    }

    @Override
    SpreadsheetExporterInfoSet propertyValue() {
        return SpreadsheetExporterInfoSet.with(
                Sets.of(
                        SpreadsheetExporterInfo.with(
                                Url.parseAbsolute("https://example.com/exporter/1"),
                                SpreadsheetExporterName.with("Test123")
                        )
                )
        );
    }

    @Override
    String propertyValueType() {
        return SpreadsheetExporterInfoSet.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyNamePluginSpreadsheetExporters> type() {
        return SpreadsheetMetadataPropertyNamePluginSpreadsheetExporters.class;
    }
}
