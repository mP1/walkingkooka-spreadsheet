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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetLabelMapping;
import walkingkooka.spreadsheet.SpreadsheetRange;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetCellReference;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetLabelName;

import java.util.TreeMap;

public final class TreeMapSpreadsheetLabelStoreTest extends SpreadsheetLabelStoreTestCase<TreeMapSpreadsheetLabelStore> {

    @Test
    public void testLoadCellReferencesOrRangesNotFound() {
        final TreeMapSpreadsheetLabelStore store = this.createStore();
        store.save(SpreadsheetLabelMapping.with(this.label1(), this.a1()));

        this.loadCellReferencesOrRangesAndCheck(store,
                SpreadsheetLabelName.with("unknown"),
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
        return SpreadsheetLabelName.with("label1");
    }

    private SpreadsheetLabelName label2() {
        return SpreadsheetLabelName.with("label2");
    }

    private SpreadsheetLabelName label3() {
        return SpreadsheetLabelName.with("label3");
    }

    private SpreadsheetCellReference a1() {
        return SpreadsheetCellReference.parse("A1");
    }

    private SpreadsheetCellReference a2() {
        return SpreadsheetCellReference.parse("A2");
    }

    private SpreadsheetRange range1() {
        return SpreadsheetRange.parse("A1:A3");
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
