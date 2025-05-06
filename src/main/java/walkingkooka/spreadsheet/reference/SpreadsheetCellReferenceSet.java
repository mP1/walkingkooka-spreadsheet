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
import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A {@link Set} or {@link SpreadsheetCellReference}.
 */
public final class SpreadsheetCellReferenceSet extends AbstractSet<SpreadsheetCellReference>
        implements ImmutableSortedSetDefaults<SpreadsheetCellReferenceSet, SpreadsheetCellReference>,
        HasText,
        TreePrintable {

    /**
     * An empty {@link SpreadsheetCellReferenceSet}.
     */
    public final static SpreadsheetCellReferenceSet EMPTY = new SpreadsheetCellReferenceSet(SortedSets.empty());

    /**
     * The comma which separates the CSV text representation.
     */
    public final static CharacterConstant SEPARATOR = SpreadsheetSelectionCsvParser.SEPARATOR;

    /**
     * Factory that creates {@link SpreadsheetCellReferenceSet} with the given cells.
     */
    public static SpreadsheetCellReferenceSet with(final SortedSet<SpreadsheetCellReference> cells) {
        return EMPTY.setElements(cells);
    }

    private static SpreadsheetCellReferenceSet withCopy(final SortedSet<SpreadsheetCellReference> cells) {
        return cells.isEmpty() ?
                EMPTY :
                new SpreadsheetCellReferenceSet(cells);
    }

    /**
     * Accepts a string of csv {@link SpreadsheetCellReference} with optional whitespace around references ignored.
     */
    public static SpreadsheetCellReferenceSet parse(final String text) {
        return withCopy(
                SpreadsheetSelectionCsvParser.parse(
                        text,
                        SpreadsheetFormulaParsers.cell(),
                        (SpreadsheetFormulaParserToken token) -> token.cast(CellSpreadsheetFormulaParserToken.class)
                                .cell()
                )
        );
    }

    private SpreadsheetCellReferenceSet(final SortedSet<SpreadsheetCellReference> cells) {
        this.cells = cells;
    }

    // ImmutableSortedSet...............................................................................................

    @Override
    public Iterator<SpreadsheetCellReference> iterator() {
        return Iterators.readOnly(this.cells.iterator());
    }

    @Override
    public int size() {
        return this.cells.size();
    }

    @Override
    public Comparator<SpreadsheetCellReference> comparator() {
        return Cast.to(
                SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR
        ); // no comparator
    }

    @Override
    public SpreadsheetCellReferenceSet subSet(final SpreadsheetCellReference from,
                                              final SpreadsheetCellReference to) {
        return withCopy(
                this.cells.subSet(
                        from,
                        to
                )
        );
    }

    @Override
    public SpreadsheetCellReferenceSet headSet(final SpreadsheetCellReference name) {
        return withCopy(
                this.cells.headSet(name)
        );
    }

    @Override
    public SpreadsheetCellReferenceSet tailSet(final SpreadsheetCellReference name) {
        return withCopy(
                this.cells.tailSet(name)
        );
    }

    @Override
    public SpreadsheetCellReference first() {
        return this.cells.first();
    }

    @Override
    public SpreadsheetCellReference last() {
        return this.cells.last();
    }

    @Override
    public SpreadsheetCellReferenceSet setElements(final SortedSet<SpreadsheetCellReference> cells) {
        final SpreadsheetCellReferenceSet spreadsheetCellReferenceSet;

        if(cells instanceof SpreadsheetCellReferenceSet) {
            spreadsheetCellReferenceSet = (SpreadsheetCellReferenceSet) cells;
        } else {
            final TreeSet<SpreadsheetCellReference> copy = new TreeSet<>(
                    Objects.requireNonNull(cells, "cells")
            );
            spreadsheetCellReferenceSet = this.cells.equals(copy) ?
                    this :
                    withCopy(copy);
        }

        return spreadsheetCellReferenceSet;
    }

    @Override
    public SortedSet<SpreadsheetCellReference> toSet() {
        return new TreeSet<>(this);
    }

    private final SortedSet<SpreadsheetCellReference> cells;

    // HasText..........................................................................................................

    @Override
    public String text() {
        return SEPARATOR.toSeparatedString(
                this.cells,
                SpreadsheetCellReference::text
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        for (final SpreadsheetCellReference cell : this) {
            printer.println(cell.toString());
        }
    }

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
    }

    /**
     * Returns a CSV string with cell references separated by commas.
     */
    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(
                this.text()
        );
    }

    static SpreadsheetCellReferenceSet unmarshall(final JsonNode node,
                                                  final JsonNodeUnmarshallContext context) {
        return parse(
                node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(SpreadsheetCellReferenceSet.class),
                SpreadsheetCellReferenceSet::unmarshall,
                SpreadsheetCellReferenceSet::marshall,
                SpreadsheetCellReferenceSet.class
        );
        SpreadsheetSelection.A1.isCell(); // trigger static init and json marshall/unmarshall registry
    }
}
