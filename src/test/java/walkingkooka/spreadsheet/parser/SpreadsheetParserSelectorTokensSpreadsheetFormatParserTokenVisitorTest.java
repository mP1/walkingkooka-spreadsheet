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

import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitorTesting;

public final class SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitorTest implements SpreadsheetFormatParserTokenVisitorTesting<SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor> {
    @Override
    public SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor();
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetParserSelectorToken.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetParserSelectorTokensSpreadsheetFormatParserTokenVisitor.class;
    }
}
