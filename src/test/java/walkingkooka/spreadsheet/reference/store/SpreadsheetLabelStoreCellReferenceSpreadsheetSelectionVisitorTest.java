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

package walkingkooka.spreadsheet.reference.store;

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRange;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitorTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetLabelStoreCellReferenceSpreadsheetSelectionVisitorTest implements SpreadsheetSelectionVisitorTesting<SpreadsheetLabelStoreCellReferenceSpreadsheetSelectionVisitor> {

    @Test
    public void testNullReferenceFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetLabelStoreCellReferenceSpreadsheetSelectionVisitor.reference(null, SpreadsheetLabelStores.fake())
        );
    }

    @Test
    public void testNullLabelStoreFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetLabelStoreCellReferenceSpreadsheetSelectionVisitor.reference(cell(), null)
        );
    }

    @Test
    public void testUnknownLabel() {
        final SpreadsheetLabelName label = this.label();
        this.referenceAndCheck(
                label,
                SpreadsheetLabelStores.treeMap(),
                null
        );
    }

    @Test
    public void testReference() {
        final SpreadsheetCellReference cell = this.cell();
        this.referenceAndCheck(
                cell,
                SpreadsheetLabelStores.fake(),
                cell
        );
    }

    @Test
    public void testLabelToReference() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetCellReference cell = this.cell();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.mapping(cell));

        this.referenceAndCheck(
                label,
                store,
                cell
        );
    }

    @Test
    public void testLabelToLabelToReference() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label22222");
        final SpreadsheetCellReference cell = this.cell();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.mapping(label2));
        store.save(label2.mapping(cell));

        this.referenceAndCheck(
                label,
                store,
                cell
        );
    }

    @Test
    public void testRange() {
        final SpreadsheetCellReference cell = this.cell();
        final SpreadsheetCellReference cell2 = SpreadsheetSelection.parseCell("Z99");
        final SpreadsheetCellRange range = SpreadsheetSelection.cellRange(cell.range(cell2));

        final SpreadsheetLabelName label = this.label();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.mapping(range));

        this.referenceAndCheck(
                cell,
                SpreadsheetLabelStores.fake(),
                cell
        );
    }

    private SpreadsheetCellReference cell() {
        return SpreadsheetSelection.parseCell("A2");
    }

    private SpreadsheetLabelName label() {
        return SpreadsheetLabelName.labelName("label123");
    }

    private void referenceAndCheck(final SpreadsheetExpressionReference reference,
                                   final SpreadsheetLabelStore store,
                                   final SpreadsheetCellReference expected) {
        this.checkEquals(
                Optional.ofNullable(expected),
                SpreadsheetLabelStoreCellReferenceSpreadsheetSelectionVisitor.reference(reference, store),
                () -> "reference " + reference + " store=" + store
        );
    }

    @Override
    public SpreadsheetLabelStoreCellReferenceSpreadsheetSelectionVisitor createVisitor() {
        return new SpreadsheetLabelStoreCellReferenceSpreadsheetSelectionVisitor(null);
    }

    @Override
    public Class<SpreadsheetLabelStoreCellReferenceSpreadsheetSelectionVisitor> type() {
        return SpreadsheetLabelStoreCellReferenceSpreadsheetSelectionVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetLabelStore.class.getSimpleName();
    }
}
