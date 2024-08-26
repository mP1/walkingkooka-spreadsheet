
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

import walkingkooka.collect.list.Lists;
import walkingkooka.visit.Visiting;

import java.util.List;

/**
 * Used to collect the begin and end tokens for a {@link SpreadsheetCellRangeParserToken}.
 */
final class SpreadsheetCellRangeParserTokenSpreadsheetParserTokenVisitor extends SpreadsheetParserTokenVisitor {

    static SpreadsheetCellRangeParserTokenSpreadsheetParserTokenVisitor with() {
        return new SpreadsheetCellRangeParserTokenSpreadsheetParserTokenVisitor();
    }

    private SpreadsheetCellRangeParserTokenSpreadsheetParserTokenVisitor() {
        super();
    }

    protected Visiting startVisit(final SpreadsheetCellReferenceParserToken token) {
        return Visiting.CONTINUE;
    }

    protected void endVisit(final SpreadsheetCellReferenceParserToken token) {
        this.components.add(token);
    }

    final List<SpreadsheetCellReferenceParserToken> components = Lists.array();
}
