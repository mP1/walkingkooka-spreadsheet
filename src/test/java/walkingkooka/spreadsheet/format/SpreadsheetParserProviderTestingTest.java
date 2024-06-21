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

import org.junit.jupiter.api.Test;
import walkingkooka.collect.set.Sets;
import walkingkooka.net.Url;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.text.cursor.parser.Parsers;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class SpreadsheetParserProviderTestingTest implements SpreadsheetParserProviderTesting<SpreadsheetParserProviderTestingTest.TestSpreadsheetParserProvider> {

    private final static String SELECTOR = "date-parse-pattern dd/mm/yyyy";

    private final static Parser<SpreadsheetParserContext> PARSER = Parsers.fake();

    private final static SpreadsheetParserInfo INFO = SpreadsheetParserInfo.with(
            Url.parseAbsolute("https://example.com/123"),
            SpreadsheetParserName.DATE_PARSER
    );

    @Test
    public void testSpreadsheetParserAndCheck() {
        this.spreadsheetParserAndCheck(
                SELECTOR,
                PARSER
        );
    }

    @Test
    public void testSpreadsheetParserInfosAndCheck() {
        this.spreadsheetParserInfosAndCheck(
                new TestSpreadsheetParserProvider(),
                INFO
        );
    }

    @Override
    public TestSpreadsheetParserProvider createSpreadsheetParserProvider() {
        return new TestSpreadsheetParserProvider();
    }

    class TestSpreadsheetParserProvider implements SpreadsheetParserProvider {
        @Override
        public Optional<Parser<SpreadsheetParserContext>> spreadsheetParser(final SpreadsheetParserSelector selector) {
            Objects.requireNonNull(selector, "selector");

            checkEquals("date-parse-pattern", selector.name().value());
            return Optional.of(PARSER);
        }

        @Override
        public Set<SpreadsheetParserInfo> spreadsheetParserInfos() {
            return Sets.of(INFO);
        }
    }

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    // ClassTesting.....................................................................................................

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public Class<TestSpreadsheetParserProvider> type() {
        return TestSpreadsheetParserProvider.class;
    }
}
