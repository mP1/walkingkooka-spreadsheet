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

import walkingkooka.Either;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.format.provider.SpreadsheetFormatterSelectorToken;
import walkingkooka.text.CharSequences;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.text.TextNode;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A {@link SpreadsheetFormatter} that formats values by attempt to convert any given value to {@link String text},
 * using the provided {@link Converter}.
 */
final class SpreadsheetFormatterSharedConverter extends SpreadsheetFormatterShared {

    static SpreadsheetFormatterSharedConverter with(final Converter<ExpressionNumberConverterContext> converter) {
        Objects.requireNonNull(converter, "converter");
        return new SpreadsheetFormatterSharedConverter(converter);
    }

    private SpreadsheetFormatterSharedConverter(final Converter<ExpressionNumberConverterContext> converter) {
        super();
        this.converter = converter;
    }

    @Override
    public Optional<TextNode> format(final Optional<Object> value,
                                     final SpreadsheetFormatterContext context) {
        final Either<String, String> converted = this.converter.convert(
            value.orElse(null),
            String.class,
            context
        );
        return Optional.ofNullable(
            converted.isLeft() ?
                textNode(converted.leftValue()) :
                null
        );
    }

    private TextNode textNode(final String value) {
        return CharSequences.isNullOrEmpty(value) ?
            null :
            SpreadsheetText.with(value)
                .textNode();
    }

    @Override
    public List<SpreadsheetFormatterSelectorToken> tokens(final SpreadsheetFormatterContext context) {
        Objects.requireNonNull(context, "context");
        throw new UnsupportedOperationException();
    }

    private final Converter<ExpressionNumberConverterContext> converter;

    @Override
    public String toString() {
        return this.converter.toString();
    }
}
