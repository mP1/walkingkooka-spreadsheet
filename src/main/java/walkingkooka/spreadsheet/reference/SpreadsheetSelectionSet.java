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

package walkingkooka.spreadsheet.reference;

import walkingkooka.Cast;
import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.set.ImmutableSortedSetDefaults;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A {@link Set} or {@link SpreadsheetCellReference}.
 */
abstract class SpreadsheetSelectionSet<E extends SpreadsheetSelection, S extends SpreadsheetSelectionSet<E, S>> extends AbstractSet<E>
    implements ImmutableSortedSetDefaults<S, E>,
    HasText,
    TreePrintable {

    /**
     * The comma which separates the CSV text representation.
     */
    public final static CharacterConstant SEPARATOR = SpreadsheetSelectionCsvParser.SEPARATOR;

    SpreadsheetSelectionSet(final SortedSet<E> references) {
        this.references = references;
    }

    abstract S createWithCopy(final SortedSet<E> copy);

    // ImmutableSortedSet...............................................................................................

    @Override
    public final Iterator<E> iterator() {
        return Iterators.readOnly(this.references.iterator());
    }

    @Override
    public final int size() {
        return this.references.size();
    }

    @Override
    public final Comparator<E> comparator() {
        return Cast.to(
            SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR
        ); // no comparator
    }

    @Override
    public final S subSet(final E from,
                          final E to) {
        return this.createWithCopy(
            this.references.subSet(
                from,
                to
            )
        );
    }

    @Override
    public final S headSet(final E reference) {
        return this.createWithCopy(
            this.references.headSet(reference)
        );
    }

    @Override
    public final S tailSet(final E reference) {
        return this.createWithCopy(
            this.references.tailSet(reference)
        );
    }

    @Override
    public final E first() {
        return this.references.first();
    }

    @Override
    public final E last() {
        return this.references.last();
    }

    @Override
    public final SortedSet<E> toSet() {
        return new TreeSet<>(this);
    }

    final SortedSet<E> references;

    // HasText..........................................................................................................

    @Override
    public final String text() {
        return SEPARATOR.toSeparatedString(
            this.references,
            HasText::text
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public final void printTree(final IndentingPrinter printer) {
        for (final E reference : this) {
            printer.println(reference.toString());
        }
    }

    // Json.............................................................................................................

    /**
     * Returns a CSV string with references separated by commas.
     */
    final JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
            this.text()
        );
    }
}
