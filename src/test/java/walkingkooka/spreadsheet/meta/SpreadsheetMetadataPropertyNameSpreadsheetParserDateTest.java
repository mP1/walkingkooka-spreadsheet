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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;
import walkingkooka.spreadsheet.parser.provider.SpreadsheetParserSelector;

import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameSpreadsheetParserDateTest extends SpreadsheetMetadataPropertyNameSpreadsheetParserTestCase<SpreadsheetMetadataPropertyNameSpreadsheetParserDate> {

    @Test
    public void testExtractLocaleAwareValueUS() {
        this.extractLocaleValueAwareAndCheck(
            LocaleContexts.jre(Locale.ENGLISH),
            SpreadsheetDateParsePattern.parseDateParsePattern("dddd, mmmm d, yyyy;dddd, mmmm d, yy;dddd, mmmm d;mmmm d, yyyy;mmmm d, yy;mmmm d;mmm d, yyyy;mmm d, yy;mmm d;m/d/yy;m/d/yyyy;m/d")
                .spreadsheetParserSelector()
        );
    }

    @Test
    public void testExtractLocaleAwareValueAu() {
        this.extractLocaleValueAwareAndCheck(
            LocaleContexts.jre(Locale.forLanguageTag("EN-AU")),
            SpreadsheetDateParsePattern.parseDateParsePattern("dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m")
                .spreadsheetParserSelector()
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataPropertyNameSpreadsheetParserDate.instance(),
            "dateParser"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetParserDate createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParserDate.instance();
    }

    @Override
    SpreadsheetParserSelector propertyValue() {
        return SpreadsheetDateParsePattern.parseDateParsePattern("yyyy/mm/dd")
            .spreadsheetParserSelector();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetParserDate> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParserDate.class;
    }
}
