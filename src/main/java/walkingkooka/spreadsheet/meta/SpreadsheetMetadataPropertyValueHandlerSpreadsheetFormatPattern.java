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

import walkingkooka.spreadsheet.format.SpreadsheetFormatPattern;
import walkingkooka.tree.json.JsonNode;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for valid {@link SpreadsheetFormatPattern} patterns.
 */
final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern extends SpreadsheetMetadataPropertyValueHandler<SpreadsheetFormatPattern> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern INSTANCE = new SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPattern() {
        super();
    }

    @Override
    final void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        SpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatPatternSpreadsheetFormatParserTokenVisitor.check(name,
                this.checkType(value, SpreadsheetFormatPattern.class, name));
    }

    @Override
    String expectedTypeName(final Class<?> type) {
        return SpreadsheetFormatPattern.class.getSimpleName();
    }

    // ToString.........................................................................................................

    @Override
    public final String toString() {
        return SpreadsheetFormatPattern.class.getSimpleName();
    }

    // HasJsonNode......................................................................................................

    @Override
    final SpreadsheetFormatPattern fromJsonNode(final JsonNode node, final SpreadsheetMetadataPropertyName<?> name) {
        return node.fromJsonNode(SpreadsheetFormatPattern.class);
    }

    @Override
    final JsonNode toJsonNode(final SpreadsheetFormatPattern value) {
        return value.toJsonNode();
    }
}
