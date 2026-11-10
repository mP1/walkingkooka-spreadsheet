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
    public SpreadsheetConverterContext setPreProcessor(final JsonNodeUnmarshallContextPreProcessor processor) {
        Objects.requireNonNull(processor, "processor");
        throw new UnsupportedOperationException();
    }

    // ConverterLike....................................................................................................

    @Override
    public boolean canConvert(final Object value,
                              final Class<?> type) {
        return this.isCycle(value, type) ?
            false :
            this.context.converter()
                .canConvert(
                    value,
                    type,
                    this.createSpreadsheetConverterContextCycle(
                        value,
                        type
                    )
                );
    }

    @Override
    public <T> Either<T, String> convert(final Object value,
                                         final Class<T> type) {
        return this.isCycle(value, type) ?
            this.failConversion(
                value,
                type
            ) :
            this.context.converter()
                .convert(
                    value,
                    type,
                    this.createSpreadsheetConverterContextCycle(
                        value,
                        type
                    )
                );
    }

    /**
     * Check if the given value and {@link Class type} combination has been seen before and a cycle must be active.
     */
    private boolean isCycle(final Object value,
                            final Class<?> type) {

        boolean cycle = Objects.equals(
            this.value,
            value
        ) &&
            this.type.equals(type);

        if (false == cycle) {
            final SpreadsheetConverterContext context = this.context;
            if (context instanceof SpreadsheetConverterContextCycle) {
                cycle = ((SpreadsheetConverterContextCycle) context).isCycle(
                    value,
                    type
                );
            }
        }

        return cycle;
    }

    /**
     * The current nullable value in this context
     */
    private final Object value;

    /**
     * The accompanying type.
     */
    private final Class<?> type;

    private SpreadsheetConverterContextCycle createSpreadsheetConverterContextCycle(final Object value,
                                                                                    final Class<?> type) {
        return new SpreadsheetConverterContextCycle(
            value,
            type,
            this
        );
    }

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
