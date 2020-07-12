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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetNumberFormatPattern;

import java.util.function.Predicate;

/**
 * A {@link SpreadsheetMetadataPropertyValueHandler} for valid {@link SpreadsheetNumberFormatPattern pattern}.
 */
final class SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberFormatPattern extends SpreadsheetMetadataPropertyValueHandlerSpreadsheetPattern<SpreadsheetNumberFormatPattern> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberFormatPattern INSTANCE = new SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberFormatPattern();

    /**
     * Private ctor
     */
    private SpreadsheetMetadataPropertyValueHandlerSpreadsheetNumberFormatPattern() {
        super();
    }

    @Override
    Predicate<Object> valueTypeChecker() {
        return v -> v instanceof SpreadsheetNumberFormatPattern;
    }

    @Override
    Class<SpreadsheetNumberFormatPattern> valueType() {
        return SpreadsheetNumberFormatPattern.class;
    }
}
