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
import walkingkooka.spreadsheet.formula.parser.RowSpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An immutable {@link Set} containing unique {@link SpreadsheetRowReference rows} with the {@link SpreadsheetReferenceKind} ignored.
 */
public final class SpreadsheetRowReferenceSet extends SpreadsheetSelectionSet<SpreadsheetRowReference, SpreadsheetRowReferenceSet> {

    /**
     * An empty {@link SpreadsheetRowReferenceSet}.
     */
    public final static SpreadsheetRowReferenceSet EMPTY = new SpreadsheetRowReferenceSet(SortedSets.empty());

    /**
     * The comma which separates the CSV text representation.
     */
    public final static CharacterConstant SEPARATOR = SpreadsheetSelectionSet.SEPARATOR;

    /**
     * Factory that creates {@link SpreadsheetRowReferenceSet} with the given rows.
     */
    public static SpreadsheetRowReferenceSet with(final Collection<SpreadsheetRowReference> rows) {
        return EMPTY.setElements(rows);
    }

    private static SpreadsheetRowReferenceSet withCopy(final SortedSet<SpreadsheetRowReference> rows) {
        return rows.isEmpty() ?
            EMPTY :
            new SpreadsheetRowReferenceSet(rows);
    }

    /**
     * Accepts a string of csv {@link SpreadsheetRowReference} with optional whitespace around references ignored.
     */
    public static SpreadsheetRowReferenceSet parse(final String text) {
        return withCopy(
            SpreadsheetSelectionCsvParser.parse(
                text,
                SpreadsheetFormulaParsers.row(),
                (SpreadsheetFormulaParserToken token) -> token.cast(RowSpreadsheetFormulaParserToken.class)
                    .reference()
            )
        );
    }

    private SpreadsheetRowReferenceSet(final SortedSet<SpreadsheetRowReference> rows) {
        super(rows);
    }

    // SpreadsheetSelectionSet..........................................................................................

    @Override
    public SpreadsheetRowReferenceSet setElements(final Collection<SpreadsheetRowReference> rows) {
        final SpreadsheetRowReferenceSet spreadsheetRowReferenceSet;

        if (rows instanceof SpreadsheetRowReferenceSet) {
            spreadsheetRowReferenceSet = (SpreadsheetRowReferenceSet) rows;
        } else {
            final TreeSet<SpreadsheetRowReference> copy = new TreeSet<>(
                Objects.requireNonNull(rows, "rows")
            );
            spreadsheetRowReferenceSet = this.references.equals(copy) ?
                this :
                withCopy(copy);
        }

        return spreadsheetRowReferenceSet;
    }

    @Override
    SpreadsheetRowReferenceSet createWithCopy(final SortedSet<SpreadsheetRowReference> rows) {
        return new SpreadsheetRowReferenceSet(rows);
    }

    @Override
    public void elementCheck(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");
    }

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
    }

    static SpreadsheetRowReferenceSet unmarshall(final JsonNode node,
                                                 final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetRowReferenceSet.class),
            SpreadsheetRowReferenceSet::unmarshall,
            SpreadsheetRowReferenceSet::marshall,
            SpreadsheetRowReferenceSet.class
        );
        SpreadsheetSelection.A1.toRow(); // trigger static init and json marshall/unmarshall registry
    }
}
