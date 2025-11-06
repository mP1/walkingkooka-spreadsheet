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
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionMaps;
import walkingkooka.spreadsheet.reference.SpreadsheetSelectionVisitorTesting;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitorTest implements SpreadsheetSelectionVisitorTesting<SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor> {

    // resolveLabel.............................................................................................

    @Test
    public void testResolveLabelWithNullLabelFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor.resolveLabel(
                null,
                SpreadsheetLabelStores.fake()
            )
        );
    }

    @Test
    public void testResolveLabelWithNullLabelStoreFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor.resolveLabel(
                label(),
                null
            )
        );
    }

    @Test
    public void testResolveLabelWithCycleFails() {
        // LabelStores#tree#save would fail when duplicate "saved".
        final SpreadsheetLabelStore store = new FakeSpreadsheetLabelStore() {

            @Override
            public Optional<SpreadsheetLabelMapping> load(final SpreadsheetLabelName label) {
                return Optional.ofNullable(
                    this.mappings.get(label)
                );
            }

            @Override
            public SpreadsheetLabelMapping save(SpreadsheetLabelMapping mapping) {
                this.mappings.put(
                    mapping.label(),
                    mapping
                );
                return mapping;
            }

            private final Map<SpreadsheetLabelName, SpreadsheetLabelMapping> mappings = SpreadsheetSelectionMaps.label();
        };

        final SpreadsheetLabelName label1 = SpreadsheetSelection.labelName("Label111");
        final SpreadsheetLabelName label2 = SpreadsheetSelection.labelName("Label222");
        final SpreadsheetLabelName label3 = SpreadsheetSelection.labelName("Label333");

        store.save(
            label3.setLabelMappingReference(label1)
        );
        store.save(
            label2.setLabelMappingReference(label3)
        );
        store.save(
            label1.setLabelMappingReference(label2)
        );

        final IllegalStateException thrown = assertThrows(
            IllegalStateException.class,
            () -> SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor.resolveLabel(
                label1,
                store
            )
        );
        this.checkEquals(
            "Cycle detected for \"Label111\" -> \"Label222\" -> \"Label333\" -> \"Label111\"",
            thrown.getMessage()
        );
    }

    @Test
    public void testResolveLabelUnknownLabel() {
        final SpreadsheetLabelName label = this.label();
        this.resolveLabelAndCheck(
            label,
            SpreadsheetLabelStores.treeMap(),
            null
        );
    }

    @Test
    public void testResolveLabelGivesCell() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetCellReference cell = this.cell();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.setLabelMappingReference(cell));

        this.resolveLabelAndCheck(
            label,
            store,
            cell
        );
    }

    @Test
    public void testResolveLabelToCell() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label22222");
        final SpreadsheetCellReference cell = this.cell();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.setLabelMappingReference(label2));
        store.save(label2.setLabelMappingReference(cell));

        this.resolveLabelAndCheck(
            label,
            store,
            cell
        );
    }

    @Test
    public void testResolveLabelToCellRange() {
        final SpreadsheetLabelName label = this.label();
        final SpreadsheetCellRangeReference range = this.range();

        final SpreadsheetLabelStore store = SpreadsheetLabelStores.treeMap();
        store.save(label.setLabelMappingReference(range));

        this.resolveLabelAndCheck(
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

    private void resolveLabelAndCheck(final SpreadsheetLabelName labelName,
                                      final SpreadsheetLabelStore store,
                                      final SpreadsheetCellReferenceOrRange expected) {
        this.checkEquals(
            Optional.ofNullable(expected),
            SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor.resolveLabel(
                labelName,
                store
            ),
            () -> "resolveLabel " + labelName + " store=" + store
        );
    }

    @Override
    public SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor createVisitor() {
        return new SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor(null);
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor> type() {
        return SpreadsheetLabelStoreResolveLabelSpreadsheetSelectionVisitor.class;
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
