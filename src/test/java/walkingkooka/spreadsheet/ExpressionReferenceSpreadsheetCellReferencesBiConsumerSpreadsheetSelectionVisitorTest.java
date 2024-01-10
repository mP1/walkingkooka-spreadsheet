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

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitorTesting;
import walkingkooka.spreadsheet.store.SpreadsheetCellRangeStores;
import walkingkooka.spreadsheet.store.SpreadsheetLabelStores;

import java.util.function.Consumer;

public final class ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitorTest implements
        SpreadsheetSelectionVisitorTesting<ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor>,
        ToStringTesting<ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor> {

    @Test
    public void testToString() {
        final ExpressionReferenceSpreadsheetCellReferencesBiConsumer f = ExpressionReferenceSpreadsheetCellReferencesBiConsumer.with(SpreadsheetLabelStores.fake(),
                SpreadsheetCellRangeStores.fake());
        final Consumer<SpreadsheetCellReference> references = new Consumer<>() {
            @Override
            public void accept(final SpreadsheetCellReference reference) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String toString() {
                return "SpreadsheetCellReferences123";
            }
        };
        this.toStringAndCheck(new ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor(f, references),
                f + " SpreadsheetCellReferences123");
    }

    @Override
    public ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor createVisitor() {
        return new ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor(null, null);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return ExpressionReferenceSpreadsheetCellReferencesBiConsumer.class.getSimpleName();
    }

    @Override
    public Class<ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor> type() {
        return ExpressionReferenceSpreadsheetCellReferencesBiConsumerSpreadsheetSelectionVisitor.class;
    }
}
