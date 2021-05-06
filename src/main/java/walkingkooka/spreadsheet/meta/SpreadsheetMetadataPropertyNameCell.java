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


import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrLabelName;

import java.util.Locale;
import java.util.Optional;

/**
 * Holds the cell or label name for that cell currently being edited.
 */
final class SpreadsheetMetadataPropertyNameCell extends SpreadsheetMetadataPropertyName<SpreadsheetCellReferenceOrLabelName> {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameCell instance() {
        return new SpreadsheetMetadataPropertyNameCell();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameCell() {
        super("cell");
    }

    /**
     * After checking the type force the {@link SpreadsheetCellReference#toRelative()}
     */
    @Override
    final SpreadsheetCellReferenceOrLabelName checkValue0(final Object value) {
        return this.checkValueType(value,
                v -> v instanceof SpreadsheetCellReferenceOrLabelName)
                .toRelative();
    }

    @Override
    final String expected() {
        return SpreadsheetCellReference.class.getSimpleName();
    }

    @Override
    final Optional<SpreadsheetCellReferenceOrLabelName> extractLocaleValue(final Locale locale) {
        return Optional.empty();
    }

    @Override
    final Class<SpreadsheetCellReferenceOrLabelName> type() {
        return SpreadsheetCellReferenceOrLabelName.class;
    }

    @Override
    final String compareToName() {
        return this.value();
    }

    @Override
    void accept(final SpreadsheetCellReferenceOrLabelName value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitCell(value);
    }
}
