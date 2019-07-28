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

import walkingkooka.spreadsheet.format.SpreadsheetTextFormatterPattern;
import walkingkooka.tree.json.JsonNode;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for valid {@link SpreadsheetTextFormatterPattern} patterns.
 */
final class SpreadsheetTextFormatterPatternSpreadsheetMetadataPropertyValueHandler extends SpreadsheetMetadataPropertyValueHandler<SpreadsheetTextFormatterPattern> {

    /**
     * Singleton
     */
    final static SpreadsheetTextFormatterPatternSpreadsheetMetadataPropertyValueHandler INSTANCE = new SpreadsheetTextFormatterPatternSpreadsheetMetadataPropertyValueHandler();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetTextFormatterPatternSpreadsheetMetadataPropertyValueHandler() {
        super();
    }

    @Override
    final void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        this.checkType(value, SpreadsheetTextFormatterPattern.class, name);
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return SpreadsheetTextFormatterPattern.class.getSimpleName();
    }

    // ToString.........................................................................................................

    @Override
    public final String toString() {
        return SpreadsheetTextFormatterPattern.class.getSimpleName();
    }

    // HasJsonNode......................................................................................................

    @Override
    final SpreadsheetTextFormatterPattern fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name) {
        return node.fromJsonNode(SpreadsheetTextFormatterPattern.class);
    }

    @Override
    final JsonNode toJsonNode(final SpreadsheetTextFormatterPattern value) {
        return value.toJsonNode();
    }
}
