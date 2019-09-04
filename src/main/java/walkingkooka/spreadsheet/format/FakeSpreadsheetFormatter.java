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

import walkingkooka.test.Fake;

import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that always fails.
 */
public class FakeSpreadsheetFormatter implements SpreadsheetFormatter, Fake {

    protected FakeSpreadsheetFormatter() {
        super();
    }

    @Override
    public boolean canFormat(final Object value) throws SpreadsheetFormatException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetText> format(final Object value, final SpreadsheetFormatterContext context) throws SpreadsheetFormatException {
        throw new UnsupportedOperationException();
    }
}
