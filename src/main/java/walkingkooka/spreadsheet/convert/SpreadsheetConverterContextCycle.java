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

import walkingkooka.Either;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContextObjectPostProcessor;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContextPreProcessor;

import java.util.Objects;

/**
 * A {@link SpreadsheetConverterContextCycle} that may be used as a guard against recursive attempts to convert a value
 * and type combination.
 */
final class SpreadsheetConverterContextCycle implements SpreadsheetConverterContext,
    SpreadsheetConverterContextDelegator {

    static SpreadsheetConverterContextCycle with(final Object value,
                                                 final Class<?> type,
                                                 final SpreadsheetConverterContext context) {
        return new SpreadsheetConverterContextCycle(
            value,
            Objects.requireNonNull(type, "type"),
            Objects.requireNonNull(context, "context")
        );
    }

    private SpreadsheetConverterContextCycle(final Object value,
                                             final Class<?> type,
                                             final SpreadsheetConverterContext context) {
        super();

        this.value = value;
        this.type = type;
        this.context = context;
    }

    @Override
    public SpreadsheetConverterContext setObjectPostProcessor(final JsonNodeMarshallContextObjectPostProcessor processor) {
        Objects.requireNonNull(processor, "processor");
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetConverterContext setPreProcessor(JsonNodeUnmarshallContextPreProcessor processor) {
        Objects.requireNonNull(processor, "processor");
        throw new UnsupportedOperationException();
    }

    // ConverterLike....................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        // guard against trying same value & type combo AGAIN which would probably result in StackOverflowError
        return this.isRecursive(value, type) ?
            false :
            this.context.canConvert(value, type);
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.isRecursive(value, type) ?
            this.failConversion(
                value,
                type
            ) :
            this.context.convert(value, type);
    }

    private boolean isRecursive(final Object value,
                                final Class<?> type) {
        return Objects.equals(this.value, value) &&
            this.type.equals(type);
    }

    private final Object value;
    private final Class<?> type;

    // SpreadsheetConverterContextDelegator.............................................................................

    @Override
    public SpreadsheetConverterContext spreadsheetConverterContext() {
        return this.context;
    }

    private final SpreadsheetConverterContext context;

    // Object...........................................................................................................

    @Override
    public String toString() {
        return this.context.toString();
    }
}
