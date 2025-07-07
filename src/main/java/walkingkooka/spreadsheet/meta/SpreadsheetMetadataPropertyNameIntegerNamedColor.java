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

import walkingkooka.naming.Name;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;

/**
 * The {@link Name} of metadata property, with a custom handler and visitor to handle dispatching to a {@link SpreadsheetMetadataVisitor} method.
 */
final class SpreadsheetMetadataPropertyNameIntegerNamedColor extends SpreadsheetMetadataPropertyNameInteger {

    /**
     * Factory used to create a new {@link SpreadsheetMetadataPropertyNameIntegerNamedColor} constant.
     */
    static SpreadsheetMetadataPropertyNameIntegerNamedColor withColorName(final SpreadsheetColorName name) {
        return new SpreadsheetMetadataPropertyNameIntegerNamedColor(name);
    }

    /**
     * Private constructor use factory.
     */
    private SpreadsheetMetadataPropertyNameIntegerNamedColor(final SpreadsheetColorName name) {
        super(COLOR_PREFIX + name.value());
        this.name = name;
    }

    @Override
    Integer checkValueNonNull(final Object value) {
        final int colorNumber = this.checkValueTypeInteger(value);
        try {
            SpreadsheetMetadataPropertyName.numberedColor(colorNumber);
        } catch (final Exception cause) {
            throw new SpreadsheetMetadataPropertyValueException(
                cause.getMessage(),
                this,
                value,
                cause
            );
        }
        return colorNumber;
    }

    @Override
    void accept(final Integer value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitNamedColor(
            this.name,
            value
        );
    }

    final SpreadsheetColorName name;
}
