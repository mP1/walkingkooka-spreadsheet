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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.validation.ValueType;

import java.util.Optional;

public final class SpreadsheetParsers implements PublicStaticHelper {

    /**
     * {@see FakeSpreadsheetParser}
     */
    public static SpreadsheetParser fake() {
        return new FakeSpreadsheetParser();
    }

    /**
     * A {@link SpreadsheetParser} that accepts integers, decimal and scientific numbers.
     */
    public static SpreadsheetParser general() {
        return GENERAL;
    }

    private final static SpreadsheetParser GENERAL = SpreadsheetPattern.parseNumberParsePattern(
        "0.0E+0;" + // scientific
        "0E+0;" + // scientific without decimals
            "#.##;" + // decimal
            "#" // integer
    ).parser()
        .setToString("general");

    /**
     * {@see ParserSpreadsheetParser}
     */
    public static SpreadsheetParser parser(final Parser<SpreadsheetParserContext> parser,
                                           final Optional<ValueType> valueType) {
        return ParserSpreadsheetParser.with(
            parser,
            valueType
        );
    }

    /**
     * {@see ToStringSpreadsheetParser}
     */
    public static SpreadsheetParser toString(final SpreadsheetParser parser,
                                             final String toString) {
        return ToStringSpreadsheetParser.with(
            parser,
            toString
        );
    }

    /**
     * {@see WholeNumberSpreadsheetParser}
     */
    public static SpreadsheetParser wholeNumber() {
        return WholeNumberSpreadsheetParser.INSTANCE;
    }

    /**
     * Stop creation
     */
    private SpreadsheetParsers() {
        throw new UnsupportedOperationException();
    }
}
