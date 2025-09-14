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
import walkingkooka.spreadsheet.formula.parser.ColumnSpreadsheetFormulaParserToken;
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
 * An immutable {@link Set} containing unique {@link SpreadsheetColumnReference columns} with the {@link SpreadsheetReferenceKind} ignored.
 */
public final class SpreadsheetColumnReferenceSet extends SpreadsheetSelectionSet<SpreadsheetColumnReference, SpreadsheetColumnReferenceSet> {

    /**
     * An empty {@link SpreadsheetColumnReferenceSet}.
     */
    public final static SpreadsheetColumnReferenceSet EMPTY = new SpreadsheetColumnReferenceSet(SortedSets.empty());

    /**
     * The comma which separates the CSV text representation.
     */
    public final static CharacterConstant SEPARATOR = SpreadsheetSelectionSet.SEPARATOR;

    /**
     * Factory that creates {@link SpreadsheetColumnReferenceSet} with the given columns.
     */
    public static SpreadsheetColumnReferenceSet with(final Collection<SpreadsheetColumnReference> columns) {
        return EMPTY.setElements(columns);
    }

    private static SpreadsheetColumnReferenceSet withCopy(final SortedSet<SpreadsheetColumnReference> columns) {
        return columns.isEmpty() ?
            EMPTY :
            new SpreadsheetColumnReferenceSet(columns);
    }

    /**
     * Accepts a string of csv {@link SpreadsheetColumnReference} with optional whitespace around references ignored.
     */
    public static SpreadsheetColumnReferenceSet parse(final String text) {
        return withCopy(
            SpreadsheetSelectionCsvParser.parse(
                text,
                SpreadsheetFormulaParsers.column(),
                (SpreadsheetFormulaParserToken token) -> token.cast(ColumnSpreadsheetFormulaParserToken.class)
                    .reference()
            )
        );
    }

    private SpreadsheetColumnReferenceSet(final SortedSet<SpreadsheetColumnReference> columns) {
        super(columns);
    }

    // SpreadsheetSelectionSet..........................................................................................

    @Override
    public SpreadsheetColumnReferenceSet setElements(final Collection<SpreadsheetColumnReference> columns) {
        final SpreadsheetColumnReferenceSet spreadsheetColumnReferenceSet;

        if (columns instanceof SpreadsheetColumnReferenceSet) {
            spreadsheetColumnReferenceSet = (SpreadsheetColumnReferenceSet) columns;
        } else {
            final TreeSet<SpreadsheetColumnReference> copy = new TreeSet<>(
                Objects.requireNonNull(columns, "columns")
            );
            spreadsheetColumnReferenceSet = this.references.equals(copy) ?
                this :
                withCopy(copy);
        }

        return spreadsheetColumnReferenceSet;
    }

    @Override
    SpreadsheetColumnReferenceSet createWithCopy(final SortedSet<SpreadsheetColumnReference> columns) {
        return new SpreadsheetColumnReferenceSet(columns);
    }

    @Override
    public void elementCheck(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");
    }

    // Json.............................................................................................................

    static void register() {
        // helps force registry of json marshaller
    }

    static SpreadsheetColumnReferenceSet unmarshall(final JsonNode node,
                                                    final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetColumnReferenceSet.class),
            SpreadsheetColumnReferenceSet::unmarshall,
            SpreadsheetColumnReferenceSet::marshall,
            SpreadsheetColumnReferenceSet.class
        );
        SpreadsheetSelection.A1.toColumn(); // trigger static init and json marshall/unmarshall registry
    }
}
