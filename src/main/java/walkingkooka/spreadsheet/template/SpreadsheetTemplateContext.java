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

package walkingkooka.spreadsheet.template;

import walkingkooka.InvalidCharacterException;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.spreadsheet.SpreadsheetCell;
import walkingkooka.spreadsheet.engine.SpreadsheetDelta;
import walkingkooka.spreadsheet.expression.SpreadsheetExpressionEvaluationContext;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.parser.SpreadsheetParserContext;
import walkingkooka.spreadsheet.reference.SpreadsheetColumnReference;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetRowReference;
import walkingkooka.spreadsheet.validation.SpreadsheetValidatorContext;
import walkingkooka.template.TemplateValueName;
import walkingkooka.text.cursor.TextCursor;
import walkingkooka.text.cursor.parser.Parser;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.JsonPropertyName;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;
import walkingkooka.validation.ValidationError;
import walkingkooka.validation.form.Form;
import walkingkooka.validation.form.FormField;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A Context that leverages Spreadsheet components to parse and render templates.
 */
public interface SpreadsheetTemplateContext extends SpreadsheetParserContext,
        SpreadsheetExpressionEvaluationContext {

    /**
     * {@see TemplateContext#templateValue}
     */
    String templateValue(final TemplateValueName name);

    @Override
    default SpreadsheetTemplateContext setCell(final Optional<SpreadsheetCell> cell) {
        throw new UnsupportedOperationException();
    }

    @Override
    default InvalidCharacterException invalidCharacterException(final Parser<?> parser,
                                                                final TextCursor cursor) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<SpreadsheetColumnReference> nextEmptyColumn(final SpreadsheetRowReference row) {
        Objects.requireNonNull(row, "row");

        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<SpreadsheetRowReference> nextEmptyRow(final SpreadsheetColumnReference column) {
        Objects.requireNonNull(column, "column");

        throw new UnsupportedOperationException();
    }

    @Override
    default void setSpreadsheetMetadata(final SpreadsheetMetadata metadata) {
        Objects.requireNonNull(metadata, "metadata");
        throw new UnsupportedOperationException();
    }

    // Validation.......................................................................................................

    /**
     * A template never needs the {@link SpreadsheetExpressionReference}.
     */
    @Override
    default SpreadsheetExpressionReference validationReference() {
        throw new UnsupportedOperationException();
    }

    // ValidationExpressionEvaluationContext............................................................................

    @Override
    default Optional<Object> validationValue() {
        throw new UnsupportedOperationException();
    }

    // EnvironmentContext...............................................................................................

    @Override
    default <T> Optional<T> environmentValue(final EnvironmentValueName<T> name) {
        Objects.requireNonNull(name, "name");

        throw new UnsupportedOperationException();
    }

    @Override
    default Set<EnvironmentValueName<?>> environmentValueNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<EmailAddress> user() {
        throw new UnsupportedOperationException();
    }

    // HasForm..........................................................................................................

    @Override
    default Form<SpreadsheetExpressionReference> form() {
        throw new UnsupportedOperationException();
    }

    // FormHandlerContext...............................................................................................

    @Override
    default Comparator<SpreadsheetExpressionReference> formFieldReferenceComparator() {
        throw new UnsupportedOperationException();
    }

    @Override
    default SpreadsheetValidatorContext validatorContext(final SpreadsheetExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<Object> loadFormFieldValue(final SpreadsheetExpressionReference reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    default SpreadsheetDelta saveFormFieldValues(final List<FormField<SpreadsheetExpressionReference>> fields) {
        throw new UnsupportedOperationException();
    }

    @Override
    default List<ValidationError<SpreadsheetExpressionReference>> validateFormFields(final List<FormField<SpreadsheetExpressionReference>> fields) {
        throw new UnsupportedOperationException();
    }

    // JsonNodeMarshallContext..........................................................................................

    @Override
    default SpreadsheetTemplateContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    default JsonNode marshall(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    default JsonNode marshallEnumSet(Set<? extends Enum<?>> set) {
        throw new UnsupportedOperationException();
    }

    @Override
    default JsonNode marshallWithType(final Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    default JsonNode marshallOptional(final Optional<?> optional) {
        throw new UnsupportedOperationException();
    }

    @Override
    default JsonNode marshallOptionalWithType(final Optional<?> optional) {
        throw new UnsupportedOperationException();
    }

    @Override
    default JsonNode marshallCollection(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    default JsonNode marshallMap(final Map<?, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    default JsonNode marshallCollectionWithType(final Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    default JsonNode marshallMapWithType(final Map<?, ?> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    default SpreadsheetTemplateContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> T unmarshall(final JsonNode jsonNode,
                             final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T extends Enum<T>> Set<T> unmarshallEnumSet(final JsonNode jsonNode,
                                                         final Class<T> type,
                                                         final Function<String, T> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> Optional<T> unmarshallOptional(final JsonNode jsonNode,
                                               final Class<T> type) {
        return Optional.empty();
    }

    @Override
    default <T> Optional<T> unmarshallOptionalWithType(final JsonNode jsonNode) {
        return Optional.empty();
    }

    @Override
    default <T> List<T> unmarshallList(final JsonNode jsonNode,
                                       final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> Set<T> unmarshallSet(final JsonNode jsonNode,
                                     final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <K, V> Map<K, V> unmarshallMap(final JsonNode jsonNode,
                                           final Class<K> keyType,
                                           final Class<V> valueType) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> T unmarshallWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> List<T> unmarshallListWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> Set<T> unmarshallSetWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <K, V> Map<K, V> unmarshallMapWithType(final JsonNode jsonNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    default <T> BiFunction<JsonNode, JsonNodeUnmarshallContext, T> unmarshallWithType(final JsonPropertyName property,
                                                                                      final JsonObject propertySource,
                                                                                      final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<Class<?>> registeredType(final JsonString jsonString) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Optional<JsonString> typeName(final Class<?> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isSupportedJsonType(final Class<?> type) {
        throw new UnsupportedOperationException();
    }
}
