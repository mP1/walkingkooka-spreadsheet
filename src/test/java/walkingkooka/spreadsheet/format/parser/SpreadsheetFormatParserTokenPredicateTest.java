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
import walkingkooka.ToStringTesting;
import walkingkooka.predicate.PredicateTesting2;
import walkingkooka.predicate.Predicates;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.cursor.parser.ParserToken;
import walkingkooka.text.cursor.parser.ParserTokens;

import java.util.function.Predicate;

public final class SpreadsheetFormatParserTokenPredicateTest implements PredicateTesting2<SpreadsheetFormatParserTokenPredicate, ParserToken>,
    ClassTesting<SpreadsheetFormatParserTokenPredicate>,
    ToStringTesting<SpreadsheetFormatParserTokenPredicate> {

    @Test
    public void testTestNullFalse() {
        this.testFalse(null);
    }

    @Test
    public void testNotSpreadsheetFormatParserToken() {
        this.testFalse(
            SpreadsheetFormatParserTokenPredicate.with(Predicates.fake()),
            ParserTokens.fake()
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenFalse() {
        this.testFalse(
            SpreadsheetFormatParserTokenPredicate.with(
                SpreadsheetFormatParserToken::isHour
            ),
            SpreadsheetFormatParserToken.amPm("ampm", "ampm")
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenTrue() {
        this.testTrue(
            SpreadsheetFormatParserTokenPredicate.with(
                SpreadsheetFormatParserToken::isHour
            ),
            SpreadsheetFormatParserToken.hour("h", "h")
        );
    }

    @Test
    public void testToString() {
        final Predicate<SpreadsheetFormatParserToken> predicate = Predicates.fake();

        this.toStringAndCheck(
            SpreadsheetFormatParserTokenPredicate.with(predicate),
            predicate.toString()
        );
    }

    @Override
    public SpreadsheetFormatParserTokenPredicate createPredicate() {
        return SpreadsheetFormatParserTokenPredicate.with(
            SpreadsheetFormatParserToken::isHour
        );
    }

    @Override
    public Class<SpreadsheetFormatParserTokenPredicate> type() {
        return SpreadsheetFormatParserTokenPredicate.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
