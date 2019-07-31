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

import walkingkooka.ToStringBuilder;
import walkingkooka.collect.list.Lists;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatBigDecimalParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.visit.Visiting;

import java.util.List;

final class SpreadsheetTextFormatterNumberPatternsSpreadsheetFormatParserTokenVisitor extends SpreadsheetFormatParserTokenVisitor {

    static List<SpreadsheetFormatBigDecimalParserToken> collect(final ParserToken token) {
        final SpreadsheetTextFormatterNumberPatternsSpreadsheetFormatParserTokenVisitor visitor = new SpreadsheetTextFormatterNumberPatternsSpreadsheetFormatParserTokenVisitor();
        visitor.accept(token);
        return visitor.tokens;
    }

    SpreadsheetTextFormatterNumberPatternsSpreadsheetFormatParserTokenVisitor() {
        super();
    }

    @Override
    protected Visiting startVisit(final SpreadsheetFormatBigDecimalParserToken token) {
        this.tokens.add(token);
        return Visiting.CONTINUE;
    }

    private final List<SpreadsheetFormatBigDecimalParserToken> tokens = Lists.array();

    @Override
    public String toString() {
        return ToStringBuilder.empty()
                .value(this.tokens)
                .build();
    }
}
