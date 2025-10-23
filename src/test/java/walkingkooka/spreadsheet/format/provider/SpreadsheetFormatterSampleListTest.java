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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.text.TextNode;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpreadsheetFormatterSampleListTest implements ListTesting2<SpreadsheetFormatterSampleList, SpreadsheetFormatterSample>,
    ClassTesting<SpreadsheetFormatterSampleList>,
    ImmutableListTesting<SpreadsheetFormatterSampleList, SpreadsheetFormatterSample>,
    JsonNodeMarshallingTesting<SpreadsheetFormatterSampleList> {

    private final static SpreadsheetFormatterSample SAMPLE1 = SpreadsheetFormatterSample.with(
        "label1",
        SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy").spreadsheetFormatterSelector(),
        TextNode.text("31/12/1999")
    );

    private final static SpreadsheetFormatterSample SAMPLE2 = SpreadsheetFormatterSample.with(
        "label1",
        SpreadsheetPattern.parseTimeFormatPattern("hh/mm").spreadsheetFormatterSelector(),
        TextNode.text("12/58")
    );

    @Test
    public void testWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetFormatterSampleList.with(null)
        );
    }

    @Test
    public void testDoesntDoubleWrap() {
        final SpreadsheetFormatterSampleList list = this.createList();
        assertSame(
            list,
            SpreadsheetFormatterSampleList.with(list)
        );
    }

    @Test
    public void testGet() {
        this.getAndCheck(
            this.createList(),
            0, // index
            SAMPLE1 // expected
        );
    }

    @Test
    public void testGet2() {
        this.getAndCheck(
            this.createList(),
            1, // index
            SAMPLE2 // expected
        );
    }

    @Test
    public void testSetFails() {
        this.setFails(
            this.createList(),
            0, // index
            SAMPLE1 // expected
        );
    }

    @Test
    public void testRemoveIndexFails() {
        final SpreadsheetFormatterSampleList list = this.createList();

        this.removeIndexFails(
            list,
            0
        );
    }

    @Test
    public void testRemoveElementFails() {
        final SpreadsheetFormatterSampleList list = this.createList();

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
                        SAMPLE1,
                        SAMPLE2,
                        null
                    )
                )
        );
        this.checkEquals(
            "includes null sample",
            thrown.getMessage()
        );
    }

    @Override
    public SpreadsheetFormatterSampleList createList() {
        return SpreadsheetFormatterSampleList.with(
            Lists.of(
                SAMPLE1,
                SAMPLE2
            )
        );
    }

    @Override
    public Class<SpreadsheetFormatterSampleList> type() {
        return SpreadsheetFormatterSampleList.class;
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
                "    \"selector\": \"date dd/mm/yyyy\",\n" +
                "    \"value\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"value\": \"31/12/1999\"\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"label\": \"label1\",\n" +
                "    \"selector\": \"time hh/mm\",\n" +
                "    \"value\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"value\": \"12/58\"\n" +
                "    }\n" +
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
                "    \"selector\": \"date dd/mm/yyyy\",\n" +
                "    \"value\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"value\": \"31/12/1999\"\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"label\": \"label1\",\n" +
                "    \"selector\": \"time hh/mm\",\n" +
                "    \"value\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"value\": \"12/58\"\n" +
                "    }\n" +
                "  }\n" +
                "]",
            this.createList()
        );
    }

    @Override
    public SpreadsheetFormatterSampleList unmarshall(final JsonNode json,
                                                     final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterSampleList.unmarshall(
            json,
            context
        );
    }

    @Override
    public SpreadsheetFormatterSampleList createJsonNodeMarshallingValue() {
        return this.createList();
    }
}
