/*
 * Copyright 2018 Miroslav Pokorny (github.com/mP1)
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
 *
 */

package walkingkooka.spreadsheet;

import org.junit.Test;
import walkingkooka.spreadsheet.style.SpreadsheetCellStyle;
import walkingkooka.spreadsheet.style.SpreadsheetTextStyle;
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;

public final class SpreadsheetFormattedCellEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetFormattedCell> {

    private final static String TEXT = "abc123";

    @Test
    public void testDifferentText() {
        this.checkNotEquals(SpreadsheetFormattedCell.with("different", this.style()));
    }

    @Test
    public void testDifferentStyle() {
        this.checkNotEquals(SpreadsheetFormattedCell.with(TEXT, this.style().setText(SpreadsheetTextStyle.EMPTY.setItalics(SpreadsheetTextStyle.ITALICS))));
    }

    @Override
    protected SpreadsheetFormattedCell createObject() {
        return SpreadsheetFormattedCell.with(TEXT, this.style());
    }

    private SpreadsheetCellStyle style() {
        return SpreadsheetCellStyle.EMPTY.setText(SpreadsheetTextStyle.EMPTY.setBold(SpreadsheetTextStyle.BOLD));
    }
}
