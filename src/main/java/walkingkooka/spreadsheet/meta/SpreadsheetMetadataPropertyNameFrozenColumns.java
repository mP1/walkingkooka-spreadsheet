
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

/**
 * Tracks how many columns are frozen.
 */
final class SpreadsheetMetadataPropertyNameFrozenColumns extends SpreadsheetMetadataPropertyNameInteger {

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
    Integer checkValue0(final Object value) {
        final Integer integerValue = this.checkValueTypeInteger(value);
        if (integerValue < 0) {
            throw new SpreadsheetMetadataPropertyValueException("Expected int >= 0", this, integerValue);
        }
        return integerValue;
    }

    @Override
    void accept(final Integer value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitFrozenColumns(value);
    }
}
