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

import walkingkooka.collect.list.CsvStringList;
import walkingkooka.collect.list.HasCsvStringListTsvStringList;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.list.StringLikeList;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A {@link List} that holds non-null {@link SpreadsheetParserSelector spreadsheetParserSelector}.
 */
public final class SpreadsheetParserSelectorList extends AbstractList<SpreadsheetParserSelector>
    implements ImmutableListDefaults<SpreadsheetParserSelectorList, SpreadsheetParserSelector>,
    HasCsvStringListTsvStringList {

    public static SpreadsheetParserSelectorList fromStringLikeList(final StringLikeList stringLikeList) {
        Objects.requireNonNull(stringLikeList, "stringLikeList");

        return EMPTY.setElements(
            stringLikeList.stream()
                .map(SpreadsheetParserSelector::parse)
                .collect(Collectors.toList())
        );
    }

    public final static SpreadsheetParserSelectorList EMPTY = new SpreadsheetParserSelectorList(Lists.empty());

    // @VisibleForTesting
    SpreadsheetParserSelectorList(final List<SpreadsheetParserSelector> list) {
        super();
        this.list = list;
    }

    @Override
    public SpreadsheetParserSelector get(final int index) {
        return this.list.get(index);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public SpreadsheetParserSelectorList setElements(final Collection<SpreadsheetParserSelector> elements) {
        final SpreadsheetParserSelectorList spreadsheetParserSelectorList;

        if (elements instanceof SpreadsheetParserSelectorList) {
            spreadsheetParserSelectorList = (SpreadsheetParserSelectorList) elements;
        } else {
            Objects.requireNonNull(elements, "spreadsheetParserSelectors");

            final List<SpreadsheetParserSelector> copy = Lists.array();

            for(final SpreadsheetParserSelector each : elements) {
                this.elementCheck(each);
                copy.add(each);
            }

            spreadsheetParserSelectorList = this.equals(copy) ?
                this :
                this.replaceSpreadsheetParserSelectors(copy);
        }

        return spreadsheetParserSelectorList;
    }

    @Override
    public SpreadsheetParserSelectorList subList(final int from,
                                                 final int to) {
        return 0 == from && this.size() == to ?
            this :
            this.replaceSpreadsheetParserSelectors(
                this.list.subList(
                    from,
                    to
                )
            );
    }

    private SpreadsheetParserSelectorList replaceSpreadsheetParserSelectors(final List<SpreadsheetParserSelector> spreadsheetParserSelectors) {
        return spreadsheetParserSelectors.isEmpty() ?
            EMPTY :
            new SpreadsheetParserSelectorList(spreadsheetParserSelectors);
    }

    @Override
    public void elementCheck(final SpreadsheetParserSelector spreadsheetParserSelector) {
        Objects.requireNonNull(spreadsheetParserSelector, "spreadsheetParserSelector");
    }

    private final List<SpreadsheetParserSelector> list;

    // HasCsvString.....................................................................................................

    /**
     * Returns a {@link CsvStringList}, with each value a single {@link SpreadsheetParserSelector} after converting to CSV.
     */
    @Override
    public CsvStringList csvStringList() {
        return CsvStringList.makeCsvStringList(
            this.list,
            SpreadsheetParserSelector::text
        );
    }

    // json.............................................................................................................

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetParserSelectorList.class),
            SpreadsheetParserSelectorList::unmarshall,
            SpreadsheetParserSelectorList::marshall,
            SpreadsheetParserSelectorList.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshall(this.csvStringList());
    }

    static SpreadsheetParserSelectorList unmarshall(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        return SpreadsheetParserSelectorList.fromStringLikeList(
            context.unmarshall(
                node,
                CsvStringList.class
            )
        );
    }
}
