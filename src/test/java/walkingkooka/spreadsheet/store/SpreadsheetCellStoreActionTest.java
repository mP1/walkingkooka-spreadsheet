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

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;

public final class SpreadsheetCellStoreActionTest implements ClassTesting<SpreadsheetCellStoreAction> {

    // max..............................................................................................................

    @Test
    public void testMaxNoneNone() {
        this.maxAndCheck(
            SpreadsheetCellStoreAction.NONE,
            SpreadsheetCellStoreAction.NONE,
            SpreadsheetCellStoreAction.NONE
        );
    }

    @Test
    public void testMaxNoneParseFormula() {
        this.maxAndCheck(
            SpreadsheetCellStoreAction.NONE,
            SpreadsheetCellStoreAction.PARSE_FORMULA,
            SpreadsheetCellStoreAction.PARSE_FORMULA
        );
    }

    @Test
    public void testMaxNoneEvaluateAndFormat() {
        this.maxAndCheck(
            SpreadsheetCellStoreAction.NONE,
            SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT,
            SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT
        );
    }

    @Test
    public void testMaxParseFormulaEvaluateAndFormat() {
        this.maxAndCheck(
            SpreadsheetCellStoreAction.PARSE_FORMULA,
            SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT,
            SpreadsheetCellStoreAction.EVALUATE_AND_FORMAT
        );
    }

    private void maxAndCheck(final SpreadsheetCellStoreAction first,
                             final SpreadsheetCellStoreAction second,
                             final SpreadsheetCellStoreAction expected) {
        this.maxAndCheck0(
            first,
            second,
            expected
        );
        this.maxAndCheck0(
            second,
            first,
            expected
        );
    }

    private void maxAndCheck0(final SpreadsheetCellStoreAction first,
                              final SpreadsheetCellStoreAction second,
                              final SpreadsheetCellStoreAction expected) {
        this.checkEquals(
            expected,
            first.max(second),
            first + " max " + second
        );
    }

    // ClassTesting....................................................................................................

    @Override
    public Class<SpreadsheetCellStoreAction> type() {
        return SpreadsheetCellStoreAction.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
