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

import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatNumberParserToken;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

final class SpreadsheetNumberPatternsSpreadsheetFormatParserTokenVisitor extends SpreadsheetPatternsSpreadsheetFormatParserTokenVisitor<SpreadsheetFormatNumberParserToken> {

    static List<SpreadsheetFormatNumberParserToken> collect(final ParserToken token) {
        return new SpreadsheetNumberPatternsSpreadsheetFormatParserTokenVisitor()
                .acceptAndCollect(token);
    }

    SpreadsheetNumberPatternsSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatNumberParserToken token) {
        this.tokens.add(token);
        return Visiting.CONTINUE;
    }
}
