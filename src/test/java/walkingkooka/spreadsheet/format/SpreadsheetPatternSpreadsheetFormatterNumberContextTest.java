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
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserToken;

public final class SpreadsheetPatternSpreadsheetFormatterNumberContextTest extends SpreadsheetPatternSpreadsheetFormatterNumberTestCase<SpreadsheetPatternSpreadsheetFormatterNumberContext> {

    @Test
    public void testToStringCurrencyTrue() {
        this.toStringAndCheck(
            SpreadsheetPatternSpreadsheetFormatterNumberContext.with(
                true, // currency
                false, // suppressMinusSignsWithinParens
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.NOT_REQUIRED, "123", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.INCLUDE),
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.REQUIRED, "456", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.INCLUDE),
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.NOT_REQUIRED, "789", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.NONE),
                SpreadsheetPatternSpreadsheetFormatterNumber.with(
                    SpreadsheetFormatParserToken.number(
                        Lists.of(
                            SpreadsheetFormatParserToken.digit("1", "1")
                        ),
                        "1"
                    ),
                    false // suppressMinusSignsWithinParens
                ),
                SpreadsheetFormatterContexts.fake()),
            "currency 123-456789"
        );
    }

    @Test
    public void testToStringCurrencyFalse() {
        this.toStringAndCheck(
            SpreadsheetPatternSpreadsheetFormatterNumberContext.with(
                false, // currency
                false, // suppressMinusSignsWithinParens
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.NOT_REQUIRED, "123", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.INCLUDE),
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.REQUIRED, "456", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.INCLUDE),
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.NOT_REQUIRED, "789", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.NONE),
                SpreadsheetPatternSpreadsheetFormatterNumber.with(
                    SpreadsheetFormatParserToken.number(
                        Lists.of(
                            SpreadsheetFormatParserToken.digit("1", "1")
                        ),
                        "1"
                    ),
                    false // suppressMinusSignsWithinParens
                ),
                SpreadsheetFormatterContexts.fake()),
            "123-456789"
        );
    }

    @Test
    public void testToStringSuppressMinusSignsWithinParensFalse() {
        this.toStringAndCheck(
            SpreadsheetPatternSpreadsheetFormatterNumberContext.with(
                false, // currency
                false, // suppressMinusSignsWithinParens
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.NOT_REQUIRED, "123", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.INCLUDE),
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.REQUIRED, "456", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.INCLUDE),
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.NOT_REQUIRED, "789", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.NONE),
                SpreadsheetPatternSpreadsheetFormatterNumber.with(
                    SpreadsheetFormatParserToken.number(
                        Lists.of(
                            SpreadsheetFormatParserToken.digit("1", "1")
                        ),
                        "1"
                    ),
                    false // suppressMinusSignsWithinParens
                ),
                SpreadsheetFormatterContexts.fake()),
            "123-456789"
        );
    }

    @Test
    public void testToStringSuppressMinusSignsWithinParensTrue() {
        this.toStringAndCheck(
            SpreadsheetPatternSpreadsheetFormatterNumberContext.with(
                false, // currency
                true, // suppressMinusSignsWithinParens
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.NOT_REQUIRED, "123", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.INCLUDE),
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.REQUIRED, "456", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.INCLUDE),
                SpreadsheetPatternSpreadsheetFormatterNumberDigits.integer(SpreadsheetPatternSpreadsheetFormatterNumberMinusSign.NOT_REQUIRED, "789", SpreadsheetPatternSpreadsheetFormatterNumberGroupSeparator.NONE),
                SpreadsheetPatternSpreadsheetFormatterNumber.with(
                    SpreadsheetFormatParserToken.number(
                        Lists.of(
                            SpreadsheetFormatParserToken.digit("1", "1")
                        ),
                        "1"
                    ),
                    false // suppressMinusSignsWithinParens
                ),
                SpreadsheetFormatterContexts.fake()),
            "suppressMinusSignsWithinParens 123-456789"
        );
    }

    @Override
    public Class<SpreadsheetPatternSpreadsheetFormatterNumberContext> type() {
        return SpreadsheetPatternSpreadsheetFormatterNumberContext.class;
    }
}
