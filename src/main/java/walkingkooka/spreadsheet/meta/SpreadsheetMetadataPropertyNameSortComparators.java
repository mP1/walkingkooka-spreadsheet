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
import walkingkooka.spreadsheet.compare.SpreadsheetComparatorNameList;

import java.util.Optional;

/**
 * This property holds active comparators to be used by any SORT operation. This list will also be used when building the
 * SORT menu ITEMS after the user has selected several columns/rows/cells.
 */
final class SpreadsheetMetadataPropertyNameSortComparators extends SpreadsheetMetadataPropertyName<SpreadsheetComparatorNameList> {

    /**
     * Singleton
     */
    static SpreadsheetMetadataPropertyNameSortComparators instance() {
        return new SpreadsheetMetadataPropertyNameSortComparators();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameSortComparators() {
        super();
    }

    @Override
    SpreadsheetComparatorNameList checkValueNonNull(final Object value) {
        return this.checkValueType(
                value,
                v -> v instanceof SpreadsheetComparatorNameList
        );
    }

    @Override
    String expected() {
        return SpreadsheetComparatorNameList.class.getSimpleName();
    }

    @Override
    void accept(final SpreadsheetComparatorNameList value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitSortComparators(value);
    }

    @Override
    Optional<SpreadsheetComparatorNameList> extractLocaleAwareValue(final LocaleContext context) {
        return Optional.empty();
    }

    @Override
    public Class<SpreadsheetComparatorNameList> type() {
        return SpreadsheetComparatorNameList.class;
    }

    // parseUrlFragmentSaveValue........................................................................................

    @Override
    SpreadsheetComparatorNameList parseUrlFragmentSaveValueNonNull(final String value) {
        return SpreadsheetComparatorNameList.parse(value);
    }
}
