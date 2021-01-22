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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitorTesting;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.ParserTesting2;

import java.time.temporal.ChronoField;

public abstract class SpreadsheetParsePatterns2ParserTestCase<P extends SpreadsheetParsePatterns2Parser>
        extends SpreadsheetParsePatterns2TestCase<P>
        implements ParserTesting2<P, SpreadsheetParserContext>,
        ToStringTesting<P>,
        TypeNameTesting<P> {

    SpreadsheetParsePatterns2ParserTestCase() {
        super();
    }

    // TypeNameTesting..................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetParsePatterns2Parser.class.getSimpleName();
    }
}
