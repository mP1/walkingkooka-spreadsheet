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

import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.validation.ValidationValueTypeName;

import java.util.Optional;

public final class SpreadsheetParsers implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetParser}
     */
    public static SpreadsheetParser fake() {
        return new FakeSpreadsheetParser();
    }

    /**
     * {@see ParserSpreadsheetParser}
     */
    public static SpreadsheetParser parser(final Parser<SpreadsheetParserContext> parser,
                                           final Optional<ValidationValueTypeName> valueType) {
        return ParserSpreadsheetParser.with(
            parser,
            valueType
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetParsers() {
        throw new UnsupportedOperationException();
    }
}
