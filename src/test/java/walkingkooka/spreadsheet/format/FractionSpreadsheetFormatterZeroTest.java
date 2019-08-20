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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class FractionSpreadsheetFormatterZeroTest extends FractionSpreadsheetFormatterTestCase<FractionSpreadsheetFormatterZero> {

    @Test
    public void testHashPattern() {
        checkPattern(FractionSpreadsheetFormatterZero.HASH, "#");
    }

    @Test
    public void testQuestionPattern() {
        checkPattern(FractionSpreadsheetFormatterZero.QUESTION_MARK, "?");
    }

    @Test
    public void testZeroPattern() {
        checkPattern(FractionSpreadsheetFormatterZero.ZERO, "0");
    }

    private void checkPattern(final FractionSpreadsheetFormatterZero zero, final String pattern) {
        assertEquals(pattern, zero.pattern(), zero.toString());
    }

    @Override
    public Class<FractionSpreadsheetFormatterZero> type() {
        return FractionSpreadsheetFormatterZero.class;
    }
}
