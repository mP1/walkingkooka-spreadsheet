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

package walkingkooka.spreadsheet.hateos;

import walkingkooka.collect.set.Sets;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.SpreadsheetCellReference;
import walkingkooka.spreadsheet.SpreadsheetDelta;
import walkingkooka.spreadsheet.SpreadsheetFormula;
import walkingkooka.spreadsheet.SpreadsheetId;
import walkingkooka.spreadsheet.engine.SpreadsheetEngine;

public abstract class SpreadsheetEngineHateosHandlerTestCase<T> extends SpreadsheetHateosHandlerTestCase<T> {

    SpreadsheetEngineHateosHandlerTestCase() {
        super();
    }

    final SpreadsheetDelta delta() {
        return SpreadsheetDelta.with(this.spreadsheetId(), Sets.of(this.cell()));
    }

    final SpreadsheetId spreadsheetId() {
        return SpreadsheetId.with(123);
    }

    final SpreadsheetCell cell() {
        return SpreadsheetCell.with(SpreadsheetCellReference.parse("A99"), SpreadsheetFormula.with("1+2"));
    }

    @Override
    public final String typeNamePrefix() {
        return SpreadsheetEngine.class.getSimpleName();
    }
}
