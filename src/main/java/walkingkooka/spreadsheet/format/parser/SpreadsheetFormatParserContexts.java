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

package walkingkooka.spreadsheet.format.parser;

import walkingkooka.InvalidCharacterException;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;

import java.util.function.BiFunction;

public final class SpreadsheetFormatParserContexts implements PublicStaticHelper {

    /**
     * {@see BasicSpreadsheetFormatParserContext}
     */
    public static SpreadsheetFormatParserContext basic(final BiFunction<Parser<?>, TextCursor, InvalidCharacterException> invalidCharacterExceptionFactory) {
        return BasicSpreadsheetFormatParserContext.with(invalidCharacterExceptionFactory);
    }

    /**
     * {@see FakeSpreadsheetFormatParserContext}
     */
    public static SpreadsheetFormatParserContext fake() {
        return new FakeSpreadsheetFormatParserContext();
    }

    /**
     * Stop creation.
     */
    private SpreadsheetFormatParserContexts() {
        throw new UnsupportedOperationException();
    }
}
