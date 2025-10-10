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
import walkingkooka.spreadsheet.formula.parser.LabelSpreadsheetFormulaParserToken;
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
 * An immutable {@link Set} containing unique {@link SpreadsheetLabelName labels}.
 */
public final class SpreadsheetLabelNameSet extends SpreadsheetSelectionSet<SpreadsheetLabelName, SpreadsheetLabelNameSet> {

    /**
     * An empty {@link SpreadsheetLabelNameSet}.
     */
    public final static SpreadsheetLabelNameSet EMPTY = new SpreadsheetLabelNameSet(SortedSets.empty());

    /**
     * The comma which separates the CSV text representation.
     */
    public final static CharacterConstant SEPARATOR = SpreadsheetSelectionSet.SEPARATOR;

    /**
     * Accepts a string of csv {@link SpreadsheetLabelName} with optional whitespace around labels ignored.
     */
    public static SpreadsheetLabelNameSet parse(final String text) {
        return withCopy(
            SpreadsheetSelectionCsvParser.parse(
                text,
                SpreadsheetFormulaParsers.labelName(),
                (SpreadsheetFormulaParserToken token) -> token.cast(LabelSpreadsheetFormulaParserToken.class)
                    .reference()
            )
        );
    }

    /**
     * Factory that creates {@link SpreadsheetLabelNameSet} with the given labels.
     */
    public static SpreadsheetLabelNameSet with(final Collection<SpreadsheetLabelName> labels) {
        return EMPTY.setElements(labels);
    }

    private static SpreadsheetLabelNameSet withCopy(final SortedSet<SpreadsheetLabelName> labels) {
        return labels.isEmpty() ?
            EMPTY :
            new SpreadsheetLabelNameSet(labels);
    }

    private SpreadsheetLabelNameSet(final SortedSet<SpreadsheetLabelName> labels) {
        super(labels);
    }

    // SpreadsheetSelectionSet..........................................................................................

    @Override
    public SpreadsheetLabelNameSet setElements(final Collection<SpreadsheetLabelName> labels) {
        final SpreadsheetLabelNameSet spreadsheetLabelNameSet;

        if (labels instanceof SpreadsheetLabelNameSet) {
            spreadsheetLabelNameSet = (SpreadsheetLabelNameSet) labels;
        } else {
            final TreeSet<SpreadsheetLabelName> copy = new TreeSet<>(
                Objects.requireNonNull(labels, "labels")
            );
            spreadsheetLabelNameSet = this.references.equals(copy) ?
                this :
                withCopy(copy);
        }

        return spreadsheetLabelNameSet;
    }

    @Override
    SpreadsheetLabelNameSet createWithCopy(final SortedSet<SpreadsheetLabelName> labels) {
        return new SpreadsheetLabelNameSet(labels);
    }

    @Override
    public void elementCheck(final SpreadsheetLabelName label) {
        Objects.requireNonNull(label, "label");
    }

    // Json.............................................................................................................

    static SpreadsheetLabelNameSet unmarshall(final JsonNode node,
                                              final JsonNodeUnmarshallContext context) {
        return parse(
            node.stringOrFail()
        );
    }

    static {
        JsonNodeContext.register(
            JsonNodeContext.computeTypeName(SpreadsheetLabelNameSet.class),
            SpreadsheetLabelNameSet::unmarshall,
            SpreadsheetLabelNameSet::marshall,
            SpreadsheetLabelNameSet.class
        );
    }
}
