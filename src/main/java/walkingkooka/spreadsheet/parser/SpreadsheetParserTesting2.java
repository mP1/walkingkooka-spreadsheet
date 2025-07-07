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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserTesting2;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface SpreadsheetParserTesting2<P extends SpreadsheetParser> extends SpreadsheetParserTesting, ParserTesting2<P, SpreadsheetParserContext> {
    @Test
    default void testTokensWithNullContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> this.createParser().tokens(null)
        );
    }

    default void tokensAndCheck(final SpreadsheetParserContext context,
                                final SpreadsheetParserSelectorToken... expected) {
        this.tokensAndCheck(
            this.createParser(),
            context,
            Lists.of(expected)
        );
    }

    default void tokensAndCheck(final SpreadsheetParserContext context,
                                final List<SpreadsheetParserSelectorToken> expected) {
        this.tokensAndCheck(
            this.createParser(),
            context,
            expected
        );
    }

    // valueTypeAndCheck................................................................................................

    @Test
    default void testValueTypeNotNull() {
        this.checkNotEquals(
            null,
            this.createParser()
        );
    }
}
