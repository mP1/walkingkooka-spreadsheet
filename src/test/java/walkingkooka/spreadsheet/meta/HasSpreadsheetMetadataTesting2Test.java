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

import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadataTesting2Test.TestHasSpreadsheetMetadata;

import java.util.Objects;
import java.util.Optional;

public final class HasSpreadsheetMetadataTesting2Test implements HasSpreadsheetMetadataTesting2<TestHasSpreadsheetMetadata> {

    @Override
    public TestHasSpreadsheetMetadata createHasSpreadsheetMetadata() {
        return new TestHasSpreadsheetMetadata();
    }

    @Override
    public Class<TestHasSpreadsheetMetadata> type() {
        return TestHasSpreadsheetMetadata.class;
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    final static class TestHasSpreadsheetMetadata extends FakeHasSpreadsheetMetadata {
        @Override
        public void setSpreadsheetMetadata(final Optional<SpreadsheetMetadata> spreadsheetMetadata) {
            Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");

            throw new UnsupportedOperationException();
        }
    }
}
