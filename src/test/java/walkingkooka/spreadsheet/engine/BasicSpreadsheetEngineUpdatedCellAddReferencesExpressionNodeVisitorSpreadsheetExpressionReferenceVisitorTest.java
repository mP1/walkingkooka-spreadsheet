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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.visit.VisitorTesting;

public final class BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor>
        implements VisitorTesting<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor, ExpressionReference> {

    private final static SpreadsheetCellReference CELL = SpreadsheetCellReference.parse("A99");

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createVisitor(), CELL.toString());
    }

    @Override
    public String typeNameSuffix() {
        return SpreadsheetExpressionReferenceVisitor.class.getSimpleName();
    }

    @Override
    public Class<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor> type() {
        return BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor.class;
    }

    // VisitingTesting.............................................................................................................

    @Override
    public BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor createVisitor() {
        return new BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorSpreadsheetExpressionReferenceVisitor(CELL, null);
    }
}
