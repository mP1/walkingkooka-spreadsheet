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
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class SpreadsheetConditionRightParserTokenTestCase<T extends SpreadsheetConditionRightParserToken> extends SpreadsheetParentParserTokenTestCase<T> {
    SpreadsheetConditionRightParserTokenTestCase() {
        super();
    }

    @Test
    public final void testSetConditionLeftWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> this.createToken()
                        .setConditionLeft(null)
        );
    }

    final void setConditionLeftAndCheck(final T token,
                                        final SpreadsheetParserToken left,
                                        final SpreadsheetConditionParserToken expected) {
        this.checkEquals(
                expected,
                token.setConditionLeft(left),
                () -> token + " setConditionLeft " + left
        );

        this.checkEquals(
                left,
                expected.left(),
                "left"
        );

        this.checkEquals(
                token,
                expected.right(),
                "right"
        );
    }

    abstract SpreadsheetSymbolParserToken symbolParserToken();

    private final static String ARGUMENT_TEXT = "123";

    @Override
    public final List<ParserToken> tokens() {
        return Lists.of(
                this.symbolParserToken(),
                this.number(ARGUMENT_TEXT)
        );
    }

    @Override
    public final String text() {
        return this.symbolParserToken() + ARGUMENT_TEXT;
    }

    @Override
    public T createDifferentToken() {
        final String symbol = this.symbolParserToken()
                .text();
        final String differentNumber = "999";

        return this.createToken(
                symbol + differentNumber,
                differentNumber
        );
    }

    private T createToken(final String text,
                          final String number) {
        final ParserToken symbol = this.symbolParserToken();
        final ParserToken parameter = SpreadsheetParserToken.number(
                Lists.of(
                        this.number(number)
                ),
                number
        );

        return this.createToken(
                text, Lists.of(
                        symbol,
                        parameter
                )
        );
    }

    final SpreadsheetParserToken number(final String text) {
        return SpreadsheetParserToken.number(
                Lists.of(
                        SpreadsheetParserToken.digits(
                                text,
                                text
                        )
                ),
                text
        );
    }
}
