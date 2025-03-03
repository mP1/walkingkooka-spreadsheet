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

package walkingkooka.spreadsheet.store;

import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;

import java.util.Objects;

/**
 * The action to perform upon all cells belonging to a Spreadsheet for a given {@link SpreadsheetMetadataPropertyName}
 * change.
 */
public enum SpreadsheetCellStoreAction {

    /**
     * Some {@link SpreadsheetMetadataPropertyName} changes do not require any action to be performed on the spreadsheet cells.
     * <br>
     * An example of this would be a update to {@link SpreadsheetMetadataPropertyName#MODIFIED_DATE_TIME}.
     */
    NONE(0),

    /**
     * Updating the global {@link SpreadsheetMetadataPropertyName#NUMBER_PARSER}, will require all cell formula
     * to be parsed again because different outcomes will be produced for number literals in cells.
     */
    PARSE_FORMULA(1),

    /**
     * Updating the global {@link SpreadsheetMetadataPropertyName#NUMBER_FORMATTER} will require all cell expressions to be re-evaluated and formatted again,
     * because formatting numeric values with the new pattern will have different text results.
     */
    EVALUATE_AND_FORMAT(2);

    SpreadsheetCellStoreAction(final int value) {
        this.value = value;
    }

    public SpreadsheetCellStoreAction max(final SpreadsheetCellStoreAction other) {
        Objects.requireNonNull(other, "other");

        final SpreadsheetCellStoreAction result;

        switch (Math.max(this.value, other.value)) {
            case 0:
                result = NONE;
                break;
            case 1:
                result = PARSE_FORMULA;
                break;
            default:
                result = EVALUATE_AND_FORMAT;
                break;
        }

        return result;
    }

    private final int value;
}
