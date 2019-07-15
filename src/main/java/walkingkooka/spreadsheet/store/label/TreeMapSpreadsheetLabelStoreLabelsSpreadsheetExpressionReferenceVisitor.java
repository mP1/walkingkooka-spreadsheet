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

package walkingkooka.spreadsheet.store.label;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.Map;
import java.util.Set;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that visits all mappings, and aims to return only {@link SpreadsheetLabelName} that map to the given {@link SpreadsheetCellReference}.
 */
final class TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static Set<SpreadsheetLabelName> gather(final SpreadsheetCellReference reference,
                                            final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings) {
        final TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor visitor = new TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor(reference,
                mappings);

        mappings.values()
                .forEach(visitor::acceptMapping);

        return visitor.labels;
    }

    // VisibleForTesting
    TreeMapSpreadsheetLabelStoreLabelsSpreadsheetExpressionReferenceVisitor(final SpreadsheetCellReference reference,
                                                                            final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings) {
        super();

        this.reference = reference;
        this.mappings = mappings;
    }

    private void acceptMapping(final SpreadsheetLabelMapping mapping) {
        this.add = false;
        this.accept(mapping.reference());
        if (this.add) {
            this.labels.add(mapping.label());
        }
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.add = this.reference.compareTo(reference) == 0;
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        if (false == this.add) {
            final SpreadsheetLabelMapping mapping = this.mappings.get(label);
            if (null != mapping) {
                this.accept(mapping.reference());
            }
        }

        if (this.add) {
            this.labels.add(label);
        }
    }

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.add = this.add | range.cellStream()
                .filter(r -> r.compareTo(reference) == 0)
                .count() > 0;
    }

    private final SpreadsheetCellReference reference;
    private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings;
    private final Set<SpreadsheetLabelName> labels = Sets.ordered();

    private boolean add;

    @Override
    public String toString() {
        return this.labels.toString();
    }
}
