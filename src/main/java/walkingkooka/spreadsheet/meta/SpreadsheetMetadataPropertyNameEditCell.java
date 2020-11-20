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

/**
 * Holds the cell currently being edited.
 */
final class SpreadsheetMetadataPropertyNameEditCell extends SpreadsheetMetadataPropertyNameSpreadsheetCellReference {

    /**
     * Singleton
     */
    final static SpreadsheetMetadataPropertyNameEditCell instance() {
        return new SpreadsheetMetadataPropertyNameEditCell();
    }

    /**
     * Private constructor use singleton.
     */
    private SpreadsheetMetadataPropertyNameEditCell() {
        super("edit-cell");
    }

    @Override
    void accept(final SpreadsheetCellReference value,
                final SpreadsheetMetadataVisitor visitor) {
        visitor.visitEditCell(value);
    }
}
