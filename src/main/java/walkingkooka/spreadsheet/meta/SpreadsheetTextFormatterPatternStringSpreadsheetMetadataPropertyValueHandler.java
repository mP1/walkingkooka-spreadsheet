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

import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatter;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserContexts;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParsers;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserReporters;
import walkingkooka.tree.json.JsonNode;

import java.math.MathContext;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for valid {@link String} {@link SpreadsheetTextFormatter} patterns.
 */
final class SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandler extends SpreadsheetMetadataPropertyValueHandler<String> {

    /**
     * Singleton
     */
    final static SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandler INSTANCE = new SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandler();

    /**
     * Private ctor use singleton
     */
    private SpreadsheetTextFormatterPatternStringSpreadsheetMetadataPropertyValueHandler() {
        super();
    }

    @Override
    final void check0(final Object value, final SpreadsheetMetadataPropertyName<?> name) {
        final String pattern = this.checkType(value, String.class, name);
        if (!pattern.isEmpty()) {
            try {
                SpreadsheetFormatParsers.expression()
                        .orFailIfCursorNotEmpty(ParserReporters.basic())
                        .parse(TextCursors.charSequence(pattern), SpreadsheetFormatParserContexts.basic(this.decimalNumberContext));
            } catch (final RuntimeException cause) {
                throw new SpreadsheetMetadataPropertyValueException(cause.getMessage(), name, value);
            }
        }
    }

    // is it safe to assume a DecimalNumberContexts.american
    private final DecimalNumberContext decimalNumberContext = DecimalNumberContexts.american(MathContext.DECIMAL32);

    @Override
    String expectedTypeName(final Class<?> type) {
        return String.class.getSimpleName();
    }

    // ToString.........................................................................................................

    @Override
    public final String toString() {
        return "pattern";
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
