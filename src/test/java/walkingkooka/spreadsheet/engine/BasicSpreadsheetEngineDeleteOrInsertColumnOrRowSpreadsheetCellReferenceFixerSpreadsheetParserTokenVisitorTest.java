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

import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitor;
import walkingkooka.text.cursor.parser.spreadsheet.SpreadsheetParserTokenVisitorTesting;

public final class BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitorTest extends BasicSpreadsheetEngineTestCase<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor>
        implements SpreadsheetParserTokenVisitorTesting<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> {

    @Override
    public BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor createVisitor() {
        return new BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor(BasicSpreadsheetEngineDeleteOrInsertColumnOrRowRow.with(0, 0, null, null));
    }

    @Override
    public Class<BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor> type() {
        return BasicSpreadsheetEngineDeleteOrInsertColumnOrRowSpreadsheetCellReferenceFixerSpreadsheetParserTokenVisitor.class;
    }

    // TypeNameTesting..........................................................................

    @Override
    public String typeNameSuffix() {
        return SpreadsheetParserTokenVisitor.class.getSimpleName();
    }
}
