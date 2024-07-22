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

package walkingkooka.spreadsheet.parser;

import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.text.cursor.parser.Parser;

import java.util.Set;

/**
 * A collection of constants and factory methods for {@link Parser}.
 */
public final class SpreadsheetParserProviders implements PublicStaticHelper {

    /**
     * This is the base {@link AbsoluteUrl} for all {@link Parser} in this package. The name of each
     * formatter will be appended to this base.
     */
    public final static AbsoluteUrl BASE_URL = Url.parseAbsolute(
            "https://github.com/mP1/walkingkooka-spreadsheet/" + Parser.class.getSimpleName()
    );

    /**
     * {@see SpreadsheetParserProviderCollection}
     */
    public static SpreadsheetParserProvider collection(final Set<SpreadsheetParserProvider> providers) {
        return SpreadsheetParserProviderCollection.with(providers);
    }

    /**
     * {@see EmptySpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider empty() {
        return EmptySpreadsheetParserProvider.INSTANCE;
    }

    /**
     * {@link FakeSpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider fake() {
        return new FakeSpreadsheetParserProvider();
    }

    /**
     * {@see MappedSpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider mapped(final Set<SpreadsheetParserInfo> infos,
                                                   final SpreadsheetParserProvider provider) {
        return MappedSpreadsheetParserProvider.with(
                infos,
                provider
        );
    }

    /**
     * {@see SpreadsheetParsePatternSpreadsheetParserProvider}
     */
    public static SpreadsheetParserProvider spreadsheetParsePattern(final SpreadsheetFormatterProvider spreadsheetFormatterProvider) {
        return SpreadsheetParsePatternSpreadsheetParserProvider.with(spreadsheetFormatterProvider);
    }

    /**
     * Stop creation
     */
    private SpreadsheetParserProviders() {
        throw new UnsupportedOperationException();
    }
}
