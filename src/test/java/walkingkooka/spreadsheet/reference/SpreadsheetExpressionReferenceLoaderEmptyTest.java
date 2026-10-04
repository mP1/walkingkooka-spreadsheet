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

package walkingkooka.spreadsheet.reference;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContexts;

public final class SpreadsheetExpressionReferenceLoaderEmptyTest implements SpreadsheetExpressionReferenceLoaderTesting2<SpreadsheetExpressionReferenceLoaderEmpty> {

    private final static SpreadsheetCellReference CELL = SpreadsheetSelection.A1;

    @Test
    public void testLoadCell() {
        this.loadCellAndCheck(
            SpreadsheetExpressionReferenceLoaderEmpty.INSTANCE,
            CELL,
            this.createContext()
        );
    }

    @Test
    public void testLoadCellRange() {
        this.loadCellRangeAndCheck(
            SpreadsheetExpressionReferenceLoaderEmpty.INSTANCE,
            SpreadsheetSelection.parseCellRange("A1:B2"),
            this.createContext()
        );
    }

    @Test
    public void testLoadLabel() {
        this.loadLabelAndCheck(
            SpreadsheetExpressionReferenceLoaderEmpty.INSTANCE,
            SpreadsheetSelection.labelName("Label123")
        );
    }

    @Override
    public SpreadsheetExpressionReferenceLoaderEmpty createSpreadsheetExpressionReferenceLoader() {
        return SpreadsheetExpressionReferenceLoaderEmpty.INSTANCE;
    }

    @Override
    public SpreadsheetExpressionEvaluationContext createContext() {
        return SpreadsheetExpressionEvaluationContexts.fake();
    }
}
