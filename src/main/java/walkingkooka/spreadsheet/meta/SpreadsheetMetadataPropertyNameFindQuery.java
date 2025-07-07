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


import walkingkooka.locale.LocaleContext;
import walkingkooka.spreadsheet.engine.SpreadsheetCellQuery;

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
    SpreadsheetCellQuery checkValueNonNull(final Object value) {
        return this.checkValueType(value,
            v -> v instanceof SpreadsheetCellQuery);
    }

    @Override
    String expected() {
        return SpreadsheetCellQuery.class.getSimpleName();
    }

    @Override
    Optional<SpreadsheetCellQuery> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<SpreadsheetCellQuery> type() {
        return SpreadsheetCellQuery.class;
    }

    @Override
    void accept(final SpreadsheetCellQuery value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitFindQuery(value);
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    SpreadsheetCellQuery parseUrlFragmentSaveValueNonNull(final String value) {
        return SpreadsheetCellQuery.parse(value);
    }
}
