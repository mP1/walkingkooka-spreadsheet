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

import java.util.Locale;


public final class SpreadsheetMetadataPropertyNameFindQueryTest extends SpreadsheetMetadataPropertyNameTestCase<
        SpreadsheetMetadataPropertyNameFindQuery,
        SpreadsheetCellQuery> {

    @Test
    public void testCheckValueWithInvalidFails3() {
        this.checkValueFails(
                "invalid",
                "Expected SpreadsheetCellQuery, but got \"invalid\" for \"find-query\""
        );
    }

    @Test
    public void testExtractLocaleAwareValue() {
        this.extractLocaleValueAwareAndCheck(Locale.ENGLISH, null);
    }

    @Test
    public void testToString() {
        this.toStringAndCheck(
                SpreadsheetMetadataPropertyNameFindQuery.instance(),
                "find-query"
        );
    }

    @Override
    SpreadsheetMetadataPropertyNameFindQuery createName() {
        return SpreadsheetMetadataPropertyNameFindQuery.instance();
    }

    @Override
    SpreadsheetCellQuery propertyValue() {
        return SpreadsheetCellQuery.parse("1+2+3");
    }

    @Override
    String propertyValueType() {
        return SpreadsheetCellQuery.class.getSimpleName();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataPropertyNameFindQuery> type() {
        return SpreadsheetMetadataPropertyNameFindQuery.class;
    }
}
