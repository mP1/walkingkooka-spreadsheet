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
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;

import java.util.Map;
import java.util.Set;

/**
 * A {@link SpreadsheetSelectionVisitor} that visits all mappings, and aims to return only {@link SpreadsheetLabelName} that map to the given {@link SpreadsheetCellReference}.
 */
final class TreeMapSpreadsheetLabelStoreFindLabelsWithReferencesSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static Set<SpreadsheetLabelMapping> gather(final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings,
                                               final SpreadsheetExpressionReference reference,
                                               final int offset,
                                               final int count) {
        final TreeMapSpreadsheetLabelStoreFindLabelsWithReferencesSpreadsheetSelectionVisitor visitor = new TreeMapSpreadsheetLabelStoreFindLabelsWithReferencesSpreadsheetSelectionVisitor(
            mappings,
            offset,
            count
        );

        if (reference.isLabelName()) {
            visitor.accept(reference);
        } else {
            visitor.filter = reference.toCellOrCellRange();
        }

        // reference could be a label to another label and never resolves to a cell or cell-range
        if (null != visitor.filter) {
            mappings.values()
                .forEach(visitor::gatherMapping);
        }

        return Sets.readOnly(visitor.labels);
    }

    // VisibleForTesting
    TreeMapSpreadsheetLabelStoreFindLabelsWithReferencesSpreadsheetSelectionVisitor(final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings,
                                                                                    final int offset,
                                                                                    final int count) {
        super();

        this.offset = offset;
        this.count = count;
        this.mappings = mappings;
    }

    // VisibleForTesting
    void gatherMapping(final SpreadsheetLabelMapping mapping) {
        if (this.shouldGather()) {

            this.accept(mapping.reference());
            if (this.add) {
                this.addLabel(mapping);
            }
        }
    }

    @Override
    protected void visit(final SpreadsheetCellReference cell) {
        if (this.shouldGather()) {
            final SpreadsheetCellReferenceOrRange filter = this.filter;
            if (null == filter) {
                this.filter = cell;
            } else {
                this.add = filter.testCell(cell);
            }
        }
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        final SpreadsheetLabelMapping mapping = this.mappings.get(label);
        if (null != mapping) {
            this.accept(mapping.reference());
        }
    }

    @Override
    protected void visit(final SpreadsheetCellRangeReference cellRange) {
        final SpreadsheetCellReferenceOrRange filter = this.filter;
        if (null == filter) {
            this.filter = cellRange;
        } else {
            this.add = this.add | cellRange.cellStream()
                .anyMatch(this.filter::testCell);
        }
    }

    /**
     * The source containing all mappings.
     */
    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings;

    /**
     * This filter will match label mappings as they are visited. Initially this is null when resolving a reference of
     * {@link SpreadsheetLabelName}.
     */
    // @VisibleForTesting
    SpreadsheetCellReferenceOrRange filter;

    /**
     * Returns true when {@link #labels} has elements than {@link #count}.
     */
    private boolean shouldGather() {
        return this.labels.size() < this.count;
    }

    /**
     * The maximum number of elements allowed in {@link #labels} before visiting should STOP
     */
    private final int count;

    private void addLabel(final SpreadsheetLabelMapping mapping) {
        if (--this.offset < 0) {
            this.labels.add(mapping);
        }
    }

    /**
     * Used a countdown before adding to {@link #labels}.
     */
    private int offset;

    /**
     * The result being built.
     */
    private final Set<SpreadsheetLabelMapping> labels = Sets.ordered();

    /**
     * A flag that starts as false and becomes true if a mapping is matched by the filter.
     */
    private boolean add;

    @Override
    public String toString() {
        return this.labels.toString();
    }
}
