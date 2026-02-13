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

import org.junit.jupiter.api.Test;
import walkingkooka.Cast;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2Test.TestSpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.storage.Storages;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class SpreadsheetEnvironmentContextTesting2Test implements SpreadsheetEnvironmentContextTesting2<TestSpreadsheetEnvironmentContext> {

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    @Test
    public void testServerUrl() {
        this.serverUrlAndCheck(
            new TestSpreadsheetEnvironmentContext(),
            SERVER_URL
        );
    }

    @Test
    public void testServerUrlAndEnvironmentValueNameMissingServerUrl() {
        new SpreadsheetEnvironmentContextTesting2<TestSpreadsheetEnvironmentContext2>() {

            @Override
            public TestSpreadsheetEnvironmentContext2 createContext() {
                return new TestSpreadsheetEnvironmentContext2();
            }

            @Override
            public Class<TestSpreadsheetEnvironmentContext2> type() {
                return null;
            }
        }.testServerUrlAndEnvironmentValueName();
    }

    final static class TestSpreadsheetEnvironmentContext2 extends FakeSpreadsheetEnvironmentContext {

        @Override
        public AbsoluteUrl serverUrl() {
            return this.environmentValueOrFail(SERVER_URL);
        }

        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            return Cast.to(
                Optional.ofNullable(
                    SERVER_URL.equals(name) ?
                        SpreadsheetEnvironmentContextTesting2Test.SERVER_URL :
                        null
                )
            );
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    @Test
    public void testSpreadsheetId() {
        this.spreadsheetIdAndCheck(
            new TestSpreadsheetEnvironmentContext(),
            SPREADSHEET_ID
        );
    }

    @Test
    public void testSpreadsheetIdAndEnvironmentValueNameMissingSpreadsheetId() {
        new SpreadsheetEnvironmentContextTesting2<TestSpreadsheetEnvironmentContext3>() {

            @Override
            public TestSpreadsheetEnvironmentContext3 createContext() {
                return new TestSpreadsheetEnvironmentContext3();
            }

            @Override
            public Class<TestSpreadsheetEnvironmentContext3> type() {
                return null;
            }
        }.testSpreadsheetIdAndEnvironmentValueName();
    }

    final static class TestSpreadsheetEnvironmentContext3 extends FakeSpreadsheetEnvironmentContext {

        @Override
        public Optional<SpreadsheetId> spreadsheetId() {
            return this.environmentValue(SPREADSHEET_ID);
        }

        @Override
        public <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
            return Cast.to(
                Optional.ofNullable(
                    SPREADSHEET_ID.equals(name) ?
                        SpreadsheetEnvironmentContextTesting2Test.SPREADSHEET_ID :
                        null
                )
            );
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }
    
    final static class TestSpreadsheetEnvironmentContext implements SpreadsheetEnvironmentContextDelegator {

        @Override
        public void setSpreadsheetId(final Optional<SpreadsheetId> spreadsheetId) {
            this.setOrRemoveEnvironmentValue(
                SPREADSHEET_ID,
                spreadsheetId
            );
        }

        @Override
        public SpreadsheetEnvironmentContext cloneEnvironment() {
            return new SpreadsheetEnvironmentContextDelegatorTest.TestSpreadsheetEnvironmentContextDelegator();
        }

        @Override
        public SpreadsheetEnvironmentContext setEnvironmentContext(final EnvironmentContext environmentContext) {
            Objects.requireNonNull(environmentContext, "environmentContext");

            return this == environmentContext ?
                this :
                new SpreadsheetEnvironmentContextDelegatorTest.TestSpreadsheetEnvironmentContextDelegator();
        }

        @Override
        public SpreadsheetEnvironmentContext spreadsheetEnvironmentContext() {
            return this.spreadsheetEnvironmentContext;
        }

        {
            final EnvironmentContext environmentContext = EnvironmentContexts.map(
                EnvironmentContexts.empty(
                    Currency.getInstance("AUD"),
                    Indentation.SPACES4,
                    LineEnding.NL,
                    Locale.ENGLISH,
                    () -> LocalDateTime.MIN,
                    EnvironmentContext.ANONYMOUS
                )
            );
            environmentContext.setEnvironmentValue(
                SpreadsheetEnvironmentContext.SERVER_URL,
                SpreadsheetEnvironmentContextTesting2Test.SERVER_URL
            );
            environmentContext.setEnvironmentValue(
                SpreadsheetEnvironmentContext.SPREADSHEET_ID,
                SpreadsheetEnvironmentContextTesting2Test.SPREADSHEET_ID
            );

            this.spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.basic(
                Storages.fake(),
                environmentContext
            );
        }

        private final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }
    }

    @Override
    public TestSpreadsheetEnvironmentContext createContext() {
        return new TestSpreadsheetEnvironmentContext();
    }

    @Override
    public Class<TestSpreadsheetEnvironmentContext> type() {
        return TestSpreadsheetEnvironmentContext.class;
    }
}
