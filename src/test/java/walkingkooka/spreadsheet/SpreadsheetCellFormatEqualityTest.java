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
import walkingkooka.test.HashCodeEqualsDefinedEqualityTestCase;
import walkingkooka.text.spreadsheetformat.SpreadsheetTextFormatter;
import walkingkooka.text.spreadsheetformat.SpreadsheetTextFormatters;

import java.util.Optional;

public final class SpreadsheetCellFormatEqualityTest extends HashCodeEqualsDefinedEqualityTestCase<SpreadsheetCellFormat> {

    private final static String TEXT = "abc123";
    private final static Optional<SpreadsheetTextFormatter<?>> FORMATTER = Optional.of(SpreadsheetTextFormatters.fake());

    @Test
    public void testBothNoFormatter() {
        this.checkEqualsAndHashCode(this.withoutFormatter(), this.withoutFormatter());
    }

    @Test
    public void testDifferentText() {
        this.checkNotEquals(SpreadsheetCellFormat.with("different", FORMATTER));
    }

    @Test
    public void testDifferentFormatter() {
        this.checkNotEquals(SpreadsheetCellFormat.with(TEXT, Optional.of(SpreadsheetTextFormatters.general())));
    }

    @Test
    public void testDifferentNoFormatter() {
        this.checkNotEquals(this.withoutFormatter());
    }

    private SpreadsheetCellFormat withoutFormatter() {
        return SpreadsheetCellFormat.with(TEXT, SpreadsheetCellFormat.NO_FORMATTER);
    }

    @Override
    protected SpreadsheetCellFormat createObject() {
        return SpreadsheetCellFormat.with(TEXT, FORMATTER);
    }
}
