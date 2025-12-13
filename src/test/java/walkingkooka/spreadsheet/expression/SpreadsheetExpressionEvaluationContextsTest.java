
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

package walkingkooka.spreadsheet.expression;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.PublicStaticHelperTesting;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetErrorKind;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.tree.expression.FakeExpressionReference;

import java.lang.reflect.Method;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetExpressionEvaluationContextsTest implements ClassTesting2<SpreadsheetExpressionEvaluationContexts>,
    PublicStaticHelperTesting<SpreadsheetExpressionEvaluationContexts> {

    @Test
    public void testPublicStaticMethodsWithoutMathContextParameter() {
        this.publicStaticMethodParametersTypeCheck(MathContext.class);
    }

    // referenceNotFound................................................................................................

    @Test
    public void testReferenceNotFoundExpressionReference() {
        final ExpressionReference reference = new FakeExpressionReference() {

            @Override
            public String toString() {
                return "TestReference!";
            }
        };

        this.referenceNotFoundAndCheck(
            reference,
            "Unknown reference: " + reference);
    }

    @Test
    public void testReferenceNotFoundCell() {
        this.referenceNotFoundAndCheck(
            SpreadsheetSelection.parseCell("B2"),
            "Cell not found: \"B2\"");
    }

    @Test
    public void testReferenceNotFoundCellRange() {
        this.referenceNotFoundAndCheck(
            SpreadsheetSelection.parseCellRange("B2:C3"),
            "Cell Range not found: \"B2:C3\""
        );
    }


    @Test
    public void testReferenceNotFoundLabel() {
        this.referenceNotFoundAndCheck(
            SpreadsheetSelection.labelName("Label123"),
            "Label not found: \"Label123\""
        );
    }

    private void referenceNotFoundAndCheck(final ExpressionReference reference,
                                           final String expected) {
        final SpreadsheetExpressionEvaluationReferenceException created = (SpreadsheetExpressionEvaluationReferenceException)
            SpreadsheetExpressionEvaluationContexts.referenceNotFound()
                .apply(reference);

        this.checkEquals(
            expected,
            created.getMessage(),
            created::getMessage
        );

        assertSame(
            SpreadsheetErrorKind.NAME,
            created.spreadsheetErrorKind(),
            created::getMessage
        );
    }

    // ClassTesting2....................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContexts> type() {
        return SpreadsheetExpressionEvaluationContexts.class;
    }

    @Override
    public boolean canHavePublicTypes(final Method method) {
        return false;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
