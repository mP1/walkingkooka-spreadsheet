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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.net.Url;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.MethodAttributes;
import walkingkooka.text.CaseKind;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class BuiltInSpreadsheetComparatorProviderTest implements SpreadsheetComparatorProviderTesting<BuiltInSpreadsheetComparatorProvider> {

    @Test
    public void testSpreadsheetComparator() {
        Arrays.stream(SpreadsheetComparators.class.getMethods())
                .filter(m -> MethodAttributes.STATIC.is(m))
                .filter(m -> SpreadsheetComparator.class.equals(m.getReturnType()))
                .filter(m -> m.getParameterTypes().length == 0)
                .map(m -> CaseKind.CAMEL.change(
                                m.getName(),
                                CaseKind.KEBAB
                        ).toString()
                ).filter(n -> false == "fake".equals(n))
                .forEach(n -> BuiltInSpreadsheetComparatorProvider.INSTANCE.spreadsheetComparator(
                                SpreadsheetComparatorName.with(n)
                        )
                );
    }

    @Test
    public void testSpreadsheetComparators() {
        this.spreadsheetComparatorsAndCheck(
                BuiltInSpreadsheetComparatorProvider.INSTANCE,
                Arrays.stream(SpreadsheetComparators.class.getMethods())
                        .filter(m -> MethodAttributes.STATIC.is(m))
                        .filter(m -> SpreadsheetComparator.class.equals(m.getReturnType()))
                        .filter(m -> m.getParameterTypes().length == 0)
                        .map(m -> CaseKind.CAMEL.change(
                                        m.getName(),
                                        CaseKind.KEBAB
                                ).toString()
                        ).filter(n -> false == "fake".equals(n))
                        .map(n -> SpreadsheetComparatorInfo.with(
                                        Url.parseAbsolute("https://github.com/mP1/walkingkooka-spreadsheet/" + n),
                                        SpreadsheetComparatorName.with(n)
                                )
                        ).collect(Collectors.toSet())
        );
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<BuiltInSpreadsheetComparatorProvider> type() {
        return BuiltInSpreadsheetComparatorProvider.class;
    }
}