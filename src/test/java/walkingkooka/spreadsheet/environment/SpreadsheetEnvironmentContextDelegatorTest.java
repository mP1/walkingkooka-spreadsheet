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

package walkingkooka.spreadsheet.environment;

import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextDelegatorTest.TestSpreadsheetEnvironmentContextDelegator;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.storage.Storages;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetEnvironmentContextDelegatorTest implements SpreadsheetEnvironmentContextTesting2<TestSpreadsheetEnvironmentContextDelegator> {

    @Override
    public void testSetIndentationWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLineEndingWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetLocaleWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void testSetUserWithDifferentAndWatcher() {
        throw new UnsupportedOperationException();
    }

    /**/
    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TestSpreadsheetEnvironmentContextDelegator createContext() {
        return new TestSpreadsheetEnvironmentContextDelegator();
    }

    @Override
    public Class<TestSpreadsheetEnvironmentContextDelegator> type() {
        return TestSpreadsheetEnvironmentContextDelegator.class;
    }

    final static class TestSpreadsheetEnvironmentContextDelegator implements SpreadsheetEnvironmentContextDelegator {

        @Override
        public void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
            this.setOrRemoveEnvironmentValue(
                SPREADSHEET_ID,
                spreadsheetId
            );
        }

        @Override
        public SpreadsheetEnvironmentContext cloneEnvironment() {
            return new TestSpreadsheetEnvironmentContextDelegator();
        }

        @Override
        public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return this == environmentContext ?
                this :
                new TestSpreadsheetEnvironmentContextDelegator();
        }

        @Override
        public SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
            final EnvironmentContext environmentContext = EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    Indentation.SPACES4,
                    LineEnding.NL,
                    Locale.ENGLISH,
                    () -> LocalDateTime.MIN,
                    EnvironmentContext.ANONYMOUS
                )
            );
            environmentContext.setEnvironmentValue(
                SpreadsheetEnvironmentContext.SERVER_URL,
                Url.parseAbsolute("https://example.com")
            );
            environmentContext.setEnvironmentValue(
                SpreadsheetEnvironmentContext.SPREADSHEET_ID,
                SpreadsheetId.with(1)
            );

            return SpreadsheetEnvironmentContexts.basic(
                Storages.fake(),
                environmentContext
            );
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
}
