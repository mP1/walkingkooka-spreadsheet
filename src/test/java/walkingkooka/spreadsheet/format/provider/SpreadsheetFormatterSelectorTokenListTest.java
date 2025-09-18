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

package walkingkooka.spreadsheet.format.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.ListTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpreadsheetFormatterSelectorTokenListTest implements ListTesting2<SpreadsheetFormatterSelectorTokenList, SpreadsheetFormatterSelectorToken>,
    ClassTesting<SpreadsheetFormatterSelectorTokenList>,
    ImmutableListTesting<SpreadsheetFormatterSelectorTokenList, SpreadsheetFormatterSelectorToken>,
    JsonNodeMarshallingTesting<SpreadsheetFormatterSelectorTokenList> {

    private final static SpreadsheetFormatterSelectorToken COMPONENT1 = SpreadsheetFormatterSelectorToken.with(
        "label1",
        "text1",
        Lists.of(
            SpreadsheetFormatterSelectorTokenAlternative.with(
                "alternative-label-1",
                "alternative-text-1"
            )
        )
    );

    private final static SpreadsheetFormatterSelectorToken COMPONENT2 = SpreadsheetFormatterSelectorToken.with(
        "label1",
        "text1",
        SpreadsheetFormatterSelectorToken.NO_ALTERNATIVES
    );

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterSelectorTokenList.with(null)
        );
    }

    @Test
    public void testDoesntDoubleWrap() {
        final SpreadsheetFormatterSelectorTokenList list = this.createList();
        assertSame(
            list,
            SpreadsheetFormatterSelectorTokenList.with(list)
        );
    }

    @Test
    public void testGet() {
        this.getAndCheck(
            this.createList(),
            0, // index
            COMPONENT1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            COMPONENT2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            COMPONENT1 // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final SpreadsheetFormatterSelectorTokenList list = this.createList();

        this.removeIndexFails(
            list,
            0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final SpreadsheetFormatterSelectorTokenList list = this.createList();

        this.removeFails(
            list,
            list.get(0)
        );
    }

    @Test
    public void testSetElementsIncludesNullFails() {
        final NullPointerException thrown = assertThrows(
            NullPointerException.class,
            () -> this.createList()
                .setElements(
                    Lists.of(
                        COMPONENT1,
                        null
                    )
                )
        );
        this.checkEquals(
            "Includes null token",
            thrown.getMessage()
        );
    }

    @Override
    public SpreadsheetFormatterSelectorTokenList createList() {
        return SpreadsheetFormatterSelectorTokenList.with(
            Lists.of(
                COMPONENT1,
                COMPONENT2
            )
        );
    }

    @Override
    public Class<SpreadsheetFormatterSelectorTokenList> type() {
        return SpreadsheetFormatterSelectorTokenList.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }

    // Json...........................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createList(),
            "[\n" +
                "  {\n" +
                "    \"label\": \"label1\",\n" +
                "    \"text\": \"text1\",\n" +
                "    \"alternatives\": [\n" +
                "      {\n" +
                "        \"label\": \"alternative-label-1\",\n" +
                "        \"text\": \"alternative-text-1\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"label\": \"label1\",\n" +
                "    \"text\": \"text1\"\n" +
                "  }\n" +
                "]"
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "[\n" +
                "  {\n" +
                "    \"label\": \"label1\",\n" +
                "    \"text\": \"text1\",\n" +
                "    \"alternatives\": [\n" +
                "      {\n" +
                "        \"label\": \"alternative-label-1\",\n" +
                "        \"text\": \"alternative-text-1\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"label\": \"label1\",\n" +
                "    \"text\": \"text1\"\n" +
                "  }\n" +
                "]",
            this.createList()
        );
    }

    @Override
    public SpreadsheetFormatterSelectorTokenList unmarshall(final JsonNode json,
                                                            final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterSelectorTokenList.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetFormatterSelectorTokenList createJsonNodeMarshallingValue() {
        return this.createList();
    }
}
