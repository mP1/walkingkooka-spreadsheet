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
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2Test.TestSpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.storage.Storages;
import walkingkooka.text.LineEnding;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

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
    public void testSpreadsheetId() {
        this.spreadsheetIdAndCheck(
            new TestSpreadsheetEnvironmentContext(),
            SPREADSHEET_ID
        );
    }


    final static class TestSpreadsheetEnvironmentContext implements SpreadsheetEnvironmentContextDelegator {

        @Override
        public void setSpreadsheetId(final SpreadsheetId spreadsheetId) {
            this.setEnvironmentValue(
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

        private SpreadsheetEnvironmentContext spreadsheetEnvironmentContext;

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
