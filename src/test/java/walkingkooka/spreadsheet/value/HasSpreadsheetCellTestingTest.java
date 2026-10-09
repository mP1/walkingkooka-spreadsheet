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

package walkingkooka.spreadsheet.value;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.PublicClassTesting;

public final class HasSpreadsheetCellTestingTest implements HasSpreadsheetCellTesting,
    PublicClassTesting<HasSpreadsheetCellTesting> {

    @Test
    public void testConstants() {
        this.checkNotEquals(
            SPREADSHEET_CELL,
            DIFFERENT_SPREADSHEET_CELL
        );
    }

    @Test
    public void testOptionalConstants() {
        this.checkNotEquals(
            OPTIONAL_SPREADSHEET_CELL,
            OPTIONAL_DIFFERENT_SPEADSHEET_CELL
        );
    }

    @Test
    public void testConstantAndOptionalConstants() {
        this.checkEquals(
            SPREADSHEET_CELL,
            OPTIONAL_SPREADSHEET_CELL.get()
        );
    }

    @Test
    public void testConstantAndOptionalConstants2() {
        this.checkEquals(
            DIFFERENT_SPREADSHEET_CELL,
            OPTIONAL_DIFFERENT_SPEADSHEET_CELL.get()
        );
    }

    @Test
    public void testConstantHasSpreadsheetCell() {
        this.checkNotEquals(
            HAS_SPREADSHEET_CELL,
            DIFFERENT_HAS_SPREADSHEET_CELL
        );
    }

    @Test
    public void testHasSpreadsheetCellConstants() {
        this.spreadsheetCellAndCheck(
            HAS_SPREADSHEET_CELL,
            SPREADSHEET_CELL
        );
    }

    @Test
    public void testDifferentHasSpreadsheetCellConstants() {
        this.spreadsheetCellAndCheck(
            DIFFERENT_HAS_SPREADSHEET_CELL,
            DIFFERENT_SPREADSHEET_CELL
        );
    }

    @Test
    public void testEmptyHasSpreadsheetCell() {
        this.spreadsheetCellAndCheck(
            HasSpreadsheetCell.EMPTY_HAS_SPREADSHEET_CELL
        );
    }

    // class............................................................................................................

    @Override
    public Class<HasSpreadsheetCellTesting> type() {
        return HasSpreadsheetCellTesting.class;
    }
}
