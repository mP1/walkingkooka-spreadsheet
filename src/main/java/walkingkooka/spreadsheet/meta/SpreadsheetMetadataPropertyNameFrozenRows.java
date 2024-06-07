

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

import walkingkooka.spreadsheet.reference.SpreadsheetRowRangeReference;

import java.util.Locale;
import java.util.Optional;

/**
 * Tracks how many rows are frozen.
 */
final class SpreadsheetMetadataPropertyNameFrozenRows extends SpreadsheetMetadataPropertyName<SpreadsheetRowRangeReference> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameFrozenRows instance() {
        return new SpreadsheetMetadataPropertyNameFrozenRows();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameFrozenRows() {
        super();
    }

    @Override
    SpreadsheetRowRangeReference checkValue0(final Object value) {
        final SpreadsheetRowRangeReference range = this.checkValueType(
                value,
                v -> v instanceof SpreadsheetRowRangeReference
        );
        if (range.begin().value() != 0) {
            throw new SpreadsheetMetadataPropertyValueException("Range must begin at '1'", this, range);
        }
        return range;
    }

    @Override
    String expected() {
        return SpreadsheetRowRangeReference.class.getSimpleName();
    }

    @Override
    Optional<SpreadsheetRowRangeReference> extractLocaleValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<SpreadsheetRowRangeReference> type() {
        return SpreadsheetRowRangeReference.class;
    }

    @Override
    void accept(final SpreadsheetRowRangeReference value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitFrozenRows(value);
    }

    @Override
    String compareToName() {
        return this.value();
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public boolean isParseUrlFragmentSaveValueSupported() {
        return false;
    }

    @Override
    public SpreadsheetRowRangeReference parseUrlFragmentSaveValue0(final String value) {
        return this.failParseUrlFragmentSaveValueUnsupported();
    }
}
