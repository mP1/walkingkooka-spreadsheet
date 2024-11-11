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


import walkingkooka.spreadsheet.engine.SpreadsheetCellQuery;

import java.util.Locale;
import java.util.Optional;

/**
 * Holds the {@link SpreadsheetCellQuery}
 */
final class SpreadsheetMetadataPropertyNameFindQuery extends SpreadsheetMetadataPropertyName<SpreadsheetCellQuery> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameFindQuery instance() {
        return new SpreadsheetMetadataPropertyNameFindQuery();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameFindQuery() {
        super();
    }

    /**
     * After checking the type force the {@link SpreadsheetCellQuery}
     */
    @Override
    SpreadsheetCellQuery checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetCellQuery);
    }

    @Override
    String expected() {
        return SpreadsheetCellQuery.class.getSimpleName();
    }

    @Override
    Optional<SpreadsheetCellQuery> extractLocaleAwareValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    Class<SpreadsheetCellQuery> type() {
        return SpreadsheetCellQuery.class;
    }

    @Override
    void accept(final SpreadsheetCellQuery value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitFindQuery(value);
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    public SpreadsheetCellQuery parseUrlFragmentSaveValue0(final String value) {
        return SpreadsheetCellQuery.parse(value);
    }
}
