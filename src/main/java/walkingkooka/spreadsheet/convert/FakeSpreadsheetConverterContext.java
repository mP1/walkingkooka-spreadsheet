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

import walkingkooka.Binary;
import walkingkooka.color.Color;
import walkingkooka.color.IndexedColor;
import walkingkooka.color.NamedColor;
import walkingkooka.convert.Converter;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.logging.LoggerPath;
import walkingkooka.logging.LoggingLevel;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.net.header.MediaType;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelName;
import walkingkooka.spreadsheet.reference.SpreadsheetSelection;
import walkingkooka.spreadsheet.validation.SpreadsheetValidationReference;
import walkingkooka.storage.StoragePath;
import walkingkooka.tree.expression.convert.FakeExpressionNumberConverterContext;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class FakeSpreadsheetConverterContext extends FakeExpressionNumberConverterContext implements SpreadsheetConverterContext {

    @Override
    public MediaType detect(final String filename,
                            final Binary content) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnvironmentValueName<?> parseEnvironmentValueName(final String name) {
        throw new UnsupportedOperationException();
    }

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
    public Optional<EmailAddress> user() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetMetadata> spreadsheetMetadata() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetSelection> resolveLabel(final SpreadsheetLabelName labelName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetValidationReference validationReference() {
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
    public Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpreadsheetMetadata> loadMetadata(final SpreadsheetId id) {
        throw new UnsupportedOperationException();
    }

    // ColorContext.....................................................................................................

    @Override
    public IndexedColor indexedColor(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Color> lookupColor(final Color color) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NamedColor namedColor(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Color parseColor(final String color) {
        throw new UnsupportedOperationException();
    }

    // CanLog...........................................................................................................

    @Override
    public void logEnter(final LoggerPath logger) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logExit() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void log(final LoggingLevel loggingLevel,
                    final String message,
                    final Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLoggingEnabled(final LoggingLevel loggingLevel) {
        throw new UnsupportedOperationException();
    }

    // LoggingContext...................................................................................................

    @Override
    public void debug(final String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void debug(final String message,
                      final Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(final String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void info(final String message,
                     final Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(final String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void warn(final String message,
                     final Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(final String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void error(final String message,
                      final Throwable throwable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDebugEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInfoEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWarnEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isErrorEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNoneEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LoggingLevel loggingLevel() {
        throw new UnsupportedOperationException();
    }
}
