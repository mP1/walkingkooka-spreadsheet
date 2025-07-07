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

import walkingkooka.collect.set.SortedSets;
import walkingkooka.spreadsheet.formula.SpreadsheetFormulaParsers;
import walkingkooka.spreadsheet.formula.parser.CellSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An immutable {@link Set} containing unique {@link SpreadsheetCellReference cells} with the {@link SpreadsheetReferenceKind}
 * of both the column and row components.
 */
public final class SpreadsheetCellReferenceSet extends SpreadsheetSelectionSet<SpreadsheetCellReference, SpreadsheetCellReferenceSet> {

    /**
     * An empty {@link SpreadsheetCellReferenceSet}.
     */
    public final static SpreadsheetCellReferenceSet EMPTY = new SpreadsheetCellReferenceSet(SortedSets.empty());

    /**
     * The comma which separates the CSV text representation.
     */
    public final static CharacterConstant SEPARATOR = SpreadsheetSelectionSet.SEPARATOR;

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
        super(cells);
    }

    // SpreadsheetSelectionSet..........................................................................................

    @Override
    public SpreadsheetCellReferenceSet setElements(final SortedSet<SpreadsheetCellReference> cells) {
        final SpreadsheetCellReferenceSet spreadsheetCellReferenceSet;

        if (cells instanceof SpreadsheetCellReferenceSet) {
            spreadsheetCellReferenceSet = (SpreadsheetCellReferenceSet) cells;
        } else {
            final TreeSet<SpreadsheetCellReference> copy = new TreeSet<>(
                Objects.requireNonNull(cells, "cells")
            );
            spreadsheetCellReferenceSet = this.references.equals(copy) ?
                this :
                withCopy(copy);
        }

        return spreadsheetCellReferenceSet;
    }

    @Override
    SpreadsheetCellReferenceSet createWithCopy(final SortedSet<SpreadsheetCellReference> cells) {
        return new SpreadsheetCellReferenceSet(cells);
    }

    @Override
    public void elementCheck(final SpreadsheetCellReference cell) {
        Objects.requireNonNull(cell, "cell");
    }

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
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
