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
import walkingkooka.spreadsheet.importer.SpreadsheetImporterInfo;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterInfoSet;
import walkingkooka.spreadsheet.importer.SpreadsheetImporterName;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds a {@link SpreadsheetImporterInfoSet}.
 */
final class SpreadsheetMetadataPropertyNamePluginSpreadsheetImporters extends SpreadsheetMetadataPropertyNamePlugin<SpreadsheetImporterInfoSet, SpreadsheetImporterInfo, SpreadsheetImporterName> {

    static {
        SpreadsheetImporterInfoSet.with(Sets.empty()); // force registry of json marshaller
    }

    /**
     * Factory, the constant on {@link SpreadsheetMetadataPropertyName} will hold the singleton.
     */
    static SpreadsheetMetadataPropertyNamePluginSpreadsheetImporters instance() {
        return new SpreadsheetMetadataPropertyNamePluginSpreadsheetImporters();
    }

    /**
     * Private ctor use getter.
     */
    private SpreadsheetMetadataPropertyNamePluginSpreadsheetImporters() {
        super("spreadsheet-importers");
    }

    @Override
    void accept(final SpreadsheetImporterInfoSet value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitSpreadsheetImporters(value);
    }

    @Override
    SpreadsheetImporterInfoSet parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetImporterInfoSet.parse(value);
    }

    @Override
    Class<SpreadsheetImporterInfoSet> type() {
        return SpreadsheetImporterInfoSet.class;
    }
}
