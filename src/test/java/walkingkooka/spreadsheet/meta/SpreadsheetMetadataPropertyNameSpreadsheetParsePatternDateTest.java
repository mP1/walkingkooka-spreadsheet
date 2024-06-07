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
import walkingkooka.spreadsheet.format.pattern.SpreadsheetDateParsePattern;

import java.util.Locale;

public final class SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDateTest extends SpreadsheetMetadataPropertyNameTestCase<SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate, SpreadsheetDateParsePattern> {

    @Test
    public void testExtractLocaleValueUS() {
        this.extractLocaleValueAndCheck(
                Locale.ENGLISH,
                SpreadsheetDateParsePattern.parseDateParsePattern("dddd, mmmm d, yyyy;dddd, mmmm d, yy;dddd, mmmm d;mmmm d, yyyy;mmmm d, yy;mmmm d;mmm d, yyyy;mmm d, yy;mmm d;m/d/yy;m/d/yyyy;m/d")
        );
    }

    @Test
    public void testExtractLocaleValueAu() {
        this.extractLocaleValueAndCheck(
                Locale.forLanguageTag("EN-AU"),
                SpreadsheetDateParsePattern.parseDateParsePattern("dddd, d mmmm yyyy;dddd, d mmmm yy;dddd, d mmmm;d mmmm yyyy;d mmmm yy;d mmmm;d mmm yyyy;d mmm yy;d mmm;d/m/yy;d/m/yyyy;d/m")
        );
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate.instance(), "date-parse-pattern");
    }

    @Override
    SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate createName() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate.instance();
    }

    @Override
    SpreadsheetDateParsePattern propertyValue() {
        return SpreadsheetDateParsePattern.parseDateParsePattern("dd mm yyyy \"custom\"");
    }

    @Override
    String propertyValueType() {
        return "Date parse pattern";
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate> type() {
        return SpreadsheetMetadataPropertyNameSpreadsheetParsePatternDate.class;
    }
}
