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

package walkingkooka.spreadsheet.engine;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.reference.store.SpreadsheetLabelStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetRangeStores;
import walkingkooka.spreadsheet.reference.store.SpreadsheetReferenceStores;
import walkingkooka.spreadsheet.store.SpreadsheetCellStores;

walkingkooka.reflect.*;

public final class BasicSpreadsheetEngineFillCellsTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineFillCells>
        implements ToStringTesting<BasicSpreadsheetEngineFillCells> {

    @Test
    public void testToString() {
        final BasicSpreadsheetEngine engine = BasicSpreadsheetEngine.with(SpreadsheetId.with(123),
                SpreadsheetCellStores.fake(),
                SpreadsheetReferenceStores.fake(),
                SpreadsheetLabelStores.fake(),
                SpreadsheetReferenceStores.fake(),
                SpreadsheetRangeStores.fake(),
                SpreadsheetRangeStores.fake()
        );
        this.toStringAndCheck(new BasicSpreadsheetEngineFillCells(engine, null), engine.toString());
    }

    @Override
    public Class<BasicSpreadsheetEngineFillCells> type() {
        return BasicSpreadsheetEngineFillCells.class;
    }

    @Override
    public String typeNameSuffix() {
        return "FillCells";
    }
}
