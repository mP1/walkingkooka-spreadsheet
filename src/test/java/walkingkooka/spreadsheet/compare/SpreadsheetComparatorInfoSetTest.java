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
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.net.http.server.hateos.HateosResourceSetTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

public final class SpreadsheetComparatorInfoSetTest implements HateosResourceSetTesting<SpreadsheetComparatorInfoSet, SpreadsheetComparatorInfo, SpreadsheetComparatorName>,
        ClassTesting<SpreadsheetComparatorInfoSet> {

    // Set..............................................................................................................

    @Override
    public SpreadsheetComparatorInfoSet createSet() {
        return SpreadsheetComparatorInfoSet.with(
                SpreadsheetComparatorProviders.builtIn()
                        .spreadsheetComparatorInfos()
        );
    }

    // json.............................................................................................................

    @Test
    public void testMarshallEmpty() {
        this.marshallAndCheck(
                SpreadsheetComparatorInfoSet.with(Sets.empty()),
                JsonNode.array()
        );
    }

    @Test
    public void testMarshallNotEmpty2() {
        final SpreadsheetComparatorInfoSet set = SpreadsheetComparatorInfoSet.with(
                Sets.of(
                        SpreadsheetComparatorInfo.with(
                                Url.parseAbsolute("https://example.com/test123"),
                                SpreadsheetComparatorName.with("test123")
                        )
                )
        );

        this.marshallAndCheck(
                set,
                "[\n" +
                        "  {\n" +
                        "    \"url\": \"https://example.com/test123\",\n" +
                        "    \"name\": \"test123\"\n" +
                        "  }\n" +
                        "]"
        );
    }

    // json............................................................................................................

    @Override
    public SpreadsheetComparatorInfoSet unmarshall(final JsonNode node,
                                                   final JsonNodeUnmarshallContext context) {
        return SpreadsheetComparatorInfoSet.unmarshall(
                node,
                context
        );
    }

    @Override
    public SpreadsheetComparatorInfoSet createJsonNodeMarshallingValue() {
        return SpreadsheetComparatorInfoSet.with(
                Sets.of(
                        SpreadsheetComparatorInfo.with(
                                Url.parseAbsolute("https://example.com/test111"),
                                SpreadsheetComparatorName.with("test111")
                        ),
                        SpreadsheetComparatorInfo.with(
                                Url.parseAbsolute("https://example.com/test222"),
                                SpreadsheetComparatorName.with("test222")
                        )
                )
        );
    }

    // Class............................................................................................................

    @Override
    public Class<SpreadsheetComparatorInfoSet> type() {
        return SpreadsheetComparatorInfoSet.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
