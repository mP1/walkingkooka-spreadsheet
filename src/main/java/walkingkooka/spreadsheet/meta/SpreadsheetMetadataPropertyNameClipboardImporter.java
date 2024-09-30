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


import walkingkooka.spreadsheet.importer.SpreadsheetImporterSelector;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameClipboardImporter extends SpreadsheetMetadataPropertyName<SpreadsheetImporterSelector> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameClipboardImporter instance() {
        return new SpreadsheetMetadataPropertyNameClipboardImporter();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameClipboardImporter() {
        super("clipboard-importer");
    }

    @Override
    SpreadsheetImporterSelector checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetImporterSelector);
    }

    @Override
    String expected() {
        return SpreadsheetImporterSelector.class.getSimpleName();
    }

    @Override
    void accept(final SpreadsheetImporterSelector value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitClipboardImporter(value);
    }

    @Override
    Optional<SpreadsheetImporterSelector> extractLocaleAwareValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<SpreadsheetImporterSelector> type() {
        return SpreadsheetImporterSelector.class;
    }

    @Override
    String compareToName() {
        return this.value();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public SpreadsheetImporterSelector parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetImporterSelector.parse(value);
    }
}
