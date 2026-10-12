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

import walkingkooka.datetime.DateTimeContextTesting;
import walkingkooka.spreadsheet.color.SpreadsheetColorContexts;
import walkingkooka.spreadsheet.provider.SpreadsheetProviderContextTesting;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.ParserContextTesting;
import walkingkooka.tree.expression.ExpressionNumberContextTesting;

/**
 * Mixing testing interface for {@link SpreadsheetParserContext}
 */
public interface SpreadsheetParserContextTesting extends ParserContextTesting,
    SpreadsheetProviderContextTesting,
    DateTimeContextTesting,
    ExpressionNumberContextTesting {

    char VALUE_SEPARATOR = ',';

    SpreadsheetParserContext SPREADSHEET_PARSER_CONTEXT = SpreadsheetParserContexts.basic(
        SpreadsheetColorContexts.spreadsheetMetadataDefaults(),
        InvalidCharacterExceptionFactory.POSITION_EXPECTED,
        DATE_TIME_CONTEXT,
        EXPRESSION_NUMBER_CONTEXT,
        VALUE_SEPARATOR
    );
}
