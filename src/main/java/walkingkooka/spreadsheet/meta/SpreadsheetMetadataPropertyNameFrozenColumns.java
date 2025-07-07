
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

import walkingkooka.locale.LocaleContext;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Optional;

/**
 * Tracks how many columns are frozen.
 */
final class SpreadsheetMetadataPropertyNameFrozenColumns extends SpreadsheetMetadataPropertyName<SpreadsheetColumnRangeReference> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameFrozenColumns instance() {
        return new SpreadsheetMetadataPropertyNameFrozenColumns();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameFrozenColumns() {
        super();
    }

    @Override
    SpreadsheetColumnRangeReference checkValueNonNull(final Object value) {
        final SpreadsheetColumnRangeReference range = this.checkValueType(
            value,
            v -> v instanceof SpreadsheetColumnRangeReference
        );
        if (range.begin().value() != 0) {
            throw new SpreadsheetMetadataPropertyValueException("Column range must begin at 'A'", this, range);
        }
        return range;
    }

    @Override
    String expected() {
        return SpreadsheetColumnRangeReference.class.getSimpleName();
    }

    @Override
    Optional<SpreadsheetColumnRangeReference> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<SpreadsheetColumnRangeReference> type() {
        return SpreadsheetColumnRangeReference.class;
    }

    @Override
    void accept(final SpreadsheetColumnRangeReference value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitFrozenColumns(value);
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    SpreadsheetColumnRangeReference parseUrlFragmentSaveValueNonNull(final String value) {
        return SpreadsheetSelection.parseColumnRange(value);
    }
}
