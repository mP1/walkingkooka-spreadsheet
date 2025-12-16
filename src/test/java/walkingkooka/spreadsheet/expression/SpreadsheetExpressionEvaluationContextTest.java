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
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.FieldAttributes;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.value.SpreadsheetCell;
import walkingkooka.spreadsheet.value.SpreadsheetError;
import walkingkooka.template.TemplateValueName;
import walkingkooka.tree.expression.ExpressionReference;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetExpressionEvaluationContextTest implements ClassTesting<SpreadsheetExpressionEvaluationContext> {

    // isSpreadsheetEnvironmentContextEnvironmentValueName..............................................................

    @Test
    public void testIsSpreadsheetEnvironmentContextEnvironmentValueNameWithNull() {
        this.isSpreadsheetEnvironmentContextEnvironmentValueNameAndCheck(
            null,
            false
        );
    }

    @Test
    public void testIsSpreadsheetEnvironmentContextEnvironmentValueNameWithLocale() {
        this.isSpreadsheetEnvironmentContextEnvironmentValueNameAndCheck(
            EnvironmentValueName.LOCALE,
            true
        );
    }

    @Test
    public void testIsSpreadsheetEnvironmentContextEnvironmentValueNameWithUser() {
        this.isSpreadsheetEnvironmentContextEnvironmentValueNameAndCheck(
            EnvironmentValueName.USER,
            false
        );
    }

    @Test
    public void testIsSpreadsheetEnvironmentContextEnvironmentValueNameWithSpreadsheetId() {
        this.isSpreadsheetEnvironmentContextEnvironmentValueNameAndCheck(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            false
        );
    }

    @Test
    public void testIsSpreadsheetEnvironmentContextEnvironmentValueNameWithConverter() {
        this.isSpreadsheetEnvironmentContextEnvironmentValueNameAndCheck(
            SpreadsheetExpressionEvaluationContext.CONVERTER,
            true
        );
    }

    @Test
    public void testIsSpreadsheetEnvironmentContextEnvironmentValueNameWithRoundingMode() {
        this.isSpreadsheetEnvironmentContextEnvironmentValueNameAndCheck(
            SpreadsheetExpressionEvaluationContext.ROUNDING_MODE,
            true
        );
    }

    @Test
    public void testIsSpreadsheetEnvironmentContextEnvironmentValueNameWithAllConstants() throws Exception {
        int i = 0;

        for(final Field field : SpreadsheetExpressionEvaluationContext.class.getDeclaredFields()) {
            if(FieldAttributes.STATIC.is(field) && field.getType() == EnvironmentValueName.class) {
                this.isSpreadsheetEnvironmentContextEnvironmentValueNameAndCheck(
                    (EnvironmentValueName<?>) field.get(null),
                    true
                );
                i++;
            }
        }

        this.checkNotEquals(
            0,
            i,
            "no constants of type " + EnvironmentValueName.class.getName() + " found"
        );
    }

    private void isSpreadsheetEnvironmentContextEnvironmentValueNameAndCheck(final EnvironmentValueName<?> name,
                                                                             final boolean expected) {
        this.checkEquals(
            expected,
            SpreadsheetExpressionEvaluationContext.isSpreadsheetEnvironmentContextEnvironmentValueName(name),
            () -> String.valueOf(name)
        );
    }

    @Test
    public void testAddLocalVariableWithNullReferenceFails() {
        assertThrows(
            NullPointerException.class,
            () -> new FakeSpreadsheetExpressionEvaluationContext()
                .addLocalVariable(
                    null,
                    Optional.empty()
                )
        );
    }

    @Test
    public void testAddLocalVariableWithNullValueFails() {
        assertThrows(
            NullPointerException.class,
            () -> new FakeSpreadsheetExpressionEvaluationContext()
                .addLocalVariable(
                    SpreadsheetSelection.labelName("Hello"),
                    null
                )
        );
    }

    @Test
    public void testCellOrFail() {
        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> new FakeSpreadsheetExpressionEvaluationContext() {
                @Override
                public Optional<SpreadsheetCell> cell() {
                    return Optional.empty();
                }
            }.cellOrFail()
        );

        this.checkEquals(
            "Missing cell",
            thrown.getMessage()
        );
    }

    @Test
    public void testReferenceOrFailPresentNotNull() {
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
    public void testReferenceOrFailPresentNull() {
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
    public void testReferenceOrFailAbsent() {
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
            SpreadsheetError.selectionNotFound(label)
        );
    }

    @Test
    public void testReferenceOrFailAbsentWhenNotSpreadsheetExpressionReference() {
        final TemplateValueName notSpreadsheetExpressionReference = TemplateValueName.with("TemplateValueName123");

        this.referenceOrFailAndCheck(
            new FakeSpreadsheetExpressionEvaluationContext() {
                @Override
                public Optional<Optional<Object>> reference(final ExpressionReference r) {
                    assertSame(notSpreadsheetExpressionReference, r, "reference");
                    return Optional.empty();
                }
            },
            notSpreadsheetExpressionReference,
            SpreadsheetError.referenceNotFound(notSpreadsheetExpressionReference)
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

    // class............................................................................................................

    @Override
    public Class<SpreadsheetExpressionEvaluationContext> type() {
        return SpreadsheetExpressionEvaluationContext.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
