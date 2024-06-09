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
import walkingkooka.net.UrlPath;
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
    public void testSpreadsheetComparatorInfos() {
        this.spreadsheetComparatorInfosAndCheck(
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
                                SpreadsheetComparatorProviders.BASE_URL.appendPath(
                                        UrlPath.parse(n)
                                ),
                                        SpreadsheetComparatorName.with(n)
                                )
                        ).collect(Collectors.toSet())
        );
    }

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                SpreadsheetComparatorInfoSet.with(
                        BuiltInSpreadsheetComparatorProvider.INSTANCE.spreadsheetComparatorInfos()
                ),
                "SpreadsheetComparatorInfoSet\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date date\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date-time date-time\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-month day-of-month\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-week day-of-week\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-am-pm hour-of-am-pm\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-day hour-of-day\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/minute-of-hour minute-of-hour\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/month-of-year month-of-year\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/nano-of-second nano-of-second\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/number number\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/seconds-of-minute seconds-of-minute\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text text\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text-case-insensitive text-case-insensitive\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/time time\n" +
                        "  https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/year year\n"
        );
    }

    @Override
    public BuiltInSpreadsheetComparatorProvider createSpreadsheetComparatorProvider() {
        return BuiltInSpreadsheetComparatorProvider.INSTANCE;
    }

    // ClassTesting.....................................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<BuiltInSpreadsheetComparatorProvider> type() {
        return BuiltInSpreadsheetComparatorProvider.class;
    }
}
