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
import walkingkooka.collect.map.Maps;
import walkingkooka.net.UrlFragment;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenKind;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitor;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.visit.Visiting;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetPatternKindTest implements ClassTesting<SpreadsheetPatternKind> {

    @Test
    public void testDateTimeFormat() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
                SpreadsheetDateTimeFormatPattern.class
        );
    }

    @Test
    public void testDateTimeParse() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN,
                SpreadsheetDateTimeParsePattern.class
        );
    }

    @Test
    public void testTextFormat() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
                SpreadsheetTextFormatPattern.class
        );
    }

    @Test
    public void testTimeFormat() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
                SpreadsheetTimeFormatPattern.class
        );
    }

    @Test
    public void testTimeParse() {
        this.typeNameAndCheck(
                SpreadsheetPatternKind.TIME_PARSE_PATTERN,
                SpreadsheetTimeParsePattern.class
        );
    }

    private void typeNameAndCheck(final SpreadsheetPatternKind kind,
                                  final Class<? extends SpreadsheetPattern> expected) {
        this.checkEquals(
                JsonNodeContext.computeTypeName(expected),
                kind.typeName(),
                kind::toString
        );
    }

    @Test
    public void testFromTypeNameFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPatternKind.fromTypeName("???")
        );
    }

    @Test
    public void testFromTypeName() {
        this.checkEquals(
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
                SpreadsheetPatternKind.fromTypeName(
                        SpreadsheetPatternKind.TEXT_FORMAT_PATTERN.typeName()
                )
        );
    }

    @Test
    public void testFromTypeNameAll() {
        for (final SpreadsheetPatternKind kind : SpreadsheetPatternKind.values()) {

            this.checkEquals(
                    kind,
                    SpreadsheetPatternKind.fromTypeName(
                            kind.typeName()
                    )
            );
        }
    }

    // parse............................................................................................................

    @Test
    public void testParseDateFormat() {
        this.parseAndCheck(
                "yyyy/mm/dd",
                SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
                SpreadsheetPattern::parseDateFormatPattern
        );
    }

    @Test
    public void testParseDateParse() {
        this.parseAndCheck(
                "yyyy/mm/dd;yyyy/mm/dd;",
                SpreadsheetPatternKind.DATE_PARSE_PATTERN,
                SpreadsheetPattern::parseDateParsePattern
        );
    }

    @Test
    public void testParseDateTimeFormat() {
        this.parseAndCheck(
                "yyyy/mm/dd",
                SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
                SpreadsheetPattern::parseDateTimeFormatPattern
        );
    }

    @Test
    public void testParseDateTimeParse() {
        this.parseAndCheck(
                "yyyy/mm/dd;yyyy/mm/dd;",
                SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN,
                SpreadsheetPattern::parseDateTimeParsePattern
        );
    }

    @Test
    public void testParseNumberFormat() {
        this.parseAndCheck(
                "$0.00",
                SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN,
                SpreadsheetPattern::parseNumberFormatPattern
        );
    }

    @Test
    public void testParseNumberParse() {
        this.parseAndCheck(
                "$0.00,$0.000;",
                SpreadsheetPatternKind.NUMBER_PARSE_PATTERN,
                SpreadsheetPattern::parseNumberParsePattern
        );
    }

    @Test
    public void testParseTextFormat() {
        this.parseAndCheck(
                "@@@",
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
                SpreadsheetPattern::parseTextFormatPattern
        );
    }

    @Test
    public void testParseTimeFormat() {
        this.parseAndCheck(
                "hh/mm/ss",
                SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
                SpreadsheetPattern::parseTimeFormatPattern
        );
    }

    @Test
    public void testParseTimeParse() {
        this.parseAndCheck(
                "hh/mm/ss;hh/mm/ss;",
                SpreadsheetPatternKind.TIME_PARSE_PATTERN,
                SpreadsheetPattern::parseTimeParsePattern
        );
    }

    private void parseAndCheck(final String pattern,
                               final SpreadsheetPatternKind kind,
                               final Function<String, SpreadsheetPattern> expected) {
        this.checkEquals(
                expected.apply(pattern),
                kind.parse(pattern),
                () -> "parse " + CharSequences.quoteAndEscape(pattern)
        );
    }

    // HasUrlFragment....................................................................................................

    @Test
    public void testUrlFragmentDateFormatPattern() {
        this.checkEquals(
                UrlFragment.with("/pattern/date-format"),
                SpreadsheetPatternKind.DATE_FORMAT_PATTERN.urlFragment()
        );
    }

    @Test
    public void testUrlFragmentDateTimeFormatPattern() {
        this.checkEquals(
                UrlFragment.with("/pattern/date-time-format"),
                SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN.urlFragment()
        );
    }


    @Test
    public void testUrlFragmentTimeParsePattern() {
        this.checkEquals(
                UrlFragment.with("/pattern/time-parse"),
                SpreadsheetPatternKind.TIME_PARSE_PATTERN.urlFragment()
        );
    }

    // isFormatPattern..................................................................................................

    @Test
    public void testIsFormatPatternDateFormatPattern() {
        this.isFormatPatternAndCheck(
                SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
                true
        );
    }

    @Test
    public void testIsFormatPatternDateParsePattern() {
        this.isFormatPatternAndCheck(
                SpreadsheetPatternKind.DATE_PARSE_PATTERN,
                false
        );
    }

    private void isFormatPatternAndCheck(final SpreadsheetPatternKind kind,
                                         final boolean expected) {
        this.checkEquals(
                expected,
                kind.isFormatPattern(),
                () -> kind + " isFormatPattern"
        );
    }

    // spreadsheetFormatParserTokenKinds................................................................................

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithDateFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
                SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
                "dd/mm/yyyy \"Hello\""
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithDateParsePattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
                SpreadsheetPatternKind.DATE_PARSE_PATTERN,
                "dd/mm/yyyy \"Hello\";dd/mm/yyyy"
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithDateTimeFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
                SpreadsheetPatternKind.DATE_TIME_FORMAT_PATTERN,
                "dd/mm/yyyy hh:mm:ss\"Hello\""
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithDateTimeParsePattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
                SpreadsheetPatternKind.DATE_TIME_PARSE_PATTERN,
                "dd/mm/yyyy hh:mm:ss\"Hello\";dd/mm/yyyy hh:mm:ss"
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithNumberFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
                SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN,
                "00.00 \"Hello\""
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithNumberParsePattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
                SpreadsheetPatternKind.NUMBER_PARSE_PATTERN,
                "$0.00 \"Hello\";00.00%"
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithTextFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
                "@\"Hello\"*a"
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithTimeFormatPattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
                SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
                "hh:mm:ss.000 \"Hello\""
        );
    }

    @Test
    public void testSpreadsheetFormatParserTokenKindsWithTimeParsePattern() {
        this.spreadsheetFormatParserTokenKindsAndCheck(
                SpreadsheetPatternKind.TIME_PARSE_PATTERN,
                "hh:mm:ss \"Hello\";hh:mm:ss"
        );
    }

    private void spreadsheetFormatParserTokenKindsAndCheck(final SpreadsheetPatternKind patternKind,
                                                           final String pattern) {
        final SpreadsheetPattern spreadsheetPattern = patternKind.parse(pattern);

        final Map<SpreadsheetFormatParserToken, SpreadsheetFormatParserTokenKind> tokenToKinds = Maps.ordered();
        new SpreadsheetFormatParserTokenVisitor() {

            @Override
            protected Visiting startVisit(final SpreadsheetFormatParserToken token) {
                token.kind()
                        .ifPresent(k ->
                        {
                            if (false == patternKind.spreadsheetFormatParserTokenKinds().contains(k)) {
                                tokenToKinds.put(token, k);
                            }
                        });
                return super.startVisit(token);
            }
        }.accept(
                spreadsheetPattern.value()
        );

        this.checkEquals(
                Maps.empty(),
                tokenToKinds,
                () -> pattern + " " + patternKind
        );
    }

    // checkSameOrFail..................................................................................................

    @Test
    public void testCheckSameOrFailWithNullPatternFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPatternKind.DATE_FORMAT_PATTERN.checkSameOrFail(null)
        );
    }

    @Test
    public void testCheckSameOrFail() {
        SpreadsheetPatternKind.DATE_FORMAT_PATTERN.checkSameOrFail(
                SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy")
        );
    }

    @Test
    public void testCheckSameOrFailInvalidThrows() {
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPatternKind.DATE_FORMAT_PATTERN.checkSameOrFail(
                        SpreadsheetPattern.parseTimeParsePattern("hh:mm")
                )
        );

        this.checkEquals(
                "Pattern \"hh:mm\" is not a TIME_PARSE_PATTERN.",
                thrown.getMessage(),
                "message"
        );
    }

    // patternPatch.....................................................................................................

    @Test
    public void testPatternPatchWithNullPatternFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetPatternKind.DATE_FORMAT_PATTERN.patternPatch(
                        null,
                        JsonNodeMarshallContexts.fake()
                )
        );
    }

    @Test
    public void testPatternPatchWithInvalidPatternFails() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SpreadsheetPatternKind.DATE_FORMAT_PATTERN.patternPatch(
                        SpreadsheetPattern.parseTextFormatPattern("@@@"),
                        JsonNodeMarshallContexts.fake()
                )
        );
    }

    @Test
    public void testPatternPatchWithSpreadsheetFormatPattern() {
        final SpreadsheetFormatPattern pattern = SpreadsheetPattern.parseDateFormatPattern("dd/mm/yyyy");
        final JsonNodeMarshallContext context = JsonNodeMarshallContexts.basic();

        this.checkEquals(
                SpreadsheetDelta.formatPatternPatch(
                        pattern,
                        context
                ),
                pattern.kind().patternPatch(
                        pattern,
                        context
                )
        );
    }

    @Test
    public void testPatternPatchWithSpreadsheetParsePattern() {
        final SpreadsheetParsePattern pattern = SpreadsheetPattern.parseDateParsePattern("dd/mm/yyyy");
        final JsonNodeMarshallContext context = JsonNodeMarshallContexts.basic();

        this.checkEquals(
                SpreadsheetDelta.parsePatternPatch(
                        pattern,
                        context
                ),
                pattern.kind().patternPatch(
                        pattern,
                        context
                )
        );
    }

    // ClassTesting......................................................................................................

    @Override
    public Class<SpreadsheetPatternKind> type() {
        return SpreadsheetPatternKind.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
