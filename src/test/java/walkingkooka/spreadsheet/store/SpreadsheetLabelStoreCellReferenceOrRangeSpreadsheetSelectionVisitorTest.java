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

import org.junit.jupiter.api.Test;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetCellRangeReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReferenceOrRange;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitorTesting;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitorTest implements SpreadsheetSelectionVisitorTesting<SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor> {

    // cellReferenceOrRange.............................................................................................

    @Test
    public void testCellReferenceOrRangeWithNullCellReferenceOrRangeFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor.cellReferenceOrRange(
                        null,
                        SpreadsheetLabelStores.fake()
                )
        );
    }

    @Test
    public void testCellReferenceOrRangeWithNullLabelStoreFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor.cellReferenceOrRange(
                        cell(),
                        null
                )
        );
    }
    
    @Test
    public void testCellReferenceOrRangeUnknownLabel() {
        final SpreadsheetLabelName label = this.label();
        this.cellReferenceOrRangeAndCheck(
                label,
                SpreadsheetLabelStores.treeMap(),
                null
        );
    }

    @Test
    public void testCellReferenceOrRangeCellReference() {
        final SpreadsheetCellReference cell = this.cell();
        this.cellReferenceOrRangeAndCheck(
                cell,
                SpreadsheetLabelStores.fake(),
                cell
        );
    }

    @Test
    public void testCellReferenceOrRangeLabelToCellReference() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetCellReference cell = this.cell();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.setLabelMappingTarget(cell));

        this.cellReferenceOrRangeAndCheck(
                label,
                store,
                cell
        );
    }

    @Test
    public void testCellReferenceOrRangeLabelToLabelToCellReference() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label22222");
        final SpreadsheetCellReference cell = this.cell();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.setLabelMappingTarget(label2));
        store.save(label2.setLabelMappingTarget(cell));

        this.cellReferenceOrRangeAndCheck(
                label,
                store,
                cell
        );
    }

    @Test
    public void testCellReferenceOrRangeCellRange() {
        final SpreadsheetCellRangeReference range = this.range();
        final SpreadsheetLabelName label = this.label();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.setLabelMappingTarget(range));

        this.cellReferenceOrRangeAndCheck(
                range,
                SpreadsheetLabelStores.fake(),
                range
        );
    }

    @Test
    public void testCellReferenceOrRangeLabelToCellRange() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetCellRangeReference range = this.range();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.setLabelMappingTarget(range));

        this.cellReferenceOrRangeAndCheck(
                label,
                store,
                range
        );
    }

    private SpreadsheetCellReference cell() {
        return SpreadsheetSelection.parseCell("A2");
    }

    private SpreadsheetLabelName label() {
        return SpreadsheetLabelName.labelName("label123");
    }

    private SpreadsheetCellRangeReference range() {
        return SpreadsheetSelection.parseCellRange("B2:C3");
    }

    private void cellReferenceOrRangeAndCheck(final SpreadsheetExpressionReference reference,
                                              final SpreadsheetLabelStore store,
                                              final SpreadsheetCellReferenceOrRange expected) {
        this.checkEquals(
                Optional.ofNullable(expected),
                SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor.cellReferenceOrRange(
                        reference,
                        store
                ),
                () -> "cellReferenceOrRange " + reference + " store=" + store
        );
    }

    @Override
    public SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor createVisitor() {
        return new SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor(null);
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor> type() {
        return SpreadsheetLabelStoreCellReferenceOrRangeSpreadsheetSelectionVisitor.class;
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
