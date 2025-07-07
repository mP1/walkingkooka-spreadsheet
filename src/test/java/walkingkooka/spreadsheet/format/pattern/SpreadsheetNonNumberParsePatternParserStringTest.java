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

package walkingkooka.spreadsheet.format.pattern;

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.collect.list.Lists;
import walkingkooka.datetime.DateTimeContext;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.spreadsheet.parser.FakeSpreadsheetParserContext;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.CaseSensitivity;
import walkingkooka.text.CharSequences;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public final class SpreadsheetNonNumberParsePatternParserStringTest extends SpreadsheetNonNumberParsePatternParserTestCase<SpreadsheetNonNumberParsePatternParserString>
    implements HashCodeEqualsDefinedTesting2<SpreadsheetNonNumberParsePatternParserString> {

    private final static String PATTERN = "Pattern-123";

    @Test
    public void testParseNone() {
        this.parseFailAndCheck("Z"); // fails to match any month starting character
    }

    @Test
    public void testParseNone2() {
        this.parseFailAndCheck("Zebra");
    }

    @Test
    public void testParseInitialTooMany() {
        this.parseFailAndCheck("J"); // too many months start with J
    }

    @Test
    public void testParseInitial() {
        this.parseAndCheck2(
            "O"
        );
    }

    @Test
    public void testParseIncompleteTooMany2() {
        this.parseFailAndCheck("Ju"); // not unique matches June and July
    }

    @Test
    public void testParseIncompleteTooMany3() {
        this.parseFailAndCheck("JuQ"); // not unique matches June and July
    }

    @Test
    public void testParseIncomplete() {
        this.parseAndCheck2(
            "Jul"
        );
    }

    @Test
    public void testParseIncomplete2() {
        this.parseAndCheck2(
            "Jun"
        );
    }

    @Test
    public void testParseIncompleteCaseInsensitive() {
        this.parseAndCheck2(
            "JUN"
        );
    }

    @Test
    public void testParseComplete() {
        this.parseAndCheck2(
            "May",
            "!"
        );
    }

    @Test
    public void testParseComplete2() {
        this.parseAndCheck2(
            "December"
        );
    }

    @Test
    public void testParseComplete3() {
        this.parseAndCheck2(
            "December",
            "123"
        );
    }

    @Test
    public void testParseCompleteCaseInsensitive() {
        this.parseAndCheck2(
            "DECEMBER",
            "123"
        );
    }

    @Test
    public void testParseCompleteAll() {
        for (final String month : this.monthNames()) {
            this.parseAndCheck2(
                month,
                "123"
            );
        }
    }

    private void parseAndCheck2(final String text) {
        this.parseAndCheck2(
            text,
            ""
        );
    }

    private void parseAndCheck2(final String text,
                                final String after) {
        int index = -1;
        int i = 0;
        for (final String month : this.monthNames()) {
            if (CaseSensitivity.INSENSITIVE.startsWith(month, text) || CaseSensitivity.INSENSITIVE.equals(month, text)) {
                index = i;
                break;
            }
            i++;
        }
        this.checkNotEquals(-1, index, () -> "failed to match a month with " + CharSequences.quote(text));

        this.parseAndCheck(
            text + after,
            SpreadsheetFormulaParserToken.monthName(
                index,
                text
            ),
            text,
            after
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createParser(), PATTERN);
    }

    @Override
    public SpreadsheetNonNumberParsePatternParserString createParser() {
        return SpreadsheetNonNumberParsePatternParserString.stringChoices(
            DateTimeContext::monthNames,
            SpreadsheetFormulaParserToken::monthName,
            PATTERN
        );
    }

    @Override
    public SpreadsheetParserContext createContext() {
        return new FakeSpreadsheetParserContext() {
            @Override
            public List<String> monthNames() {
                return SpreadsheetNonNumberParsePatternParserStringTest.this.monthNames();
            }
        };
    }

    private List<String> monthNames() {
        return DateTimeContexts.basic(
                DateTimeSymbols.fromDateFormatSymbols(
                    new DateFormatSymbols(Locale.forLanguageTag("EN-AU"))
                ),
                Locale.forLanguageTag("EN-AU"),
                1900,
                20,
                LocalDateTime::now
            )
            .monthNames();
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentPattern() {
        this.checkNotEquals(
            SpreadsheetNonNumberParsePatternParserString.with(
                (c) -> Lists.of("AM/PM"),
                SpreadsheetFormulaParserToken::amPm,
                "AM/PM"
            )
        );
    }

    @Override
    public SpreadsheetNonNumberParsePatternParserString createObject() {
        return SpreadsheetNonNumberParsePatternParserString.with(
            (c) -> Lists.of("am/pm"),
            SpreadsheetFormulaParserToken::amPm,
            "am/pm"
        );
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetNonNumberParsePatternParserString> type() {
        return SpreadsheetNonNumberParsePatternParserString.class;
    }

    @Override
    public String typeNameSuffix() {
        return String.class.getSimpleName();
    }
}
