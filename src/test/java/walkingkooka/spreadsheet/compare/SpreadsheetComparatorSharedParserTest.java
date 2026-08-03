
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

package walkingkooka.spreadsheet.compare;

import org.junit.jupiter.api.Test;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;

public final class SpreadsheetComparatorSharedParserTest extends SpreadsheetComparatorSharedTestCase<SpreadsheetComparatorSharedParser, SpreadsheetParserSelector> {

    @Test
    public void testCompareLess() {
        this.compareAndCheckLess(
            SpreadsheetParserSelector.parse("date"),
            SpreadsheetParserSelector.parse("time")
        );
    }

    @Test
    public void testCompareEqualsIgnoresParameterValues() {
        this.compareAndCheckEquals(
            SpreadsheetParserSelector.parse("date 222"),
            SpreadsheetParserSelector.parse("date 111")
        );
    }

    @Override
    public SpreadsheetComparatorSharedParser createComparator() {
        return SpreadsheetComparatorSharedParser.INSTANCE;
    }

    @Override
    public SpreadsheetComparatorContext createContext() {
        return new FakeSpreadsheetComparatorContext();
    }

    // toString.........................................................................................................

    @Test
    public void tesToString() {
        this.toStringAndCheck(
            this.createComparator(),
            "parser"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetComparatorSharedParser> type() {
        return SpreadsheetComparatorSharedParser.class;
    }
}
