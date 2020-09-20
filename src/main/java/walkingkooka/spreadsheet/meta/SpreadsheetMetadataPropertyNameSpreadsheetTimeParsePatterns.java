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

import walkingkooka.spreadsheet.format.pattern.SpreadsheetTimeParsePatterns;

final class SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns extends SpreadsheetMetadataPropertyName<SpreadsheetTimeParsePatterns> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns INSTANCE = new SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns();

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSpreadsheetTimeParsePatterns() {
        super("time-parse-patterns");
    }

    @Override
    void checkValue0(final Object value) {
        this.checkValueType(value,
                v -> v instanceof SpreadsheetTimeParsePatterns);
    }

    @Override
    String expected() {
        return "Time parse patterns";
    }

    @Override
    void accept(final SpreadsheetTimeParsePatterns value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitTimeParsePatterns(value);
    }

    @Override
    Class<SpreadsheetTimeParsePatterns> type() {
        return SpreadsheetTimeParsePatterns.class;
    }
}
