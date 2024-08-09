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
import walkingkooka.plugin.ProviderContexts;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.MethodAttributes;
import walkingkooka.text.CaseKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class SpreadsheetComparatorsSpreadsheetComparatorProviderTest implements SpreadsheetComparatorProviderTesting<SpreadsheetComparatorsSpreadsheetComparatorProvider> {

    @Test
    public void testSpreadsheetComparator() {
        Arrays.stream(SpreadsheetComparators.class.getMethods())
                .filter(MethodAttributes.STATIC::is)
                .filter(m -> SpreadsheetComparator.class.equals(m.getReturnType()))
                .filter(m -> m.getParameterTypes().length == 0)
                .map(m -> CaseKind.CAMEL.change(
                                m.getName(),
                                CaseKind.KEBAB
                        ).toString()
                ).filter(n -> false == "fake".equals(n))
                .forEach(n -> SpreadsheetComparatorsSpreadsheetComparatorProvider.INSTANCE.spreadsheetComparator(
                                SpreadsheetComparatorName.with(n),
                        ProviderContexts.fake()
                        )
                );
    }

    @Test
    public void testSpreadsheetComparatorInfos() {
        this.spreadsheetComparatorInfosAndCheck(
                SpreadsheetComparatorsSpreadsheetComparatorProvider.INSTANCE,
                Arrays.stream(SpreadsheetComparators.class.getMethods())
                        .filter(MethodAttributes.STATIC::is)
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
                        SpreadsheetComparatorsSpreadsheetComparatorProvider.INSTANCE.spreadsheetComparatorInfos()
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

    @Test
    public void testMarshall() {
        this.checkEquals(
                JsonNode.parse(
                        "[\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date\",\n" +
                                "    \"name\": \"date\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/date-time\",\n" +
                                "    \"name\": \"date-time\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-month\",\n" +
                                "    \"name\": \"day-of-month\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/day-of-week\",\n" +
                                "    \"name\": \"day-of-week\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-am-pm\",\n" +
                                "    \"name\": \"hour-of-am-pm\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/hour-of-day\",\n" +
                                "    \"name\": \"hour-of-day\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/minute-of-hour\",\n" +
                                "    \"name\": \"minute-of-hour\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/month-of-year\",\n" +
                                "    \"name\": \"month-of-year\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/nano-of-second\",\n" +
                                "    \"name\": \"nano-of-second\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/number\",\n" +
                                "    \"name\": \"number\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/seconds-of-minute\",\n" +
                                "    \"name\": \"seconds-of-minute\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text\",\n" +
                                "    \"name\": \"text\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/text-case-insensitive\",\n" +
                                "    \"name\": \"text-case-insensitive\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/time\",\n" +
                                "    \"name\": \"time\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"url\": \"https://github.com/mP1/walkingkooka-spreadsheet/SpreadsheetComparator/year\",\n" +
                                "    \"name\": \"year\"\n" +
                                "  }\n" +
                                "]"
                ),
                JsonNodeMarshallContexts.basic()
                        .marshall(
                                SpreadsheetComparatorInfoSet.with(
                                        SpreadsheetComparatorsSpreadsheetComparatorProvider.INSTANCE.spreadsheetComparatorInfos()
                                )
                        )
        );
    }

    @Override
    public SpreadsheetComparatorsSpreadsheetComparatorProvider createSpreadsheetComparatorProvider() {
        return SpreadsheetComparatorsSpreadsheetComparatorProvider.INSTANCE;
    }

    // ClassTesting.....................................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<SpreadsheetComparatorsSpreadsheetComparatorProvider> type() {
        return SpreadsheetComparatorsSpreadsheetComparatorProvider.class;
    }
}
