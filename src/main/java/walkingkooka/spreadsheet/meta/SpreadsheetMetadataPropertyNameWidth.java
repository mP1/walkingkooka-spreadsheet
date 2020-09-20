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

final class SpreadsheetMetadataPropertyNameWidth extends SpreadsheetMetadataPropertyNameInteger {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameWidth INSTANCE = new SpreadsheetMetadataPropertyNameWidth();

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameWidth() {
        super("width");
    }

    @Override
    void checkValue0(final Object value) {
        final Integer integerValue = this.checkValueType(value,
                v -> v instanceof Integer);
        if (integerValue < 0) {
            throw new SpreadsheetMetadataPropertyValueException("Expected int > 0", this, integerValue);
        }
    }

    @Override
    void accept(final Integer value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitWidth(value);
    }
}
