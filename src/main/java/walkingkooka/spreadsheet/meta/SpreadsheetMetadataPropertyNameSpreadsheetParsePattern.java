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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern;

/**
 * Base class for all {@link walkingkooka.spreadsheet.format.pattern.SpreadsheetParsePattern} properties.
 */
abstract class SpreadsheetMetadataPropertyNameSpreadsheetParsePattern<P extends SpreadsheetParsePattern> extends SpreadsheetMetadataPropertyName<P> {

    SpreadsheetMetadataPropertyNameSpreadsheetParsePattern(final String name) {
        super(name);
    }

    @Override
    final String compareToName() {
        return this.value();
    }

    // parseValue.......................................................................................................

    @Override
    public final boolean isParseValueSupported() {
        return true;
    }
}
