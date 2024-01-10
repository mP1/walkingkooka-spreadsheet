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

package walkingkooka.spreadsheet;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitorTesting;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;

public final class ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetSelectionVisitorTest implements
        SpreadsheetSelectionVisitorTesting<ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetSelectionVisitor>,
        ToStringTesting<ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetSelectionVisitor> {

    @Test
    public void testToString() {
        final ExpressionReferenceSpreadsheetCellReferenceFunction f = ExpressionReferenceSpreadsheetCellReferenceFunction.with(SpreadsheetLabelStores.fake(),
                SpreadsheetCellRangeStores.fake());
        this.toStringAndCheck(new ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetSelectionVisitor(f), f + " null");
    }

    @Override
    public ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetSelectionVisitor createVisitor() {
        return new ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetSelectionVisitor(null);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return ExpressionReferenceSpreadsheetCellReferenceFunction.class.getSimpleName();
    }

    @Override
    public Class<ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetSelectionVisitor> type() {
        return ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetSelectionVisitor.class;
    }
}
