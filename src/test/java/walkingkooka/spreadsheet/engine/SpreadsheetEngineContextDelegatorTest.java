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

package walkingkooka.spreadsheet.engine;

import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetEngineContextDelegatorTest implements ClassTesting<SpreadsheetEngineContextDelegator> {

    final static class TestSpreadsheetEngineContextDelegator implements SpreadsheetEngineContextDelegator {
        @Override
        public SpreadsheetEngineContext spreadsheetEngineContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetEngineContext cloneEnvironment() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpreadsheetEngineContext setLocale(final Locale locale) {
            Objects.requireNonNull(locale, "locale");
            throw new UnsupportedOperationException();
        }
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetEngineContextDelegator> type() {
        return SpreadsheetEngineContextDelegator.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
