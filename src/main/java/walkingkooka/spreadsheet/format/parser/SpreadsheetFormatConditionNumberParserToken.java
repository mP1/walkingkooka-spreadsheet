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

import walkingkooka.text.cursor.parser.ParserToken;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Holds the condition number argument.
 */
public final class SpreadsheetFormatConditionNumberParserToken extends SpreadsheetFormatNonSymbolParserToken<BigDecimal> {

    static SpreadsheetFormatConditionNumberParserToken with(final BigDecimal value, final String text) {
        checkValueAndText(value, text);

        return new SpreadsheetFormatConditionNumberParserToken(value, text);
    }

    private SpreadsheetFormatConditionNumberParserToken(final BigDecimal value, final String text) {
        super(value, text);
    }

    // replaceFirstIf...................................................................................................

    @Override
    public SpreadsheetFormatConditionNumberParserToken replaceFirstIf(final Predicate<ParserToken> predicate,
                                                                      final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceFirstIf(
                this,
                predicate,
                mapper,
                SpreadsheetFormatConditionNumberParserToken.class
        );
    }
    // replaceIf........................................................................................................

    @Override
    public SpreadsheetFormatConditionNumberParserToken replaceIf(final Predicate<ParserToken> predicate,
                                                                 final Function<ParserToken, ParserToken> mapper) {
        return ParserToken.replaceIf(
                this,
                predicate,
                mapper,
                SpreadsheetFormatConditionNumberParserToken.class
        );
    }
    // visitor........................................................................................................

    @Override
    void accept(final SpreadsheetFormatParserTokenVisitor visitor) {
        visitor.visit(this);
    }

    // SpreadsheetFormatParserTokenKind ................................................................................

    @Override
    public Optional<SpreadsheetFormatParserTokenKind> kind() {
        return SpreadsheetFormatParserTokenKind.CONDITION.asOptional;
    }

    // equals ..........................................................................................................

    @Override
    boolean canBeEqual(final Object other) {
        return other instanceof SpreadsheetFormatConditionNumberParserToken;
    }
}
