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

import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserAliasSet;

/**
 * This {@link SpreadsheetMetadataPropertyName} holds a {@link SpreadsheetParserAliasSet}. These aliases will
 * map and filter which parsers are available to a single spreadsheet.
 */
final class SpreadsheetMetadataPropertyNameSpreadsheetParserAliasSetParsers extends SpreadsheetMetadataPropertyNameSpreadsheetParserAliasSet {

    /**
     * Factory, the constant on {@link SpreadsheetMetadataPropertyName} will hold the singleton.
     */
    static SpreadsheetMetadataPropertyNameSpreadsheetParserAliasSetParsers instance() {
        return new SpreadsheetMetadataPropertyNameSpreadsheetParserAliasSetParsers();
    }

    /**
     * Private ctor use getter.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetParserAliasSetParsers() {
        super("parsers");
    }

    @Override
    void accept(final SpreadsheetParserAliasSet aliases,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitParsers(aliases);
    }
}
