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
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.test.ToStringTesting;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.VisitorTesting;
import walkingkooka.type.JavaVisibility;

public final class ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitorTest implements
        SpreadsheetExpressionReferenceVisitorTesting<ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor>,
        ToStringTesting<ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor> {

    @Test
    public void testToString() {
        final ExpressionReferenceSpreadsheetCellReferenceFunction f = ExpressionReferenceSpreadsheetCellReferenceFunction.with(SpreadsheetLabelStores.fake(),
                SpreadsheetRangeStores.fake());
        this.toStringAndCheck(new ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor(f), f.toString() + " null");
    }

    @Override
    public ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor(null);
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
    public Class<ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor> type() {
        return ExpressionReferenceSpreadsheetCellReferenceFunctionSpreadsheetExpressionReferenceVisitor.class;
    }
}
