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

package walkingkooka.spreadsheet.format;

import org.junit.jupiter.api.Test;
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitorTesting;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPattern;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetPatternKind;
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;

public final class SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitorTest implements SpreadsheetFormatParserTokenVisitorTesting<SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testNextTextComponentWithDateEndsInYear() {
        this.nextTextComponentAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("yy")
                        .value(),
                SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmm",
                                        "mmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmm",
                                        "mmmm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mmmmm",
                                        "mmmmm"
                                )
                        )
                )
        );
    }

    @Test
    public void testNextTextComponentWithDateEndsInMonth() {
        this.nextTextComponentAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("yy/mm")
                        .value(),
                SpreadsheetPatternKind.DATE_FORMAT_PATTERN,
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "d",
                                        "d"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dd",
                                        "dd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ddd",
                                        "ddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "dddd",
                                        "dddd"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yy",
                                        "yy"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "yyyy",
                                        "yyyy"
                                )
                        )
                )
        );
    }

    @Test
    public void testNextTextComponentWithNumber() {
        this.nextTextComponentAndCheck(
                SpreadsheetPattern.parseNumberFormatPattern("$0.00")
                        .value(),
                SpreadsheetPatternKind.NUMBER_FORMAT_PATTERN,
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "#",
                                        "#"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "$",
                                        "$"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "%",
                                        "%"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ",",
                                        ","
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "/",
                                        "/"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "?",
                                        "?"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "E",
                                        "E"
                                )
                        )
                )
        );
    }

    @Test
    public void testNextTextComponentWithText() {
        this.nextTextComponentAndCheck(
                SpreadsheetPattern.parseTextFormatPattern("@\"Hello\"")
                        .value(),
                SpreadsheetPatternKind.TEXT_FORMAT_PATTERN,
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "* ",
                                        "* "
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "@",
                                        "@"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "_ ",
                                        "_ "
                                )
                        )
                )
        );
    }

    @Test
    public void testNextTextComponentWithTimeEndsInMinutes() {
        this.nextTextComponentAndCheck(
                SpreadsheetPattern.parseTimeFormatPattern("hh:mm")
                        .value(),
                SpreadsheetPatternKind.TIME_FORMAT_PATTERN,
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        ".",
                                        "."
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "0",
                                        "0"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "A/P",
                                        "A/P"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "AM/PM",
                                        "AM/PM"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "a/p",
                                        "a/p"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "am/pm",
                                        "am/pm"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "h",
                                        "h"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "hh",
                                        "hh"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "s",
                                        "s"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "ss",
                                        "ss"
                                )
                        )
                )
        );
    }

    private void nextTextComponentAndCheck(final ParserToken token,
                                           final SpreadsheetPatternKind patternKind,
                                           final SpreadsheetFormatterSelectorTextComponent textComponent) {
        this.checkEquals(
                Optional.of(textComponent),
                SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor.nextTextComponent(
                        token,
                        patternKind
                ),
                () -> token + " " + patternKind
        );
    }

    @Override
    public SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor();
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetFormatterSelectorTextComponent.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor.class;
    }
}
