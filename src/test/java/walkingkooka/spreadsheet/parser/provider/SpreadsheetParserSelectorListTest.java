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
import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.HasCsvStringListTsvStringListTesting2;
import walkingkooka.collect.list.ImmutableListTesting;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.list.StringLikeList;
import walkingkooka.collect.list.StringList;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallerTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetParserSelectorListTest implements ImmutableListTesting<SpreadsheetParserSelectorList, SpreadsheetParserSelector>,
    HasCsvStringListTsvStringListTesting2<SpreadsheetParserSelectorList>,
    HasOptionalSpreadsheetParserSelectorTesting,
    JsonNodeMarshallerTesting<SpreadsheetParserSelectorList> {

    // fromStringLikeList................................................................................................

    @Test
    public void testFromStringLikeListWithNullFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetParserSelectorList.fromStringLikeList(null)
        );
    }

    @Test
    public void testFromStringLikeListWithEmpty() {
        this.fromStringLikeListAndCheck(
            CsvStringList.EMPTY,
            SpreadsheetParserSelectorList.EMPTY
        );
    }

    @Test
    public void testFromStringLikeListWithNotEmpty() {
        this.fromStringLikeListAndCheck(
            CsvStringList.EMPTY.concat(PARSER_SELECTOR.text())
                .concat(DIFFERENT_PARSER_SELECTOR.text()),
            SpreadsheetParserSelectorList.EMPTY.concat(PARSER_SELECTOR)
                .concat(DIFFERENT_PARSER_SELECTOR)
        );
    }

    @Test
    public void testFromStringLikeListWithNotEmptyListIncludesNull() {
        this.fromStringLikeListAndCheck(
            StringList.EMPTY.concat(PARSER_SELECTOR.text())
                .concat(null)
                .concat(DIFFERENT_PARSER_SELECTOR.text()),
            SpreadsheetParserSelectorList.EMPTY.concat(PARSER_SELECTOR)
                .concat(null)
                .concat(DIFFERENT_PARSER_SELECTOR)
        );
    }

    @Test
    public void testFromStringLikeListWithNotEmptyListIncludesEmptyString() {
        this.fromStringLikeListAndCheck(
            StringList.EMPTY.concat(PARSER_SELECTOR.text())
                .concat("")
                .concat(DIFFERENT_PARSER_SELECTOR.text()),
            SpreadsheetParserSelectorList.EMPTY.concat(PARSER_SELECTOR)
                .concat(null)
                .concat(DIFFERENT_PARSER_SELECTOR)
        );
    }

    private void fromStringLikeListAndCheck(final StringLikeList stringLikeList,
                                            final SpreadsheetParserSelectorList expected) {
        this.checkEquals(
            expected,
            SpreadsheetParserSelectorList.fromStringLikeList(stringLikeList)
        );
    }

    // setElements......................................................................................................

    @Test
    public void testSetElementsWithDifferent() {
        this.setElementsAndCheck(
            new SpreadsheetParserSelectorList(
                Lists.of(PARSER_SELECTOR)
            ),
            Lists.of(DIFFERENT_PARSER_SELECTOR),
            new SpreadsheetParserSelectorList(
                Lists.of(DIFFERENT_PARSER_SELECTOR)
            )
        );
    }

    @Test
    public void testSetElementsWithDifferentEmpty() {
        this.setElementsAndCheck(
            new SpreadsheetParserSelectorList(
                Lists.of(PARSER_SELECTOR)
            ),
            Lists.empty(),
            SpreadsheetParserSelectorList.EMPTY
        );
    }

    @Test
    public void testSetElementsWithDifferentListIncludesNull() {
        final List<SpreadsheetParserSelector> list = Lists.of(
            null,
            DIFFERENT_PARSER_SELECTOR
        );
        this.setElementsAndCheck(
            new SpreadsheetParserSelectorList(
                Lists.of(PARSER_SELECTOR)
            ),
            list,
            new SpreadsheetParserSelectorList(list)
        );
    }

    @Override
    public SpreadsheetParserSelectorList createList() {
        return SpreadsheetParserSelectorList.EMPTY;
    }

    // HasCsvStringList.................................................................................................

    @Test
    public void testCsvStringListWithEmpty() {
        this.csvStringListAndCheck(
            SpreadsheetParserSelectorList.EMPTY,
            ""
        );
    }

    @Test
    public void testCsvStringListWithNotEmpty() {
        this.csvStringListAndCheck(
            SpreadsheetParserSelectorList.EMPTY.concat(PARSER_SELECTOR)
                .concat(DIFFERENT_PARSER_SELECTOR),
            "\"parser-selector-111\"\n" +
                "\"different-parser-selector-222\""
        );
    }

    @Test
    public void testCsvStringListWithNotEmptyListIncludesNull() {
        this.csvStringListAndCheck(
            SpreadsheetParserSelectorList.EMPTY.concat(PARSER_SELECTOR)
                .concat(null)
                .concat(DIFFERENT_PARSER_SELECTOR),
            "\"parser-selector-111\"\n" +
                "\n" +
                "\"different-parser-selector-222\""
        );
    }

    // HasTsvStringList.................................................................................................

    @Test
    public void testTsvStringListWithEmpty() {
        this.tsvStringListAndCheck(
            SpreadsheetParserSelectorList.EMPTY,
            ""
        );
    }

    @Test
    public void testTsvStringListWithNotEmpty() {
        this.tsvStringListAndCheck(
            SpreadsheetParserSelectorList.EMPTY.concat(PARSER_SELECTOR)
                .concat(DIFFERENT_PARSER_SELECTOR),
            "\"parser-selector-111\"\n" +
                "\"different-parser-selector-222\""
        );
    }

    @Test
    public void testTsvStringListWithNotEmptyIncludesNull() {
        this.tsvStringListAndCheck(
            SpreadsheetParserSelectorList.EMPTY.concat(PARSER_SELECTOR)
                .concat(null)
                .concat(DIFFERENT_PARSER_SELECTOR),
            "\"parser-selector-111\"\n" +
                "\n" +
                "\"different-parser-selector-222\""
        );
    }

    @Override
    public SpreadsheetParserSelectorList createHasCsvStringListTsvStringList() {
        return this.createList();
    }

    // json.............................................................................................................

    @Test
    public void testMarshall() {
        this.marshallAndCheck(
            SpreadsheetParserSelectorList.EMPTY.concat(PARSER_SELECTOR)
                .concat(null)
                .concat(DIFFERENT_PARSER_SELECTOR),
            "\"parser-selector-111,,different-parser-selector-222\""
        );
    }

    @Override
    public SpreadsheetParserSelectorList unmarshall(final JsonNode jsonNode,
                                                    final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserSelectorList.unmarshall(
            jsonNode,
            context
        );
    }

    @Override
    public SpreadsheetParserSelectorList createJsonNodeMarshallingValue() {
        return this.createList();
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetParserSelectorList> type() {
        return SpreadsheetParserSelectorList.class;
    }
}
