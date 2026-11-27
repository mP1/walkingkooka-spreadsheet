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

package walkingkooka.spreadsheet.compare.provider;

import walkingkooka.collect.iterable.Iterables;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnOrRowReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.text.HasText;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link List} that holds multiple {@link SpreadsheetColumnOrRowSpreadsheetComparatorNames} typically one for each sortable
 * column with a range sort.
 */
public final class SpreadsheetColumnOrRowSpreadsheetComparatorNamesList extends AbstractList<SpreadsheetColumnOrRowSpreadsheetComparatorNames>
    implements ImmutableListDefaults<SpreadsheetColumnOrRowSpreadsheetComparatorNamesList, SpreadsheetColumnOrRowSpreadsheetComparatorNames>,
    HasText,
    HasUrlFragment {

    public static SpreadsheetColumnOrRowSpreadsheetComparatorNamesList parse(final String text) {
        return (SpreadsheetColumnOrRowSpreadsheetComparatorNamesList)
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.parseList(text);
    }

    public static SpreadsheetColumnOrRowSpreadsheetComparatorNamesList with(final Collection<SpreadsheetColumnOrRowSpreadsheetComparatorNames> list) {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList spreadsheetColumnOrRowSpreadsheetComparatorNamesList;

        if (list instanceof SpreadsheetColumnOrRowSpreadsheetComparatorNamesList) {
            spreadsheetColumnOrRowSpreadsheetComparatorNamesList = (SpreadsheetColumnOrRowSpreadsheetComparatorNamesList) list;
        } else {
            Objects.requireNonNull(list, "list");

            final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> copy = Lists.array();
            final Set<SpreadsheetSelection> duplicates = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

            for (final SpreadsheetColumnOrRowSpreadsheetComparatorNames names : list) {
                Objects.requireNonNull(
                    names,
                    "Includes null names"
                );
                final SpreadsheetColumnOrRowReferenceOrRange columnOrRow = names.columnOrRow();

                if (false == duplicates.isEmpty()) {
                    duplicates.iterator()
                        .next()
                        .ifDifferentColumnOrRowTypeFail(columnOrRow);
                }

                if (false == duplicates.add(columnOrRow)) {
                    throw new IllegalArgumentException(
                        "Duplicate " +
                            columnOrRow.cellColumnOrRowText() +
                            " " +
                            columnOrRow
                    );
                }
                copy.add(names);
            }

            if (copy.isEmpty()) {
                throw new IllegalArgumentException("Missing comparators");
            }

            spreadsheetColumnOrRowSpreadsheetComparatorNamesList = new SpreadsheetColumnOrRowSpreadsheetComparatorNamesList(copy);
        }

        return spreadsheetColumnOrRowSpreadsheetComparatorNamesList;
    }

    // @VisibleForTesting
    SpreadsheetColumnOrRowSpreadsheetComparatorNamesList(final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparators) {
        this.comparators = comparators;
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames get(final int index) {
        return this.comparators.get(index);
    }

    @Override
    public int size() {
        return this.comparators.size();
    }

    private final List<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparators;

    @Override
    public void elementCheck(final SpreadsheetColumnOrRowSpreadsheetComparatorNames names) {
        Objects.requireNonNull(names, "Includes null names");
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNamesList setElements(final Collection<SpreadsheetColumnOrRowSpreadsheetComparatorNames> elements) {
        return (SpreadsheetColumnOrRowSpreadsheetComparatorNamesList)
            this.setElementsMaybeCreate(
                elements,
                (final Collection<SpreadsheetColumnOrRowSpreadsheetComparatorNames> collection) -> collection instanceof SpreadsheetColumnOrRowSpreadsheetComparatorNamesList,
                this::elementCheck,
                SpreadsheetColumnOrRowSpreadsheetComparatorNamesList::new
            );
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNamesList subList(final int from,
                                                                        final int to) {
        return 0 == from && this.size() == to ?
            this :
            new SpreadsheetColumnOrRowSpreadsheetComparatorNamesList(
                this.comparators.subList(
                    from,
                    to
                )
            );
    }

    // HasText..........................................................................................................

    @Override
    public String text() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.COLUMN_ROW_COMPARATOR_NAMES_SEPARATOR.toDelimiteredString(
            Iterables.mapping(
                this,
                Object::toString
            ),
            SpreadsheetColumnOrRowSpreadsheetComparatorNames.COLUMN_ROW_COMPARATOR_NAMES_SEPARATOR.constantSeparator()
        );
    }

    // SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.............................................................

    /**
     * Extracts all {@link SpreadsheetComparatorName}.
     */
    public Set<SpreadsheetComparatorName> names() {
        return Sets.immutable(
            this.stream()
                .flatMap(n ->
                    n.comparatorNames()
                        .stream())
                .collect(Collectors.toCollection(Sets::ordered))
        );
    }

    // Json.............................................................................................................

    static SpreadsheetColumnOrRowSpreadsheetComparatorNamesList unmarshall(final JsonNode node,
                                                                           final JsonNodeUnmarshallContext context) {
        return parse(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.text());
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.class),
            SpreadsheetColumnOrRowSpreadsheetComparatorNamesList::unmarshall,
            SpreadsheetColumnOrRowSpreadsheetComparatorNamesList::marshall,
            SpreadsheetColumnOrRowSpreadsheetComparatorNamesList.class
        );
    }

    // HasUrlFragment...................................................................................................

    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(this.text());
    }
}
