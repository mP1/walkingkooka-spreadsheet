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
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;

public final class SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitorTest implements SpreadsheetFormatParserTokenVisitorTesting<SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testLastWithDateFormatPattern() {
        this.lastAndCheck(
            SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy").value(),
            SpreadsheetFormatParserTokenKind.YEAR_FULL
        );
    }

    @Test
    public void testLastWithDateTimeFormatPattern() {
        this.lastAndCheck(
            SpreadsheetPattern.parseDateTimeFormatPattern("dd/mm/yyyy").value(),
            SpreadsheetFormatParserTokenKind.YEAR_FULL
        );
    }

    @Test
    public void testLastWithNumberFormatPattern() {
        this.lastAndCheck(
            SpreadsheetPattern.parseNumberFormatPattern("$0.00").value(),
            SpreadsheetFormatParserTokenKind.DIGIT_ZERO
        );
    }

    @Test
    public void testLastWithNumberParsePattern() {
        this.lastAndCheck(
            SpreadsheetPattern.parseNumberParsePattern("$0.00;$0.00;").value(),
            SpreadsheetFormatParserTokenKind.SEPARATOR
        );
    }

    private void lastAndCheck(final ParserToken token,
                              final SpreadsheetFormatParserTokenKind expected) {
        this.lastAndCheck(
            token,
            Optional.of(expected)
        );
    }

    private void lastAndCheck(final ParserToken token,
                              final Optional<SpreadsheetFormatParserTokenKind> expected) {
        this.checkEquals(
            expected,
            SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor.last(token),
            token::toString
        );
    }

    @Override
    public SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor();
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetFormatParserTokenKind.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetFormatParserTokenKindLastSpreadsheetFormatParserTokenVisitor.class;
    }
}
