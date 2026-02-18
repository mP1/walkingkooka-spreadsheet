
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

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.convert.Converter;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonString;
import walkingkooka.tree.json.convert.JsonNodeConverters;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetConverterTextToSpreadsheetMetadataTest extends SpreadsheetConverterTestCase<SpreadsheetConverterTextToSpreadsheetMetadata> {

    private final static JsonNodeMarshallContext MARSHALL_CONTEXT = JsonNodeMarshallContexts.basic();

    @Test
    public void testConvertStringToSpreadsheetMetadata() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY;

        this.convertAndCheck(
            MARSHALL_CONTEXT.marshall(metadata)
                .toString(),
            SpreadsheetMetadata.class,
            metadata
        );
    }

    @Test
    public void testConvertStringToSpreadsheetMetadata2() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY;

        this.convertAndCheck(
            MARSHALL_CONTEXT.marshall(metadata)
                .toString(),
            metadata
        );
    }

    @Test
    public void testConvertStringToSpreadsheetMetadata3() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetName.with("Spreadsheet123")
        ).set(
            SpreadsheetMetadataPropertyName.ROUNDING_MODE,
            RoundingMode.CEILING
        );

        this.convertAndCheck(
            MARSHALL_CONTEXT.marshall(metadata)
                .toString(),
            metadata
        );
    }

    @Test
    public void testConvertCharSequenceToSpreadsheetMetadata2() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
            SpreadsheetName.with("Spreadsheet123")
        ).set(
            SpreadsheetMetadataPropertyName.ROUNDING_MODE,
            RoundingMode.CEILING
        );

        this.convertAndCheck(
            new StringBuilder(
                MARSHALL_CONTEXT.marshall(metadata)
                    .toString()
            ),
            metadata
        );
    }

    @Override
    public SpreadsheetConverterTextToSpreadsheetMetadata createConverter() {
        return SpreadsheetConverterTextToSpreadsheetMetadata.INSTANCE;
    }

    @Override
    public SpreadsheetConverterContext createContext() {
        return new FakeSpreadsheetConverterContext() {
            @Override
            public boolean canConvert(final Object value,
                                      final Class<?> type) {
                return converter.canConvert(
                    value,
                    type,
                    this
                );
            }

            @Override
            public <T> Either<T, String> convert(final Object value,
                                                 final Class<T> target) {
                return this.converter.convert(
                    value,
                    target,
                    this
                );
            }

            private final Converter<SpreadsheetConverterContext> converter = SpreadsheetConverters.collection(
                Lists.of(
                    SpreadsheetConverters.textToText(),
                    JsonNodeConverters.textToJsonNode(), // parses String -> JsonNode
                    JsonNodeConverters.jsonNodeTo() // unmarshall JsonNode -> SpreadsheetMetadata
                )
            );

            private final JsonNodeUnmarshallContext unmarshallContext = JsonNodeUnmarshallContexts.basic(
                (String cc) -> Optional.ofNullable(
                    Currency.getInstance(cc)
                ),
                (String lt) -> Optional.of(
                    Locale.forLanguageTag(lt)
                ),
                ExpressionNumberKind.BIG_DECIMAL,
                MathContext.UNLIMITED
            );

            @Override
            public <T> T unmarshall(final JsonNode jsonNode,
                                    final Class<T> type) {
                return this.unmarshallContext.unmarshall(
                    jsonNode,
                    type
                );
            }

            @Override
            public Optional<JsonString> typeName(final Class<?> type) {
                return this.unmarshallContext.typeName(type);
            }
        };
    }

    // class............................................................................................................

    @Override
    public Class<SpreadsheetConverterTextToSpreadsheetMetadata> type() {
        return SpreadsheetConverterTextToSpreadsheetMetadata.class;
    }
}
