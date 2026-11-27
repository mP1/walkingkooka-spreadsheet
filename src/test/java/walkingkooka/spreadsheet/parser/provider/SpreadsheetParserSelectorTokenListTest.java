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

package walkingkooka.spreadsheet.parser.provider;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.ListTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.PublicClassTesting;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallerTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpreadsheetParserSelectorTokenListTest implements ListTesting2<SpreadsheetParserSelectorTokenList, SpreadsheetParserSelectorToken>,
    PublicClassTesting<SpreadsheetParserSelectorTokenList>,
    ImmutableListTesting<SpreadsheetParserSelectorTokenList, SpreadsheetParserSelectorToken>,
    JsonNodeMarshallerTesting<SpreadsheetParserSelectorTokenList>,
    ThrowableTesting {

    private final static SpreadsheetParserSelectorToken TOKEN1 = SpreadsheetParserSelectorToken.with(
        "label111",
        "text111",
        Lists.of(
            SpreadsheetParserSelectorTokenAlternative.with(
                "alternative-label-111",
                "alternative-text-111"
            )
        )
    );

    private final static SpreadsheetParserSelectorToken TOKEN2 = SpreadsheetParserSelectorToken.with(
        "label222",
        "text222",
        SpreadsheetParserSelectorToken.NO_ALTERNATIVES
    );

    private final static SpreadsheetParserSelectorToken TOKEN3 = SpreadsheetParserSelectorToken.with(
        "label333",
        "text333",
        SpreadsheetParserSelectorToken.NO_ALTERNATIVES
    );

    private final static SpreadsheetParserSelectorToken TOKEN4 = SpreadsheetParserSelectorToken.with(
        "label444",
        "text444",
        SpreadsheetParserSelectorToken.NO_ALTERNATIVES
    );

    private final static SpreadsheetParserSelectorToken TOKEN5 = SpreadsheetParserSelectorToken.with(
        "label555",
        "text555",
        SpreadsheetParserSelectorToken.NO_ALTERNATIVES
    );

    @Test
    public void testGet() {
        this.getAndCheck(
            this.createList(),
            0, // index
            TOKEN1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            TOKEN2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            TOKEN1 // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final SpreadsheetParserSelectorTokenList list = this.createList();

        this.removeIndexFails(
            list,
            0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final SpreadsheetParserSelectorTokenList list = this.createList();

        this.removeFails(
            list,
            list.get(0)
        );
    }

    @Test
    public void testSetElementsDoesntDoubleWrap() {
        final SpreadsheetParserSelectorTokenList list = this.createList();
        assertSame(
            list,
            SpreadsheetParserSelectorTokenList.EMPTY.setElements(list)
        );
    }

    @Test
    public void testSetElementsIncludesNullFails() {
        final NullPointerException thrown = assertThrows(
            NullPointerException.class,
            () -> this.createList()
                .setElements(
                    Lists.of(
                        TOKEN1,
                        null
                    )
                )
        );
        this.getMessageAndCheck(
            thrown,
            "token"
        );
    }

    // subList..........................................................................................................

    private final static List<SpreadsheetParserSelectorToken> ELEMENTS = Lists.of(
        TOKEN1,
        TOKEN2,
        TOKEN3,
        TOKEN4,
        TOKEN5
    );

    @Test
    public void testSubList() {
        this.subListAndCheck(
            new SpreadsheetParserSelectorTokenList(ELEMENTS),
            0,
            2,
            new SpreadsheetParserSelectorTokenList(
                Lists.of(
                    TOKEN1,
                    TOKEN2
                )
            )
        );
    }

    @Test
    public void testSubList2() {
        this.subListAndCheck(
            new SpreadsheetParserSelectorTokenList(ELEMENTS),
            2,
            4,
            new SpreadsheetParserSelectorTokenList(
                Lists.of(
                    TOKEN3,
                    TOKEN4
                )
            )
        );
    }

    @Test
    public void testSubListSubList() {
        this.subListAndCheck(
            new SpreadsheetParserSelectorTokenList(ELEMENTS)
                .subList(
                    1,
                    4
                ), // 2, 3, 4
            1,
            3,
            new SpreadsheetParserSelectorTokenList(
                Lists.of(
                    TOKEN3,
                    TOKEN4
                )
            )
        );
    }
    
    @Override
    public SpreadsheetParserSelectorTokenList createList() {
        return new SpreadsheetParserSelectorTokenList(
            Lists.of(
                TOKEN1,
                TOKEN2
            )
        );
    }

    // Json...........................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            this.createList(),
            "[\n" +
                "  {\n" +
                "    \"label\": \"label111\",\n" +
                "    \"text\": \"text111\",\n" +
                "    \"alternatives\": [\n" +
                "      {\n" +
                "        \"label\": \"alternative-label-111\",\n" +
                "        \"text\": \"alternative-text-111\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"label\": \"label222\",\n" +
                "    \"text\": \"text222\"\n" +
                "  }\n" +
                "]"
        );
    }

    @Test
    public void testUnmarshall() {
        this.unmarshallAndCheck(
            "[\n" +
                "  {\n" +
                "    \"label\": \"label111\",\n" +
                "    \"text\": \"text111\",\n" +
                "    \"alternatives\": [\n" +
                "      {\n" +
                "        \"label\": \"alternative-label-111\",\n" +
                "        \"text\": \"alternative-text-111\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"label\": \"label222\",\n" +
                "    \"text\": \"text222\"\n" +
                "  }\n" +
                "]",
            this.createList()
        );
    }

    @Override
    public SpreadsheetParserSelectorTokenList unmarshall(final JsonNode json,
                                                         final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserSelectorTokenList.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetParserSelectorTokenList createJsonNodeMarshallingValue() {
        return this.createList();
    }

    // firstOrEmpty.....................................................................................................

    @Test
    public void testFirstOrEmptyWhenEmpty() {
        this.firstOrEmptyAndCheck(
            SpreadsheetParserSelectorTokenList.EMPTY
        );
    }

    @Test
    public void testFirstOrEmptyWhenNotEmpty() {
        this.firstOrEmptyAndCheck(
            this.createList(),
            TOKEN1
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParserSelectorTokenList> type() {
        return SpreadsheetParserSelectorTokenList.class;
    }
}
