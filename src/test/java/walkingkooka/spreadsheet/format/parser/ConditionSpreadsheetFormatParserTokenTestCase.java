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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class ConditionSpreadsheetFormatParserTokenTestCase<T extends ConditionSpreadsheetFormatParserToken> extends ParentSpreadsheetFormatParserTokenTestCase<T> {

    ConditionSpreadsheetFormatParserTokenTestCase() {
        super();
    }

    @Test
    public final void testWithMissingNumberTokenFails() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken("", this.whitespace()));
    }

    @Test
    public final void testWithMissingNumberTokenFails2() {
        assertThrows(IllegalArgumentException.class, () -> this.createToken("", this.whitespace(), this.whitespace()));
    }

    // kind............................................................................................................

    @Test
    public final void testKind() {
        this.kindAndCheck(
            SpreadsheetFormatParserTokenKind.CONDITION
        );
    }

    // helpers..........................................................................................................

    @Override
    abstract T createToken(final String text,
                           final List<ParserToken> tokens);

    @Override
    public final String text() {
        return this.operatorSymbol().text() + NUMBER1;
    }

    abstract SymbolSpreadsheetFormatParserToken operatorSymbol();

    final SpreadsheetFormatParserToken rightToken() {
        return this.number1();
    }

    @Override
    List<ParserToken> tokens() {
        return Lists.of(this.operatorSymbol(), this.rightToken());
    }

    @Override
    public T createDifferentToken() {
        return this.createToken(this.operatorSymbol().text() + NUMBER2,
            Lists.of(this.operatorSymbol(), this.number2()));
    }
}
