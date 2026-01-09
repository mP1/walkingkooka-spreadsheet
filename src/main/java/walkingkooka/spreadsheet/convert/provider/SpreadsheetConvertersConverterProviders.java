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

package walkingkooka.spreadsheet.convert.provider;

import walkingkooka.convert.Converter;
import walkingkooka.convert.provider.ConverterAliasSet;
import walkingkooka.convert.provider.ConverterInfoSet;
import walkingkooka.convert.provider.ConverterProvider;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.reflect.PublicStaticHelper;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverters;

import java.util.function.Function;

/**
 * A {@link ConverterProvider} for {@link SpreadsheetConverters}.
 */
public final class SpreadsheetConvertersConverterProviders implements PublicStaticHelper {

    /**
     * This is the base {@link AbsoluteUrl} for all {@link Converter} in this package. The name of each
     * converter will be appended to this base.
     */
    public final static AbsoluteUrl BASE_URL = SpreadsheetConvertersConverterProvider.BASE_URL;

    /**
     * Convenient {@link ConverterInfoSet} containing all {@link Converter}.
     */
    public final static ConverterInfoSet ALL = SpreadsheetConvertersConverterProvider.INFOS;

    public final static ConverterAliasSet FIND = ALL.aliasSet()
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.COLOR_TO_COLOR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.COLOR_TO_NUMBER)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.NUMBER_TO_COLOR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE_VALUE_INFO_LIST_TO_TEXT)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_STORAGE_PATH)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_FORM_NAME)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TO_STYLEABLE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.URL_TO_HYPERLINK)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.URL_TO_IMAGE);

    public final static ConverterAliasSet FORMATTING = ALL.aliasSet()
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE_VALUE_INFO_LIST_TO_TEXT)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_FORM_NAME)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_VALIDATION_ERROR);

    public final static ConverterAliasSet FORMULA = ALL.aliasSet()
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.COLOR_TO_COLOR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.COLOR_TO_NUMBER)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.NUMBER_TO_COLOR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE_VALUE_INFO_LIST_TO_TEXT)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_FORM_NAME)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_STORAGE_PATH)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TO_STYLEABLE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.URL_TO_HYPERLINK)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.URL_TO_IMAGE);

    public final static ConverterAliasSet SCRIPTING = ALL.aliasSet();

    public final static ConverterAliasSet SORT = ALL.aliasSet()
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.COLOR_TO_COLOR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.COLOR_TO_NUMBER)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.HAS_STYLE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.HAS_TEXT_NODE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.NUMBER_TO_COLOR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE_VALUE_INFO_LIST_TO_TEXT)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_ERROR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_FORM_NAME)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_STORAGE_PATH)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_TEXT_STYLE_PROPERTY_NAME)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_VALIDATION_ERROR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TO_STYLEABLE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.URL_TO_HYPERLINK)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.URL_TO_IMAGE);

    public final static ConverterAliasSet VALIDATOR = ALL.aliasSet()
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.COLOR_TO_COLOR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.COLOR_TO_NUMBER)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.HAS_STYLE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.HAS_TEXT_NODE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.NUMBER_TO_COLOR)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.STORAGE_VALUE_INFO_LIST_TO_TEXT)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_STORAGE_PATH)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TEXT_TO_TEXT_STYLE_PROPERTY_NAME)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.TO_STYLEABLE)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.URL_TO_HYPERLINK)
        .deleteAliasOrName(SpreadsheetConvertersConverterProvider.URL_TO_IMAGE);

    /**
     * {@see SpreadsheetConvertersConverterProvider}
     */
    public static ConverterProvider spreadsheetConverters(final Function<ProviderContext, Converter<SpreadsheetConverterContext>> dateTime) {
        return SpreadsheetConvertersConverterProvider.with(dateTime);
    }

    /**
     * Stop creation
     */
    private SpreadsheetConvertersConverterProviders() {
        throw new UnsupportedOperationException();
    }
}
