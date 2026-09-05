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

package walkingkooka.spreadsheet.currency.provider;

import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.currency.CurrencyExchangeRater;
import walkingkooka.currency.CurrencyExchangeRaterContext;
import walkingkooka.currency.provider.CurrencyExchangeRaterInfo;
import walkingkooka.currency.provider.CurrencyExchangeRaterInfoSet;
import walkingkooka.currency.provider.CurrencyExchangeRaterName;
import walkingkooka.currency.provider.CurrencyExchangeRaterProvider;
import walkingkooka.currency.provider.CurrencyExchangeRaterSelector;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.UrlPath;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.props.Properties;
import walkingkooka.spreadsheet.currency.SpreadsheetCurrencyExchangeRaters;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link CurrencyExchangeRaterProvider} for {@link CurrencyExchangeRater} in {@link SpreadsheetCurrencyExchangeRaters}.
 */
final class SpreadsheetCurrencyCurrencyExchangeRaterProvider implements CurrencyExchangeRaterProvider,
    TreePrintable {

    final static AbsoluteUrl BASE_URL = Url.parseAbsolute(
        "https://github.com/mP1/walkingkooka-spreadsheet/" + CurrencyExchangeRater.class.getSimpleName()
    );

    /**
     * Factory
     */
    static SpreadsheetCurrencyCurrencyExchangeRaterProvider with(final Function<String, Number> numberParser) {
        return new SpreadsheetCurrencyCurrencyExchangeRaterProvider(
            Objects.requireNonNull(numberParser, "numberParser")
        );
    }

    private SpreadsheetCurrencyCurrencyExchangeRaterProvider(final Function<String, Number> numberParser) {
        super();
        this.numberParser = numberParser;
    }

    @Override
    public <C extends CurrencyExchangeRaterContext> CurrencyExchangeRater<C> currencyExchangeRater(final CurrencyExchangeRaterSelector selector,
                                                                                                   final ProviderContext context) {
        Objects.requireNonNull(selector, "selector");

        return selector.evaluateValueText(
            this,
            context
        );
    }

    @Override
    public <C extends CurrencyExchangeRaterContext> CurrencyExchangeRater<C> currencyExchangeRater(final CurrencyExchangeRaterName name,
                                                                                                   final List<?> values,
                                                                                                   final ProviderContext context) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(context, "context");

        final CurrencyExchangeRater<?> currencyExchangeRater;
        final List<?> copy = Lists.immutable(values);
        final String nameString = name.value();

        switch (nameString) {
            case PROPERTIES_STRING:
                parameterCountCheck(
                    copy,
                    1
                );

                currencyExchangeRater = SpreadsheetCurrencyExchangeRaters.properties(
                    context.convertOrFail(
                        copy.get(0),
                        Properties.class
                    ),
                    this.numberParser
                );
                break;
            case STORAGE_PATH_PROPERTIES_STRING:
                parameterCountCheck(
                    copy,
                    1
                );

                currencyExchangeRater = SpreadsheetCurrencyExchangeRaters.storagePathProperties(
                    context.convertOrFail(
                        copy.get(0),
                        StoragePath.class
                    ),
                    this.numberParser,
                    context // StorageContext
                );
                break;
            default:
                throw new IllegalArgumentException("Unknown " + CurrencyExchangeRater.class.getSimpleName() + " " + name);
        }

        return Cast.to(currencyExchangeRater);
    }

    private final Function<String, Number> numberParser;

    private static void noParameterCheck(final List<?> values) {
        parameterCountCheck(
            values,
            0
        );
    }

    private static void parameterCountCheck(final List<?> values,
                                            final int expected) {
        if (expected != values.size()) {
            throw new IllegalArgumentException("Expected " + expected + " values got " + values.size() + " " + values);
        }
    }

    private final static String PROPERTIES_STRING = "properties";

    final static CurrencyExchangeRaterName PROPERTIES = CurrencyExchangeRaterName.with(PROPERTIES_STRING);

    private final static String STORAGE_PATH_PROPERTIES_STRING = "storage-path-properties";

    final static CurrencyExchangeRaterName STORAGE_PATH_PROPERTIES = CurrencyExchangeRaterName.with(STORAGE_PATH_PROPERTIES_STRING);

    @Override
    public CurrencyExchangeRaterInfoSet currencyExchangeRaterInfos() {
        return INFOS;
    }

    // @see SpreadsheetCurrencyExchangeRaters constants
    final static CurrencyExchangeRaterInfoSet INFOS = CurrencyExchangeRaterInfoSet.with(
        Sets.of(
            currencyExchangeRaterInfo(PROPERTIES),
            currencyExchangeRaterInfo(STORAGE_PATH_PROPERTIES)
        )
    );

    /**
     * Helper that creates a {@link CurrencyExchangeRaterInfo} from the given {@link CurrencyExchangeRaterName} and {@link SpreadsheetCurrencyCurrencyExchangeRaterProvider#BASE_URL}.
     */
    private static CurrencyExchangeRaterInfo currencyExchangeRaterInfo(final CurrencyExchangeRaterName name) {
        return CurrencyExchangeRaterInfo.with(
            BASE_URL.appendPath(
                UrlPath.parse(
                    name.value()
                )
            ),
            name
        );
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        printer.println(this.getClass().getSimpleName());

        printer.indent();
        {
            TreePrintable.printTreeOrToString(
                this.currencyExchangeRaterInfos(),
                printer
            );
        }
        printer.outdent();
    }
}
