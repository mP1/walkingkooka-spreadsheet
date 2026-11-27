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
 * A {@link List} that holds non-null {@link SpreadsheetFormatterSelector spreadsheetFormatterSelector}.
 */
public final class SpreadsheetFormatterSelectorList extends AbstractList<SpreadsheetFormatterSelector>
    implements ImmutableListDefaults<SpreadsheetFormatterSelectorList, SpreadsheetFormatterSelector>,
    HasCsvStringListTsvStringList {

    public static SpreadsheetFormatterSelectorList fromStringLikeList(final StringLikeList stringLikeList) {
        Objects.requireNonNull(stringLikeList, "stringLikeList");

        return EMPTY.setElements(
            stringLikeList.stream()
                .map(SpreadsheetFormatterSelector::parse)
                .collect(Collectors.toList())
        );
    }

    public final static SpreadsheetFormatterSelectorList EMPTY = new SpreadsheetFormatterSelectorList(Lists.empty());

    // @VisibleForTesting
    SpreadsheetFormatterSelectorList(final List<SpreadsheetFormatterSelector> list) {
        super();
        this.list = list;
    }

    @Override
    public SpreadsheetFormatterSelector get(final int index) {
        return this.list.get(index);
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public SpreadsheetFormatterSelectorList setElements(final Collection<SpreadsheetFormatterSelector> spreadsheetFormatterSelectors) {
        final SpreadsheetFormatterSelectorList spreadsheetFormatterSelectorList;

        if (spreadsheetFormatterSelectors instanceof SpreadsheetFormatterSelectorList) {
            spreadsheetFormatterSelectorList = (SpreadsheetFormatterSelectorList) spreadsheetFormatterSelectors;
        } else {
            Objects.requireNonNull(spreadsheetFormatterSelectors, "spreadsheetFormatterSelectors");

            final List<SpreadsheetFormatterSelector> copy = Lists.array();
            for (final SpreadsheetFormatterSelector spreadsheetFormatterSelector : spreadsheetFormatterSelectors) {
                this.elementCheck(spreadsheetFormatterSelector);
                copy.add(spreadsheetFormatterSelector);
            }

            spreadsheetFormatterSelectorList = this.equals(copy) ?
                this :
                this.replaceSpreadsheetFormatterSelectors(copy);
        }

        return spreadsheetFormatterSelectorList;
    }

    @Override
    public SpreadsheetFormatterSelectorList subList(final int from,
                                                    final int to) {
        return 0 == from && this.size() == to ?
            this :
            this.replaceSpreadsheetFormatterSelectors(
                this.list.subList(
                    from,
                    to
                )
            );
    }

    private SpreadsheetFormatterSelectorList replaceSpreadsheetFormatterSelectors(final List<SpreadsheetFormatterSelector> spreadsheetFormatterSelectors) {
        return spreadsheetFormatterSelectors.isEmpty() ?
            EMPTY :
            new SpreadsheetFormatterSelectorList(spreadsheetFormatterSelectors);
    }

    @Override
    public void elementCheck(final SpreadsheetFormatterSelector spreadsheetFormatterSelector) {
        Objects.requireNonNull(spreadsheetFormatterSelector, "spreadsheetFormatterSelector");
    }

    private final List<SpreadsheetFormatterSelector> list;

    // HasCsvString.....................................................................................................

    /**
     * Returns a {@link CsvStringList}, with each value a single {@link SpreadsheetFormatterSelector} after converting to CSV.
     */
    @Override
    public CsvStringList csvStringList() {
        return CsvStringList.makeCsvStringList(
            this.list,
            SpreadsheetFormatterSelector::text
        );
    }

    // json.............................................................................................................

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetFormatterSelectorList.class),
            SpreadsheetFormatterSelectorList::unmarshall,
            SpreadsheetFormatterSelectorList::marshall,
            SpreadsheetFormatterSelectorList.class
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return context.marshall(this.csvStringList());
    }

    static SpreadsheetFormatterSelectorList unmarshall(final JsonNode node,
                                                       final JsonNodeUnmarshallContext context) {
        return SpreadsheetFormatterSelectorList.fromStringLikeList(
            context.unmarshall(
                node,
                CsvStringList.class
            )
        );
    }
}
