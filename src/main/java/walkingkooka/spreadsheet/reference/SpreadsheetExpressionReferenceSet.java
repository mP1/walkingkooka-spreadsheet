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
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

/**
 * An immutable {@link Set} containing unique {@link SpreadsheetExpressionReference references} with the {@link SpreadsheetReferenceKind} ignored.
 */
public final class SpreadsheetExpressionReferenceSet extends SpreadsheetSelectionSet<SpreadsheetExpressionReference, SpreadsheetExpressionReferenceSet> {

    /**
     * An empty {@link SpreadsheetExpressionReferenceSet}.
     */
    public final static SpreadsheetExpressionReferenceSet EMPTY = new SpreadsheetExpressionReferenceSet(SortedSets.empty());

    /**
     * The comma which separates the CSV text representation.
     */
    public final static CharacterConstant SEPARATOR = SpreadsheetSelectionSet.SEPARATOR;

    /**
     * Accepts a string of csv {@link SpreadsheetExpressionReference} with optional whitespace around references ignored.
     */
    public static SpreadsheetExpressionReferenceSet parse(final String text) {
        return withCopy(
            SpreadsheetSelectionCsvParser.parse(
                text,
                CELL_OR_LABEL,
                (SpreadsheetFormulaParserToken token) -> ((SpreadsheetSelection) ((HasSpreadsheetReference<?>) token).reference())
                    .toExpressionReference()
            )
        );
    }

    private final static Parser<SpreadsheetParserContext> CELL_OR_LABEL = SpreadsheetFormulaParsers.labelName()
        .or(
            SpreadsheetFormulaParsers.cell()
        );

    /**
     * Factory that creates {@link SpreadsheetExpressionReferenceSet} with the given references.
     */
    public static SpreadsheetExpressionReferenceSet with(final Collection<SpreadsheetExpressionReference> references) {
        return EMPTY.setElements(references);
    }

    private static SpreadsheetExpressionReferenceSet withCopy(final SortedSet<SpreadsheetExpressionReference> references) {
        return references.isEmpty() ?
            EMPTY :
            new SpreadsheetExpressionReferenceSet(references);
    }

    private SpreadsheetExpressionReferenceSet(final SortedSet<SpreadsheetExpressionReference> references) {
        super(references);
    }

    // SpreadsheetSelectionSet..........................................................................................

    @Override
    public SpreadsheetExpressionReferenceSet setElements(final Collection<SpreadsheetExpressionReference> references) {
        final SpreadsheetExpressionReferenceSet spreadsheetExpressionReferenceSet;

        if (references instanceof SpreadsheetExpressionReferenceSet) {
            spreadsheetExpressionReferenceSet = (SpreadsheetExpressionReferenceSet) references;
        } else {
            final SortedSet<SpreadsheetExpressionReference> copy = SortedSets.tree(SpreadsheetSelection.IGNORES_REFERENCE_KIND_COMPARATOR);
            copy.addAll(references);
            spreadsheetExpressionReferenceSet = this.references.equals(copy) ?
                this :
                withCopy(copy);
        }

        return spreadsheetExpressionReferenceSet;
    }

    @Override
    public void elementCheck(final SpreadsheetExpressionReference cellOrLabel) {
        Objects.requireNonNull(cellOrLabel, "cellOrLabel");
    }

    @Override
    SpreadsheetExpressionReferenceSet createWithCopy(final SortedSet<SpreadsheetExpressionReference> references) {
        return new SpreadsheetExpressionReferenceSet(references);
    }

    // Json.............................................................................................................

    static SpreadsheetExpressionReferenceSet unmarshall(final JsonNode node,
                                                        final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetExpressionReferenceSet.class),
            SpreadsheetExpressionReferenceSet::unmarshall,
            SpreadsheetExpressionReferenceSet::marshall,
            SpreadsheetExpressionReferenceSet.class
        );
    }
}
