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
import walkingkooka.text.cursor.parser.ParserToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitorTest implements SpreadsheetFormatParserTokenVisitorTesting<SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testNextTextComponentUnknownIndexFails() {
        assertThrows(
                IndexOutOfBoundsException.class,
                () ->
                        SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor.nextTextComponent(
                                SpreadsheetPattern.parseDateFormatPattern("yy/mm")
                                        .value(),
                                4
                        )
        );
    }

    @Test
    public void testNextTextComponentWithDate() {
        this.nextTextComponentAndCheck(
                SpreadsheetPattern.parseDateFormatPattern("yy/mm")
                        .value(),
                3, // after the minutes,
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
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
    public void testNextTextComponentWithTime() {
        this.nextTextComponentAndCheck(
                SpreadsheetPattern.parseTimeFormatPattern("hh:mm")
                        .value(),
                3, // after the minutes,
                SpreadsheetFormatterSelectorTextComponent.with(
                        "",
                        "",
                        Lists.of(
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "m",
                                        "m"
                                ),
                                SpreadsheetFormatterSelectorTextComponentAlternative.with(
                                        "mm",
                                        "mm"
                                )
                        )
                )
        );
    }

    private void nextTextComponentAndCheck(final ParserToken token,
                                           final int index,
                                           final SpreadsheetFormatterSelectorTextComponent textComponent) {
        this.checkEquals(
                Optional.of(textComponent),
                SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor.nextTextComponent(
                        token,
                        index
                )
        );
    }

    @Override
    public SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetFormatterSelectorTextComponentNextTextComponentSpreadsheetFormatParserTokenVisitor(
                -1
        );
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
