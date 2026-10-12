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

package walkingkooka.spreadsheet.formula;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.environment.HasEnvironmentContextTesting;
import walkingkooka.reflect.PackagePrivateClassTesting;
import walkingkooka.reflect.TypeNameTesting;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContexts;
import walkingkooka.text.cursor.parser.InvalidCharacterExceptionFactory;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.ParserTesting2;
import walkingkooka.tree.expression.ExpressionNumberContextTesting;
import walkingkooka.tree.expression.HasExpressionNumberKindTesting;

public final class SpreadsheetFormulaParserValueSeparatorTest implements ParserTesting2<SpreadsheetFormulaParserValueSeparator, SpreadsheetParserContext>,
    ExpressionNumberContextTesting,
    HasEnvironmentContextTesting,
    HasExpressionNumberKindTesting,
    PackagePrivateClassTesting<SpreadsheetFormulaParserValueSeparator>,
    TypeNameTesting<SpreadsheetFormulaParserValueSeparator>,
    ToStringTesting<SpreadsheetFormulaParserValueSeparator> {

    final static char VALUE_SEPARATOR = ',';

    @Test
    public void testIncorrectCharacterFails() {
        this.checkNotEquals("@", VALUE_SEPARATOR, "valueSeparator");

        this.parseFailAndCheck("@");
    }

    @Test
    public void testParseMatch() {
        final String text = VALUE_SEPARATOR + "";

        this.parseAndCheck(
            text,
            SpreadsheetFormulaParserToken.valueSeparatorSymbol(text, text),
            text
        );
    }

    @Test
    public void testParseMatch2() {
        final String text = VALUE_SEPARATOR + "";
        final String after = "@";

        this.parseAndCheck(
            text + after,
            SpreadsheetFormulaParserToken.valueSeparatorSymbol(text, text),
            text,
            after
        );
    }

    // ensure only consumes a single character
    @Test
    public void testParseMatch3() {
        final String text = VALUE_SEPARATOR + "";

        this.parseAndCheck(
            text + text,
            SpreadsheetFormulaParserToken.valueSeparatorSymbol(text, text),
            text,
            text
        );
    }

    @Test
    public void testEqualsDifferentValueSeparator() {
        final char c = ';';
        this.checkNotEquals(c, VALUE_SEPARATOR, "valueSeparator");
        final String text = c + "";

        this.parseAndCheck(
            SpreadsheetFormulaParserValueSeparator.INSTANCE,
            new FakeSpreadsheetParserContext() {
                @Override
                public char valueSeparator() {
                    return c;
                }
            },
            text,
            SpreadsheetFormulaParserToken.valueSeparatorSymbol(text, text),
            text
        );
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return SpreadsheetParserContexts.basic(
            InvalidCharacterExceptionFactory.COLUMN_AND_LINE,
            this.dateTimeContext(),
            EXPRESSION_NUMBER_CONTEXT,
            VALUE_SEPARATOR
        );
    }

    @Override
    public SpreadsheetFormulaParserValueSeparator createParser() {
        return SpreadsheetFormulaParserValueSeparator.INSTANCE;
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            this.createParser(),
            ","
        );
    }

    @Override
    public Class<SpreadsheetFormulaParserValueSeparator> type() {
        return SpreadsheetFormulaParserValueSeparator.class;
    }

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String typeNamePrefix() {
        return "Spreadsheet";
    }

    @Override
    public String typeNameSuffix() {
        return Parser.class.getSimpleName();
    }
}
