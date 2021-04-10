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
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.reference.SpreadsheetCellReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetRange;

import java.util.TreeMap;

public final class TreeMapSpreadsheetLabelStoreTest extends SpreadsheetLabelStoreTestCase<TreeMapSpreadsheetLabelStore> {

    // findSimilar......................................................................................................

    @Test
    public void testFindSimilarNone() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        store.save(SpreadsheetLabelMapping.with(this.label1(), this.a1()));

        this.findSimilarAndCheck(store, this.label2().value(), 2);
    }

    @Test
    public void testFindSimilarOnly() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label = this.label1();
        final SpreadsheetCellReference cell = this.a1();
        final SpreadsheetLabelMapping mapping = label.mapping(cell);

        store.save(mapping);

        this.findSimilarAndCheck(
                store,
                label.value(),
                1,
                mapping
        );
    }

    @Test
    public void testFindSimilarOnly2() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label = this.label1();
        final SpreadsheetCellReference cell = this.a1();
        final SpreadsheetLabelMapping mapping = label.mapping(cell);

        store.save(mapping);

        this.findSimilarAndCheck(
                store,
                label.value(),
                2,
                mapping
        );
    }

    @Test
    public void testFindSimilarOnly3() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label = this.label1();
        final SpreadsheetCellReference cell = this.a1();
        final SpreadsheetLabelMapping mapping = label.mapping(cell);

        store.save(mapping);
        store.save(SpreadsheetLabelMapping.with(SpreadsheetLabelName.labelName("xyz"), this.a1()));

        this.findSimilarAndCheck(
                store,
                label.value(),
                2,
                mapping
        );
    }

    @Test
    public void testFindSimilarPartial() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetLabelName label = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetCellReference cell = this.a1();
        final SpreadsheetLabelMapping mapping = label.mapping(cell);

        store.save(mapping);

        this.findSimilarAndCheck(store, "123", 2, mapping);
    }

    @Test
    public void testFindSimilarSeveral() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetCellReference cell = this.a1();

        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping1 = label1.mapping(cell);
        store.save(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label1234");
        final SpreadsheetLabelMapping mapping2 = label2.mapping(cell);
        store.save(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetLabelName.labelName("Label999");
        store.save(SpreadsheetLabelMapping.with(label3, cell));

        this.findSimilarAndCheck(
                store,
                "Label123",
                2,
                mapping1, mapping2
        );
    }

    @Test
    public void testFindSimilarSeveral2() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();

        final SpreadsheetCellReference cell = this.a1();

        final SpreadsheetLabelName label1 = SpreadsheetLabelName.labelName("Label123");
        final SpreadsheetLabelMapping mapping1 = label1.mapping(cell);
        store.save(mapping1);

        final SpreadsheetLabelName label2 = SpreadsheetLabelName.labelName("Label1234");
        final SpreadsheetLabelMapping mapping2 = label2.mapping(cell);
        store.save(mapping2);

        final SpreadsheetLabelName label3 = SpreadsheetLabelName.labelName("Label999");
        store.save(SpreadsheetLabelMapping.with(label3, cell));

        this.findSimilarAndCheck(
                store,
                "2",
                2,
                mapping1, mapping2
        );
    }

    // loadCellReferencesOrRanges.......................................................................................

    @Test
    public void testLoadCellReferencesOrRangesNotFound() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(SpreadsheetLabelMapping.with(this.label1(), this.a1()));

        this.loadCellReferencesOrRangesAndCheck(store,
                SpreadsheetExpressionReference.labelName("unknown"),
                Sets.empty());
    }

    @Test
    public void testLoadCellReferencesOrRangesLabelToCell() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(SpreadsheetLabelMapping.with(this.label1(), this.a1()));
        store.save(SpreadsheetLabelMapping.with(this.label2(), this.a2()));

        this.loadCellReferencesOrRangesAndCheck(store,
                this.label1(),
                Sets.of(this.a1()));
    }

    @Test
    public void testLoadCellReferencesOrRangesLabelToLabelToCell() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(SpreadsheetLabelMapping.with(this.label1(), this.a1()));
        store.save(SpreadsheetLabelMapping.with(this.label2(), this.label1()));

        this.loadCellReferencesOrRangesAndCheck(store,
                this.label2(),
                Sets.of(this.a1()));
    }

    @Test
    public void testLoadCellReferencesOrRangesLabelToLabelToLabelToCell() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(SpreadsheetLabelMapping.with(this.label1(), this.a1()));
        store.save(SpreadsheetLabelMapping.with(this.label2(), this.label1()));
        store.save(SpreadsheetLabelMapping.with(this.label3(), this.label2()));

        this.loadCellReferencesOrRangesAndCheck(store,
                this.label2(),
                Sets.of(this.a1()));
    }

    @Test
    public void testLoadCellReferencesOrRangesLabelToSelf() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(SpreadsheetLabelMapping.with(this.label1(), this.label2()));
        store.save(SpreadsheetLabelMapping.with(this.label2(), this.label1()));

        this.loadCellReferencesOrRangesAndCheck(store,
                this.label1(),
                Sets.empty());
    }

    @Test
    public void testLoadCellReferencesOrRangesLabelToRange() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(SpreadsheetLabelMapping.with(this.label1(), this.range1()));
        store.save(SpreadsheetLabelMapping.with(this.label2(), this.a2()));

        this.loadCellReferencesOrRangesAndCheck(store,
                this.label1(),
                Sets.of(this.range1()));
    }

    @Test
    public void testToString() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(SpreadsheetLabelMapping.with(this.label1(), this.range1()));
        store.save(SpreadsheetLabelMapping.with(this.label2(), this.a2()));

        this.toStringAndCheck(store, "[label1=A1:A3, label2=A2]");
    }

    private SpreadsheetLabelName label1() {
        return SpreadsheetExpressionReference.labelName("label1");
    }

    private SpreadsheetLabelName label2() {
        return SpreadsheetExpressionReference.labelName("label2");
    }

    private SpreadsheetLabelName label3() {
        return SpreadsheetExpressionReference.labelName("label3");
    }

    private SpreadsheetCellReference a1() {
        return SpreadsheetExpressionReference.parseCellReference("A1");
    }

    private SpreadsheetCellReference a2() {
        return SpreadsheetExpressionReference.parseCellReference("A2");
    }

    private SpreadsheetRange range1() {
        return SpreadsheetExpressionReference.parseRange("A1:A3");
    }

    @Override
    public TreeMapSpreadsheetLabelStore createStore() {
        return TreeMapSpreadsheetLabelStore.create();
    }

    @Override
    public Class<TreeMapSpreadsheetLabelStore> type() {
        return TreeMapSpreadsheetLabelStore.class;
    }

    // TypeNameTesting..................................................................

    @Override
    public String typeNamePrefix() {
        return TreeMap.class.getSimpleName();
    }
}
