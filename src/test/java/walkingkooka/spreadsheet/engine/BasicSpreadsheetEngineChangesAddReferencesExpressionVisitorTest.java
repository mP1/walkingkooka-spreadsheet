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
import walkingkooka.tree.expression.Expression;
import walkingkooka.tree.expression.ExpressionVisitor;
import walkingkooka.tree.expression.ExpressionVisitorTesting;

public final class BasicSpreadsheetEngineChangesAddReferencesExpressionVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor>
        implements ExpressionVisitorTesting<BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor> {

    @Test
    public void testProcessReferences() {
        BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor.processReferences(
                Expression.value("abc123"),
                null,
                null
        );
    }

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return ExpressionVisitor.class.getSimpleName();
    }

    // ClassTesting..........................................................................

    @Override
    public Class<BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor> type() {
        return BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor.class;
    }

    // VisitingTesting....................................................................................

    @Override
    public BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor createVisitor() {
        return new BasicSpreadsheetEngineChangesAddReferencesExpressionVisitor(null, null);
    }
}
