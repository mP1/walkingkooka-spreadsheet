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

import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReferenceVisitor;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;
import walkingkooka.tree.expression.ExpressionReference;

import java.util.function.Consumer;

/**
 * A {@link SpreadsheetExpressionReferenceVisitor} that resolves labels and ranges to a {@link SpreadsheetCellReference}
 */
final class ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor extends SpreadsheetExpressionReferenceVisitor {

    static void findSpreadsheetCellReferences(final ExpressionReference reference,
                                              final ExpressionReferenceSpreadsheetCellReferencesBiConsumer stores,
                                              final Consumer<SpreadsheetCellReference> references) {
        new ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor(stores, references)
                .accept(reference);
    }

    // @VisibleForTesting
    ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetExpressionReferenceVisitor(final ExpressionReferenceSpreadsheetCellReferencesBiConsumer stores,
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
        this.stores.labelStore.load(label).ifPresent(m -> this.accept(m.reference()));
    }

    @Override
    protected void visit(final SpreadsheetRange range) {
        this.stores.rangeToCellStore.load(range).ifPresent(r -> r.forEach(this::accept));
    }

    private final ExpressionReferenceSpreadsheetCellReferencesBiConsumer stores;
    private final Consumer<SpreadsheetCellReference> references;

    @Override
    public String toString() {
        return this.stores + " " + this.references;
    }
}
