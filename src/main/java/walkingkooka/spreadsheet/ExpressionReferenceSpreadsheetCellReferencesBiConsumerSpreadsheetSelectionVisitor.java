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

package walkingkooka.spreadsheet;

import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitor;

import java.util.function.Consumer;

/**
 * A {@link SpreadsheetSelectionVisitor} that resolves labels and ranges to a {@link SpreadsheetCellReference}
 */
final class ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor extends SpreadsheetSelectionVisitor {

    static void findSpreadsheetCellReferences(final SpreadsheetExpressionReference reference,
                                              final ExpressionReferenceSpreadsheetCellReferencesBiConsumer stores,
                                              final Consumer<SpreadsheetCellReference> references) {
        new ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor(stores, references)
                .accept(reference);
    }

    // @VisibleForTesting
    ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor(final ExpressionReferenceSpreadsheetCellReferencesBiConsumer stores,
                                                                                      final Consumer<SpreadsheetCellReference> references) {
        super();
        this.stores = stores;
        this.references = references;
    }

    @Override
    protected void visit(final SpreadsheetCellReference reference) {
        this.references.accept(reference);
    }

    @Override
    protected void visit(final SpreadsheetLabelName label) {
        this.stores.labelStore.load(label).ifPresent(m -> this.accept(m.target()));
    }

    @Override
    protected void visit(final SpreadsheetCellRange range) {
        this.stores.rangeToCellStore.load(range).ifPresent(r -> r.forEach(this::accept));
    }

    private final ExpressionReferenceSpreadsheetCellReferencesBiConsumer stores;
    private final Consumer<SpreadsheetCellReference> references;

    @Override
    public String toString() {
        return this.stores + " " + this.references;
    }
}
