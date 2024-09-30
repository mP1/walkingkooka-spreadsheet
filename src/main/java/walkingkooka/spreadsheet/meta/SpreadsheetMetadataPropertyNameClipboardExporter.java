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


import walkingkooka.spreadsheet.export.SpreadsheetExporterSelector;

import java.util.Locale;
import java.util.Optional;

final class SpreadsheetMetadataPropertyNameClipboardExporter extends SpreadsheetMetadataPropertyName<SpreadsheetExporterSelector> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameClipboardExporter instance() {
        return new SpreadsheetMetadataPropertyNameClipboardExporter();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameClipboardExporter() {
        super("clipboard-exporter");
    }

    @Override
    SpreadsheetExporterSelector checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetExporterSelector);
    }

    @Override
    String expected() {
        return SpreadsheetExporterSelector.class.getSimpleName();
    }

    @Override
    void accept(final SpreadsheetExporterSelector value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitClipboardExporter(value);
    }

    @Override
    Optional<SpreadsheetExporterSelector> extractLocaleAwareValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<SpreadsheetExporterSelector> type() {
        return SpreadsheetExporterSelector.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public SpreadsheetExporterSelector parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetExporterSelector.parse(value);
    }
}
