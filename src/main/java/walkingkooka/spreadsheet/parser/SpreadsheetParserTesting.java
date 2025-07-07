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
import walkingkooka.text.cursor.parser.ParserTesting;
import walkingkooka.validation.ValidationValueTypeName;

import java.util.List;
import java.util.Optional;

public interface SpreadsheetParserTesting extends ParserTesting {

    // tokensAndCheck...................................................................................................

    default void tokensAndCheck(final SpreadsheetParser parser,
                                final SpreadsheetParserContext context,
                                final SpreadsheetParserSelectorToken... expected) {
        this.tokensAndCheck(
            parser,
            context,
            Lists.of(expected)
        );
    }

    default void tokensAndCheck(final SpreadsheetParser parser,
                                final SpreadsheetParserContext context,
                                final List<SpreadsheetParserSelectorToken> expected) {
        this.checkEquals(
            expected,
            parser.tokens(context),
            parser::toString
        );
    }

    // valueTypeAndCheck................................................................................................

    default void valueTypeAndCheck(final SpreadsheetParser parser) {
        this.valueTypeAndCheck(
            parser,
            SpreadsheetParser.NO_VALUE_TYPE
        );
    }

    default void valueTypeAndCheck(final SpreadsheetParser parser,
                                   final ValidationValueTypeName expected) {
        this.valueTypeAndCheck(
            parser,
            Optional.of(expected)
        );
    }

    default void valueTypeAndCheck(final SpreadsheetParser parser,
                                   final Optional<ValidationValueTypeName> expected) {
        this.checkEquals(
            expected,
            parser.valueType(),
            parser::toString
        );
    }
}
