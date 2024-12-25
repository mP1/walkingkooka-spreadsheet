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

final class SpreadsheetMetadataPropertyNameIntegerTwoDigitYear extends SpreadsheetMetadataPropertyNameInteger {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameIntegerTwoDigitYear instance() {
        return new SpreadsheetMetadataPropertyNameIntegerTwoDigitYear();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameIntegerTwoDigitYear() {
        super("two-digit-year");
    }

    @Override
    Integer checkValue0(final Object value) {
        final Integer integerValue = this.checkValueTypeInteger(value);
        if (integerValue < 0 || integerValue > 99) {
            throw new SpreadsheetMetadataPropertyValueException("must be between 0 and including 99", this, integerValue);
        }
        return integerValue;
    }

    @Override
    void accept(final Integer value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitTwoDigitYear(value);
    }
}
