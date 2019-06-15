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
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStore;
import walkingkooka.spreadsheet.store.label.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStore;
import walkingkooka.spreadsheet.store.range.SpreadsheetRangeStores;
import walkingkooka.test.ClassTesting2;
import walkingkooka.test.ToStringTesting;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;
import walkingkooka.tree.expression.ExpressionReference;
import walkingkooka.type.JavaVisibility;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class ExpressionReferenceSpreadsheetCellReferencesBiConsumerTest implements ClassTesting2<ExpressionReferenceSpreadsheetCellReferencesBiConsumer>,
        ToStringTesting<ExpressionReferenceSpreadsheetCellReferencesBiConsumer> {

    @Test
    public void testWithNullLabelStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            ExpressionReferenceSpreadsheetCellReferencesBiConsumer.with(null, SpreadsheetRangeStores.fake());
        });
    }

    @Test
    public void testWithNullSpreadsheetRangeStoreFails() {
        assertThrows(NullPointerException.class, () -> {
            ExpressionReferenceSpreadsheetCellReferencesBiConsumer.with(SpreadsheetLabelStores.fake(), null);
        });
    }

    @Test
    public void testAcceptNullExpressionReferenceFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBiConsumer().accept(null, (c) -> {
            });
        });
    }

    @Test
    public void testAcceptNullBiConsumerFails() {
        assertThrows(NullPointerException.class, () -> {
            this.createBiConsumer().accept(this.cellB1(), null);
        });
    }

    @Test
    public void testCell() {
        final SpreadsheetCellReference z99 = SpreadsheetCellReference.parse("Z99");
        this.acceptAndCheck(z99, z99);
    }

    @Test
    public void testRange() {
        this.acceptAndCheck(this.rangeC1C2(), this.cellC1(), this.cellC2());
    }

    @Test
    public void testLabelToCell() {
        this.acceptAndCheck(this.labelB1(), this.cellB1());
    }

    @Test
    public void testLabelToUnknown() {
        this.acceptAndCheck(SpreadsheetLabelName.with("unknown"));
    }

    @Test
    public void testLabelToRange() {
        this.acceptAndCheck(this.labelRangeC1D2(), this.cellC1(), this.cellC2());
    }

    private void acceptAndCheck(final ExpressionReference reference,
                                final SpreadsheetCellReference... references) {
        final List<SpreadsheetCellReference> actual = Lists.array();
        this.createBiConsumer().accept(reference, actual::add);
        assertEquals(Lists.of(references), actual);
    }

    @Test
    public void testToString() {
        final SpreadsheetLabelStore labelStore = SpreadsheetLabelStores.fake();
        final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore = SpreadsheetRangeStores.fake();
        this.toStringAndCheck(ExpressionReferenceSpreadsheetCellReferencesBiConsumer.with(labelStore, rangeToCellStore),
                "ExpressionReference->Consumer<SpreadsheetCellReference>(" + labelStore + " " + rangeToCellStore + ")");
    }

    private ExpressionReferenceSpreadsheetCellReferencesBiConsumer createBiConsumer() {
        final SpreadsheetLabelStore labelStore = SpreadsheetLabelStores.treeMap();

        labelStore.save(SpreadsheetLabelMapping.with(labelB1(), this.cellB1()));
        labelStore.save(SpreadsheetLabelMapping.with(labelRangeC1D2(), this.rangeC1C2()));

        final SpreadsheetRangeStore<SpreadsheetCellReference> rangeToCellStore = SpreadsheetRangeStores.treeMap();
        rangeToCellStore.addValue(this.rangeC1C2(), this.cellC1());
        rangeToCellStore.addValue(this.rangeC1C2(), this.cellC2());

        return ExpressionReferenceSpreadsheetCellReferencesBiConsumer.with(SpreadsheetLabelStores.readOnly(labelStore),
                SpreadsheetRangeStores.readOnly(rangeToCellStore));
    }

    private SpreadsheetLabelName labelB1() {
        return SpreadsheetLabelName.with("labelB1");
    }

    private SpreadsheetCellReference cellB1() {
        return SpreadsheetCellReference.parse("B1");
    }

    private SpreadsheetLabelName labelRangeC1D2() {
        return SpreadsheetLabelName.with("labelRangeC1D2");
    }

    private SpreadsheetRange rangeC1C2() {
        return SpreadsheetRange.with(this.cellC1(), this.cellC2());
    }

    private SpreadsheetCellReference cellC1() {
        return SpreadsheetCellReference.parse("C1");
    }

    private SpreadsheetCellReference cellC2() {
        return SpreadsheetCellReference.parse("C2");
    }

    @Override
    public Class<ExpressionReferenceSpreadsheetCellReferencesBiConsumer> type() {
        return ExpressionReferenceSpreadsheetCellReferencesBiConsumer.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
