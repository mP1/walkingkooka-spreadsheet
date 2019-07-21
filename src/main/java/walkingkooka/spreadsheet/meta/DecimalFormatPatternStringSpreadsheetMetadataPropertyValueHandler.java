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

import walkingkooka.tree.json.JsonNode;

import java.text.DecimalFormat;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for valid {@link String} {@link DecimalFormat} patterns.
 */
final class DecimalFormatPatternStringSpreadsheetMetadataPropertyValueHandler extends SpreadsheetMetadataPropertyValueHandler<String> {

    /**
     * Singleton
     */
    final static DecimalFormatPatternStringSpreadsheetMetadataPropertyValueHandler INSTANCE = new DecimalFormatPatternStringSpreadsheetMetadataPropertyValueHandler();

    /**
     * Private ctor use singleton
     */
    private DecimalFormatPatternStringSpreadsheetMetadataPropertyValueHandler() {
        super();
    }

    @Override
    final void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        final String pattern = this.checkType(value, String.class, name);
        if (pattern.isEmpty()) {
            throw new SpreadsheetMetadataPropertyValueException("Empty pattern", name, value);
        }
        try {
            new DecimalFormat(pattern);
        } catch (final IllegalArgumentException cause) {
            throw new SpreadsheetMetadataPropertyValueException(cause.getMessage(), name, value);
        }
    }

    @Override
    String expectedTypeName(Class<?> type) {
        return String.class.getSimpleName();
    }

    // ToString.........................................................................................................

    @Override
    public final String toString() {
        return DecimalFormat.class.getSimpleName() + " pattern";
    }

    // HasJsonNode......................................................................................................

    @Override
    final String fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name) {
        return node.fromJsonNode(String.class);
    }

    @Override
    final JsonNode toJsonNode(final String value) {
        return JsonNode.string(value);
    }
}
