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

package walkingkooka.spreadsheet.convert;

import walkingkooka.convert.Converter;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.format.SpreadsheetFormatterProvider;
import walkingkooka.spreadsheet.format.SpreadsheetParserProvider;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;

/**
 * A {@link ConverterProvider} for {@link SpreadsheetConverters}.
 */
public final class SpreadsheetConvertersConverterProviders implements PublicStaticHelper {

    /**
     * This is the base {@link AbsoluteUrl} for all {@link Converter} in this package. The name of each
     * converter will be appended to this base.
     */
    public final static AbsoluteUrl BASE_URL = Url.parseAbsolute(
            "https://github.com/mP1/walkingkooka-spreadsheet/" + Converter.class.getSimpleName()
    );

    /**
     * {@see SpreadsheetConvertersConverterProvider}
     */
    public static ConverterProvider spreadsheetConverters(final SpreadsheetMetadata metadata,
                                                          final SpreadsheetFormatterProvider spreadsheetFormatterProvider,
                                                          final SpreadsheetParserProvider spreadsheetParserProvider) {
        return SpreadsheetConvertersConverterProvider.with(
                metadata,
                spreadsheetFormatterProvider,
                spreadsheetParserProvider
        );
    }

    /**
     * Stop creation
     */
    private SpreadsheetConvertersConverterProviders() {
        throw new UnsupportedOperationException();
    }
}
