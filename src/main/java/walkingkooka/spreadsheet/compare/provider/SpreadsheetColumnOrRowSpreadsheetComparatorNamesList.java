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

import walkingkooka.collect.list.ImmutableListDefaults;
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

    public static SpreadsheetColumnOrRowSpreadsheetComparatorNamesList with(final Collection<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparatorNames) {
        Objects.requireNonNull(comparatorNames, "comparatorNames");

        return comparatorNames instanceof SpreadsheetColumnOrRowSpreadsheetComparatorNamesList ?
            (SpreadsheetColumnOrRowSpreadsheetComparatorNamesList) comparatorNames :
            copyAndCreate(comparatorNames);
    }

    static SpreadsheetColumnOrRowSpreadsheetComparatorNamesList copyAndCreate(final Collection<SpreadsheetColumnOrRowSpreadsheetComparatorNames> comparatorNames) {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNames[] copy = comparatorNames.toArray(
            new SpreadsheetColumnOrRowSpreadsheetComparatorNames[comparatorNames.size()]
        );

        if (copy.length == 0) {
            throw new IllegalArgumentException("Expected several sorted column/rows got 0");
        }

        final Set<SpreadsheetSelection> duplicates = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);

        for (final SpreadsheetColumnOrRowSpreadsheetComparatorNames columnOrRowComparators : comparatorNames) {
            if (null == columnOrRowComparators) {
                throw new NullPointerException("Includes null names");
            }

            final SpreadsheetColumnOrRowReferenceOrRange columnOrRow = columnOrRowComparators.columnOrRow();

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
        }

        return new SpreadsheetColumnOrRowSpreadsheetComparatorNamesList(copy);
    }

    private SpreadsheetColumnOrRowSpreadsheetComparatorNamesList(final SpreadsheetColumnOrRowSpreadsheetComparatorNames[] comparators) {
        this.comparators = comparators;
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNames get(final int index) {
        return this.comparators[index];
    }

    @Override
    public int size() {
        return this.comparators.length;
    }

    private final SpreadsheetColumnOrRowSpreadsheetComparatorNames[] comparators;

    // HasText..........................................................................................................

    @Override
    public String text() {
        return SpreadsheetColumnOrRowSpreadsheetComparatorNames.COLUMN_ROW_COMPARATOR_NAMES_SEPARATOR.toSeparatedString(
            this,
            Object::toString
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

    // ImmutableListDefaults............................................................................................

    @Override
    public void elementCheck(final SpreadsheetColumnOrRowSpreadsheetComparatorNames names) {
        Objects.requireNonNull(names, "names");
    }

    @Override
    public SpreadsheetColumnOrRowSpreadsheetComparatorNamesList setElements(final Collection<SpreadsheetColumnOrRowSpreadsheetComparatorNames> names) {
        final SpreadsheetColumnOrRowSpreadsheetComparatorNamesList copy = with(names);
        return this.equals(copy) ?
            this :
            copy;
    }
}
