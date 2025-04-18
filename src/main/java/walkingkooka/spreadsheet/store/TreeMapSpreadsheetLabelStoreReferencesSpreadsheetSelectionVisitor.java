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

package walkingkooka.spreadsheet.store;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.Map;
import java.util.Set;

/**
 * A {@link SpreadsheetSelectionVisitor} that visits all label targets, and aims to return only {@link SpreadsheetCellReference} and {@link SpreadsheetCellRangeReference}.
 */
final class TreeMapSpreadsheetLabelStoreReferencesSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static Set<SpreadsheetCellReferenceOrRange> gather(final SpreadsheetLabelName label,
                                                       final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings) {
        final TreeMapSpreadsheetLabelStoreReferencesSpreadsheetSelectionVisitor visitor = new TreeMapSpreadsheetLabelStoreReferencesSpreadsheetSelectionVisitor(mappings);
        visitor.accept(label);
        return visitor.cellOrCellRanges;
    }

    // VisibleForTesting
    TreeMapSpreadsheetLabelStoreReferencesSpreadsheetSelectionVisitor(final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings) {
        super();

        this.mappings = mappings;
    }

    @Override
    protected void visit(final SpreadsheetCellReference cell) {
        this.cellOrCellRanges.add(cell);
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        final SpreadsheetLabelMapping mapping = this.mappings.get(label);
        if (null != mapping) {
            final ExpressionReference reference = mapping.reference();
            if (this.seen.add(reference)) {
                this.accept(mapping.reference());
            }
        }
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference cellRange) {
        this.cellOrCellRanges.add(cellRange);
    }

    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings;
    private final Set<ExpressionReference> seen = Sets.hash();
    private final Set<SpreadsheetCellReferenceOrRange> cellOrCellRanges = Sets.ordered();

    @Override
    public String toString() {
        return this.cellOrCellRanges.toString();
    }
}
