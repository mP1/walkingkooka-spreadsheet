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

import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadataDelegatorTest.TestHasSpreadsheetMetadataDelegator;

public final class HasSpreadsheetMetadataDelegatorTest implements HasSpreadsheetMetadataTesting2<TestHasSpreadsheetMetadataDelegator> {

    @Override
    public TestHasSpreadsheetMetadataDelegator createHasSpreadsheetMetadata() {
        return new TestHasSpreadsheetMetadataDelegator();
    }

    @Override
    public Class<TestHasSpreadsheetMetadataDelegator> type() {
        return TestHasSpreadsheetMetadataDelegator.class;
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    final static class TestHasSpreadsheetMetadataDelegator implements HasSpreadsheetMetadataDelegator {

        @Override
        public HasSpreadsheetMetadata hasSpreadsheetMetadata() {
            return SPREADSHEET_METADATA;
        }
    }
}
