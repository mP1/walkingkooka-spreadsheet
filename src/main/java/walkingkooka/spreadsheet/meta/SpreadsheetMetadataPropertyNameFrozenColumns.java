
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

import walkingkooka.spreadsheet.reference.SpreadsheetColumnReferenceRange;

import java.util.Locale;
import java.util.Optional;

/**
 * Tracks how many columns are frozen.
 */
final class SpreadsheetMetadataPropertyNameFrozenColumns extends SpreadsheetMetadataPropertyName<SpreadsheetColumnReferenceRange> {

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
        super("frozen-columns");
    }

    @Override
    SpreadsheetColumnReferenceRange checkValue0(final Object value) {
        final SpreadsheetColumnReferenceRange range = this.checkValueType(
                value,
                v -> v instanceof SpreadsheetColumnReferenceRange
        );
        if (range.begin().value() != 0) {
            throw new SpreadsheetMetadataPropertyValueException("Range must begin at 'A'", this, range);
        }
        return range;
    }

    @Override
    String expected() {
        return SpreadsheetColumnReferenceRange.class.getSimpleName();
    }

    @Override
    Optional<SpreadsheetColumnReferenceRange> extractLocaleValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<SpreadsheetColumnReferenceRange> type() {
        return SpreadsheetColumnReferenceRange.class;
    }

    @Override
    void accept(final SpreadsheetColumnReferenceRange value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitFrozenColumns(value);
    }

    @Override
    String compareToName() {
        return this.value();
    }
}
