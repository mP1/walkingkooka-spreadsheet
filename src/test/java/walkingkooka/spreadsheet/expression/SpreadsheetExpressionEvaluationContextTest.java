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
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.SpreadsheetErrorKind;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;

public final class SpreadsheetExpressionEvaluationContextTest implements ClassTesting<SpreadsheetExpressionEvaluationContext> {

    @Test
    public void testResolveOrFailPresentNotNull() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");
        final Object value = "abc123";

        this.referenceOrFailAndCheck(
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<Optional<Object>> reference(final ExpressionReference r) {
                        assertSame(label, r, "reference");
                        return Optional.of(
                                Optional.of(value)
                        );
                    }
                },
                label,
                value
        );
    }

    @Test
    public void testResolveOrFailPresentNull() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        this.referenceOrFailAndCheck(
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<Optional<Object>> reference(final ExpressionReference r) {
                        assertSame(label, r, "reference");
                        return Optional.of(
                                Optional.empty()
                        );
                    }
                },
                label,
                null
        );
    }

    @Test
    public void testResolveOrFailAbsent() {
        final SpreadsheetLabelName label = SpreadsheetSelection.labelName("Label123");

        this.referenceOrFailAndCheck(
                new FakeSpreadsheetExpressionEvaluationContext() {
                    @Override
                    public Optional<Optional<Object>> reference(final ExpressionReference r) {
                        assertSame(label, r, "reference");
                        return Optional.empty();
                    }
                },
                label,
                SpreadsheetErrorKind.REF.setMessage("Reference not found: " + label)
        );
    }

    private void referenceOrFailAndCheck(final SpreadsheetExpressionEvaluationContext context,
                                         final ExpressionReference reference,
                                         final Object expected) {
        this.checkEquals(
                expected,
                context.referenceOrFail(reference),
                () -> "referenceOrFail " + reference
        );
    }

    @Override
    public Class<SpreadsheetExpressionEvaluationContext> type() {
        return SpreadsheetExpressionEvaluationContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
