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
import walkingkooka.tree.expression.ExpressionNode;
import walkingkooka.tree.expression.ExpressionNodeVisitor;
import walkingkooka.tree.expression.ExpressionNodeVisitorTesting;

public final class BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor>
        implements ExpressionNodeVisitorTesting<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor> {

    @Test
    public void testProcessReferences() {
        BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor.processReferences(ExpressionNode.text("abc123"), null, null);
    }

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return ExpressionNodeVisitor.class.getSimpleName();
    }

    // ClassTesting..........................................................................

    @Override
    public Class<BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor> type() {
        return BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor.class;
    }

    // VisitingTesting....................................................................................

    @Override
    public BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor createVisitor() {
        return new BasicSpreadsheetEngineUpdatedCellAddReferencesExpressionNodeVisitor(null, null);
    }
}
