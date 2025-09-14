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

package walkingkooka.spreadsheet.engine.collection;

import walkingkooka.collect.iterator.Iterators;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.ImmutableSortedSetDefaults;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.net.HasUrlFragment;
import walkingkooka.net.UrlFragment;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * An immutable {@link Set} of unique {@link SpreadsheetCell}, with the {@link walkingkooka.spreadsheet.reference.SpreadsheetReferenceKind}
 * ignored.
 */
public final class SpreadsheetCellSet extends AbstractSet<SpreadsheetCell> implements ImmutableSortedSetDefaults<SpreadsheetCellSet, SpreadsheetCell>,
    HasUrlFragment,
    TreePrintable {

    /**
     * Empty constant
     */
    public final static SpreadsheetCellSet EMPTY = new SpreadsheetCellSet(SortedSets.empty());

    /**
     * Factory that creates a {@link SpreadsheetCellSet} after taking a copy.
     */
    public static SpreadsheetCellSet with(final Collection<SpreadsheetCell> cells) {
        SpreadsheetCellSet with;

        if (cells instanceof SpreadsheetCellSet) {
            with = (SpreadsheetCellSet) cells;
        } else {
            Objects.requireNonNull(cells, "cells");

            SortedSet<SpreadsheetCell> sortedSet;
            if (cells instanceof SortedSet) {
                sortedSet = (SortedSet<SpreadsheetCell>) cells;
            } else {
                sortedSet = SortedSets.tree(COMPARATOR);
                sortedSet.addAll(cells);
            }

            with = withCopy(
                SortedSets.immutable(sortedSet)
            );
        }

        return with;
    }

    private static SpreadsheetCellSet withCopy(final SortedSet<SpreadsheetCell> cells) {
        return cells.isEmpty() ?
            EMPTY :
            new SpreadsheetCellSet(cells);
    }

    // @VisibleForTesting
    private SpreadsheetCellSet(final SortedSet<SpreadsheetCell> cells) {
        super();
        this.cells = cells;
    }

    @Override
    public Iterator<SpreadsheetCell> iterator() {
        return Iterators.readOnly(this.cells.iterator());
    }

    @Override
    public int size() {
        return this.cells.size();
    }

    @Override
    public Comparator<? super SpreadsheetCell> comparator() {
        return COMPARATOR;
    }

    private final static Comparator<SpreadsheetCell> COMPARATOR = SpreadsheetCell.REFERENCE_COMPARATOR;

    @Override
    public SpreadsheetCellSet subSet(final SpreadsheetCell from,
                                     final SpreadsheetCell to) {
        return withCopy(
            this.cells.subSet(
                from,
                to
            )
        );
    }

    @Override
    public SpreadsheetCellSet headSet(final SpreadsheetCell cell) {
        return withCopy(
            this.cells.headSet(cell)
        );
    }

    @Override
    public SpreadsheetCellSet tailSet(final SpreadsheetCell cell) {
        return withCopy(
            this.cells.tailSet(cell)
        );
    }

    @Override
    public SpreadsheetCell first() {
        return this.cells.first();
    }

    @Override
    public SpreadsheetCell last() {
        return this.cells.last();
    }

    @Override
    public SpreadsheetCellSet setElements(final Collection<SpreadsheetCell> cells) {
        final TreeSet<SpreadsheetCell> copy = new TreeSet<>(COMPARATOR);
        copy.addAll(cells);

        return this.cells.equals(copy) ?
            this :
            withCopy(copy);
    }

    @Override
    public void elementCheck(final SpreadsheetCell cell) {
        Objects.requireNonNull(cell, "cell");
    }

    @Override
    public SortedSet<SpreadsheetCell> toSet() {
        return new TreeSet<>(this);
    }

    private final SortedSet<SpreadsheetCell> cells;

    // json.............................................................................................................

    static SpreadsheetCellSet unmarshall(final JsonNode node,
                                         final JsonNodeUnmarshallContext context) {
        final SortedSet<SpreadsheetCell> cells = SortedSets.tree(COMPARATOR);

        unmarshallCollection(
            node,
            context,
            cells::add
        );

        return withCopy(cells);
    }

    static void unmarshallCollection(final JsonNode node,
                                     final JsonNodeUnmarshallContext context,
                                     final Consumer<SpreadsheetCell> builder) {
        for (final JsonNode cell : node.children()) {
            final SpreadsheetCell spreadsheetCell = context.unmarshall(
                JsonNode.object()
                    .set(
                        cell.name(),
                        cell
                    ),
                SpreadsheetCell.class
            );

            builder.accept(
                spreadsheetCell
            );
        }
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return marshallCollection(
            this,
            context
        );
    }

    static JsonNode marshallCollection(final Collection<SpreadsheetCell> cells,
                                       final JsonNodeMarshallContext context) {
        final List<JsonNode> children = Lists.array();

        for (final SpreadsheetCell cell : cells) {
            final JsonObject json = context.marshall(cell)
                .objectOrFail();
            children.add(
                json.children()
                    .get(0)
            );
        }

        return JsonNode.object()
            .setChildren(children);
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetCellSet.class),
            SpreadsheetCellSet::unmarshall,
            SpreadsheetCellSet::marshall,
            SpreadsheetCellSet.class
        );
    }

    // HasUrlFragment...................................................................................................

    /**
     * The {@link UrlFragment} will contain the cells within this set marshalled to JSON.
     */
    @Override
    public UrlFragment urlFragment() {
        return UrlFragment.with(
            this.marshall(
                JsonNodeMarshallContexts.basic()
            ).toString()
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        for (final SpreadsheetCell cell : this) {
            TreePrintable.printTreeOrToString(
                cell,
                printer
            );
            printer.lineStart();
        }
    }
}
