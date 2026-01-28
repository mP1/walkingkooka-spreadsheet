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
import walkingkooka.convert.Converter;
import walkingkooka.convert.ConverterContexts;
import walkingkooka.convert.Converters;
import walkingkooka.datetime.DateTimeContexts;
import walkingkooka.locale.LocaleContext;
import walkingkooka.locale.LocaleContexts;
import walkingkooka.math.DecimalNumberContext;
import walkingkooka.math.DecimalNumberContextDelegator;
import walkingkooka.math.DecimalNumberContexts;
import walkingkooka.spreadsheet.reference.SpreadsheetExpressionReference;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolver;
import walkingkooka.spreadsheet.reference.SpreadsheetLabelNameResolvers;
import walkingkooka.storage.StoragePath;
import walkingkooka.text.LineEnding;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContext;
import walkingkooka.tree.expression.convert.ExpressionNumberConverterContexts;
import walkingkooka.tree.json.convert.JsonNodeConverterContext;
import walkingkooka.tree.json.convert.JsonNodeConverterContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallUnmarshallContexts;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class BasicSpreadsheetConverterContextTest implements SpreadsheetConverterContextTesting<BasicSpreadsheetConverterContext>,
    DecimalNumberContextDelegator {

    private final static Optional<StoragePath> CURRENT_WORKING_DIRECTORY = Optional.of(
        StoragePath.parse("/current1/working2/directory3")
    );

    private final static ExpressionNumberKind KIND = ExpressionNumberKind.DEFAULT;

    private final static Optional<SpreadsheetExpressionReference> VALIDATION_REFERENCE = Optional.empty();

    private final static Converter<SpreadsheetConverterContext> CONVERTER = Converters.numberToNumber();

    private final static SpreadsheetLabelNameResolver LABEL_RESOLVER = SpreadsheetLabelNameResolvers.fake();

    private final static JsonNodeConverterContext JSON_NODE_CONVERTER_CONTEXT = JsonNodeConverterContexts.fake();

    private final static Locale LOCALE = Locale.forLanguageTag("EN-AU");

    private final static LocaleContext LOCALE_CONTEXT = LocaleContexts.jre(LOCALE);

    // with.............................................................................................................

    @Test
    public void testWithNullCurrentWorkingDirectoryFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                null,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                LABEL_RESOLVER,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                CURRENT_WORKING_DIRECTORY,
                null,
                VALIDATION_REFERENCE,
                CONVERTER,
                LABEL_RESOLVER,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullConverterFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                CURRENT_WORKING_DIRECTORY,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                null,
                LABEL_RESOLVER,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetLabelNameResolverFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                CURRENT_WORKING_DIRECTORY,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                null,
                JSON_NODE_CONVERTER_CONTEXT,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullJsonNodeConverterContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                CURRENT_WORKING_DIRECTORY,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                LABEL_RESOLVER,
                null,
                LOCALE_CONTEXT
            )
        );
    }

    @Test
    public void testWithNullLocaleContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> BasicSpreadsheetConverterContext.with(
                CURRENT_WORKING_DIRECTORY,
                SpreadsheetConverterContexts.NO_METADATA,
                VALIDATION_REFERENCE,
                CONVERTER,
                LABEL_RESOLVER,
                JSON_NODE_CONVERTER_CONTEXT,
                null
            )
        );
    }

    // currentWorkingDirectory..........................................................................................

    @Test
    public void testCurrentWorkingDirectory() {
        this.currentWorkingDirectoryAndCheck(
            this.createContext(),
            CURRENT_WORKING_DIRECTORY
        );
    }

    // convert..........................................................................................................

    @Test
    public void testConvert() {
        this.convertAndCheck(
            123,
            Float.class,
            123f
        );
    }

    // locale...........................................................................................................

    @Test
    public void testLocale() {
        this.localeAndCheck(
            this.createContext(),
            LOCALE_CONTEXT.locale()
        );
    }

    @Override
    public BasicSpreadsheetConverterContext createContext() {
        return BasicSpreadsheetConverterContext.with(
            CURRENT_WORKING_DIRECTORY,
            SpreadsheetConverterContexts.NO_METADATA,
            VALIDATION_REFERENCE,
            CONVERTER,
            LABEL_RESOLVER,
            this.jsonNodeConverterContext(),
            LOCALE_CONTEXT
        );
    }

    private JsonNodeConverterContext jsonNodeConverterContext() {
        return JsonNodeConverterContexts.basic(
            ExpressionNumberConverterContexts.basic(
                Converters.fake(),
                ConverterContexts.basic(
                    false, // canNumbersHaveGroupSeparator
                    Converters.JAVA_EPOCH_OFFSET, // dateOffset
                    INDENTATION,
                    LineEnding.NL,
                    ',', // valueSeparator
                    Converters.fake(),
                    DateTimeContexts.basic(
                        LOCALE_CONTEXT.dateTimeSymbolsForLocale(LOCALE)
                            .get(),
                        LOCALE_CONTEXT.locale(),
                        1900,
                        20,
                        LocalDateTime::now
                    ),
                    this.decimalNumberContext()
                ),
                KIND
            ),
            JsonNodeMarshallUnmarshallContexts.basic(
                JsonNodeMarshallContexts.basic(),
                JsonNodeUnmarshallContexts.basic(
                    KIND,
                    this.decimalNumberContext()
                        .mathContext()
                )
            )
        );
    }

    @Override
    public int decimalNumberDigitCount() {
        return this.decimalNumberContext()
            .decimalNumberDigitCount();
    }

    @Override
    public MathContext mathContext() {
        return this.decimalNumberContext()
            .mathContext();
    }

    // DecimalNumberContextDelegator....................................................................................

    @Override
    public DecimalNumberContext decimalNumberContext() {
        return DecimalNumberContexts.american(MathContext.DECIMAL32);
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        final ExpressionNumberConverterContext converterContext = ExpressionNumberConverterContexts.fake();
        final JsonNodeMarshallUnmarshallContext marshallUnmarshallContext = JsonNodeMarshallUnmarshallContexts.fake();

        this.toStringAndCheck(
            JsonNodeConverterContexts.basic(
                converterContext,
                marshallUnmarshallContext
            ),
            converterContext +
                " " +
                marshallUnmarshallContext
        );
    }

    // class............................................................................................................

    @Override
    public Class<BasicSpreadsheetConverterContext> type() {
        return BasicSpreadsheetConverterContext.class;
    }
}
