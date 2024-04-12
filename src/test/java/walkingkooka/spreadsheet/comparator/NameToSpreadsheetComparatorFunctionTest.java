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

package walkingkooka.spreadsheet.comparator;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.MethodAttributes;
import walkingkooka.text.CaseKind;
import walkingkooka.util.FunctionTesting;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class NameToSpreadsheetComparatorFunctionTest implements FunctionTesting<NameToSpreadsheetComparatorFunction, String, SpreadsheetComparator<?>>,
        ClassTesting<NameToSpreadsheetComparatorFunction> {

    @Test
    public void testApplyNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createFunction().apply(null)
        );
    }

    @Test
    public void testApplyEmptyStringFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createFunction().apply("")
        );
    }

    @Test
    public void testApplyUnknownFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> this.createFunction().apply("???")
        );
    }

    @Test
    public void testApply() {
        this.checkEquals(
                Sets.empty(),
                Arrays.stream(SpreadsheetComparators.class.getDeclaredMethods())
                        .filter(m -> JavaVisibility.of(m).equals(JavaVisibility.PUBLIC))
                        .filter(MethodAttributes.STATIC::is)
                        .filter(m -> m.getParameterTypes().length == 0)
                        .filter(m -> m.getReturnType() == SpreadsheetComparator.class)
                        .filter(m -> false == m.getName().equals("fake"))
                        .filter(m -> {
                            boolean fails;
                            try {
                                this.createFunction()
                                        .apply(
                                                CaseKind.CAMEL.change(
                                                        m.getName(),
                                                        CaseKind.KEBAB
                                                )
                                        );
                                fails = false;
                            } catch (final RuntimeException ignore) {
                                fails = true;
                            }
                            return fails;
                        }).map(m -> m.toGenericString())
                        .collect(Collectors.toCollection(Sets::sorted))
        );
    }

    @Override
    public Class<NameToSpreadsheetComparatorFunction> type() {
        return NameToSpreadsheetComparatorFunction.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public NameToSpreadsheetComparatorFunction createFunction() {
        return NameToSpreadsheetComparatorFunction.INSTANCE;
    }
}
