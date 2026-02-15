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
import walkingkooka.datetime.DateTimeSymbols;
import walkingkooka.math.DecimalNumberSymbols;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.expression.convert.FakeExpressionNumberConverterContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class FakeSpreadsheetConverterContext extends FakeExpressionNumberConverterContext implements SpreadsheetConverterContext {

    @Override
    public Optional<StoragePath> currentWorkingDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Converter<SpreadsheetConverterContext> converter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<StoragePath> homeDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetMetadata spreadsheetMetadata() {
        throw new UnsupportedOperationException();
    }
    @Override
    public StoragePath parseStoragePath(final String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetExpressionReference validationReference() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshall(final Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallEnumSet(final Set<? extends Enum<?>> set) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallWithType(final Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallOptional(final Optional<?> optional) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallOptionalWithType(final Optional<?> optional) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallCollection(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallMap(final Map<?, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallCollectionWithType(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonNode marshallMapWithType(final Map<?, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unmarshall(final JsonNode jsonNode,
                            final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Enum<T>> Set<T> unmarshallEnumSet(final JsonNode jsonNode,
                                                        final Class<T> type, Function<String, T> enumFactory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Optional<T> unmarshallOptional(final JsonNode jsonNode,
                                              final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Optional<T> unmarshallOptionalWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> unmarshallList(final JsonNode jsonNode,
                                      final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<T> unmarshallSet(final JsonNode JsonNode,
                                    final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> Map<K, V> unmarshallMap(final JsonNode jsonNode,
                                          final Class<K> keyType,
                                          final Class<V> valueType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unmarshallWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> unmarshallListWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<T> unmarshallSetWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> Map<K, V> unmarshallMapWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Class<?>> registeredType(final JsonString jsonString) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<JsonString> typeName(final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Currency> currencyForCurrencyCode(final String currencyCode) {
        throw new UnsupportedOperationException();
    }

    // LocaleContext....................................................................................................

    @Override
    public Set<Locale> availableLocales() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<DateTimeSymbols> dateTimeSymbolsForLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<DecimalNumberSymbols> decimalNumberSymbolsForLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Locale> findByLocaleText(final String text,
                                        final int offset,
                                        final int count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<String> localeText(final Locale locale,
                                       final Locale requestedLocale) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocale(final Locale locale) {
        throw new UnsupportedOperationException();
    }
}
