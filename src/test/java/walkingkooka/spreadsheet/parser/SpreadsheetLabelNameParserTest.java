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

package walkingkooka.spreadsheet.parser;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.SpreadsheetLabelName;
import walkingkooka.spreadsheet.SpreadsheetRowReference;

public final class SpreadsheetLabelNameParserTest extends SpreadsheetParserTestCase<SpreadsheetLabelNameParser, SpreadsheetLabelNameParserToken> {

    @Test
    public void testWrongFirstCharFail() {
        this.parseFailAndCheck("1");
    }

    @Test
    public void testColumnOnly() {
        this.parseAndCheck2("A");
    }

    @Test
    public void testColumnAndRowFail() {
        this.parseFailAndCheck("A1");
    }

    @Test
    public void testColumnAndRowFail2() {
        this.parseFailAndCheck("AA11");
    }

    @Test
    public void testMaxColumn() {
        // A1 column+row
        this.parseAndCheck2("A" + SpreadsheetRowReference.MAX);
    }

    @Test
    public void testMaxRow() {
        // A1 column+row
        this.parseAndCheck2("XFE1");
    }

    @Test
    public void testLabel() {
        this.parseAndCheck2("Hello");
    }

    @Test
    public void testLabel2() {
        this.parseAndCheck2("Hello", "...");
    }

    @Test
    public void testLabel3() {
        this.parseAndCheck2("Hello_");
    }

    @Test
    public void testLabel4() {
        this.parseAndCheck2("Hello_", "...");
    }

    @Test
    public void testLabel5() {
        this.parseAndCheck2("Hello123");
    }

    @Test
    public void testLabel6() {
        this.parseAndCheck2("Hello123", "...");
    }

    private void parseAndCheck2(final String text) {
        this.parseAndCheck2(text, "");
    }

    private void parseAndCheck2(final String text, final String textAfter) {
        this.parseAndCheck(text + textAfter,
                SpreadsheetLabelNameParserToken.with(SpreadsheetLabelName.with(text), text),
                text,
                textAfter);
    }

    @Override
    public SpreadsheetLabelNameParser createParser() {
        return SpreadsheetLabelNameParser.INSTANCE;
    }

    @Override
    public Class<SpreadsheetLabelNameParser> type() {
        return SpreadsheetLabelNameParser.class;
    }
}
