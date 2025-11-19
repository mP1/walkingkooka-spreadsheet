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

import walkingkooka.spreadsheet.parser.SpreadsheetParser;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.Parsers;
import walkingkooka.validation.ValueType;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Base class for any class that creates a {@link SpreadsheetParser} parse a {@link DateTimeFormatter}.
 */
abstract class SpreadsheetNonNumberParsePattern extends SpreadsheetParsePattern {

    // ctor.............................................................................................................

    /**
     * Package private ctor use factory
     */
    SpreadsheetNonNumberParsePattern(final ParserToken token) {
        super(token);
    }

    // HasParser........................................................................................................

    /**
     * Creates a {@link Parsers#alternatives(List)} that tries each of the individual patterns until success.
     */
    @Override final SpreadsheetParser createParser() {
        return SpreadsheetNonNumberParsePatternSpreadsheetParserSpreadsheetFormatParserTokenVisitor.toParser(
            this.value(),
            this.valueType()
        );
    }

    /**
     * The {@link ValueType} returned by the returned {@link SpreadsheetParser#valueType()}.
     */
    abstract ValueType valueType();
}
