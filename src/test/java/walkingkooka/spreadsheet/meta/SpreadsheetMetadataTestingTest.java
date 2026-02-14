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

package walkingkooka.spreadsheet.meta;

import org.junit.jupiter.api.Test;
import walkingkooka.Either;
import walkingkooka.collect.list.Lists;
import walkingkooka.collect.set.Sets;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.convert.Converter;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.environment.ReadOnlyEnvironmentValueException;
import walkingkooka.locale.LocaleContextTesting;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.plugin.ProviderContext;
import walkingkooka.spreadsheet.convert.SpreadsheetConverterContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting;
import walkingkooka.spreadsheet.formula.SpreadsheetFormula;
import walkingkooka.spreadsheet.formula.parser.SpreadsheetFormulaParserToken;
import walkingkooka.storage.HasUserDirectorieses;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;
import walkingkooka.text.printer.TreePrintableTesting;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataTestingTest implements SpreadsheetMetadataTesting,
    LocaleContextTesting,
    SpreadsheetEnvironmentContextTesting,
    TreePrintableTesting {

    @Test
    public void testCurrencyContextReadOnly() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> SpreadsheetMetadataTesting.CURRENCY_CONTEXT.setCurrency(
                Currency.getInstance("NZD")
            )
        );
    }

    @Test
    public void testEffectiveStyle() {
        METADATA_EN_AU.effectiveStyle();
    }

    @Test
    public void testEnvironmentContextSetEnvironmentValueFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> SPREADSHEET_ENVIRONMENT_CONTEXT.setEnvironmentValue(
                EnvironmentValueName.with(
                    "Hello",
                    String.class
                ),
                "World"
            )
        );
    }

    @Test
    public void testEnvironmentContextRemoveEnvironmentValueFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> SPREADSHEET_ENVIRONMENT_CONTEXT.removeEnvironmentValue(
                SpreadsheetEnvironmentContext.LINE_ENDING
            )
        );
    }

    @Test
    public void testEnvironmentContextConvertAllValuesToString() {
        final Converter<SpreadsheetConverterContext> converter = METADATA_EN_AU.converter(
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER,
            CONVERTER_PROVIDER,
            PROVIDER_CONTEXT
        );

        final SpreadsheetConverterContext converterContext = METADATA_EN_AU.spreadsheetConverterContext(
            SpreadsheetMetadata.NO_CELL,
            SpreadsheetMetadata.NO_VALIDATION_REFERENCE,
            SpreadsheetMetadataPropertyName.SCRIPTING_CONVERTER,
            HasUserDirectorieses.empty(), // no current working directory
            INDENTATION,
            SPREADSHEET_LABEL_NAME_RESOLVER,
            LINE_ENDING,
            CONVERTER_PROVIDER,
            LOCALE_CONTEXT,
            PROVIDER_CONTEXT
        );

        final Set<EnvironmentValueName<?>> names = SortedSets.tree();

        for (final EnvironmentValueName<?> name : SPREADSHEET_ENVIRONMENT_CONTEXT.environmentValueNames()) {
            final Either<?, String> result = converter.convert(
                SPREADSHEET_ENVIRONMENT_CONTEXT.environmentValueOrFail(name),
                String.class,
                converterContext
            );
            if (result.isRight()) {
                names.add(name);
            }
        }

        this.checkEquals(
            Sets.empty(),
            names,
            SPREADSHEET_ENVIRONMENT_CONTEXT::toString
        );
    }

    @Test
    public void testFormatter() {
        METADATA_EN_AU.spreadsheetFormatter(
            SPREADSHEET_FORMATTER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testJsonNodeMarshallContext() {
        METADATA_EN_AU.jsonNodeMarshallContext();
    }

    @Test
    public void testJsonNodeUnmarshallContext() {
        METADATA_EN_AU.jsonNodeUnmarshallContext();
    }

    @Test
    public void testParseFormula() {
        this.checkEquals(
            SpreadsheetFormula.EMPTY.setToken(
                Optional.of(
                    SpreadsheetFormulaParserToken.expression(
                        Lists.of(
                            SpreadsheetFormulaParserToken.equalsSymbol(
                                "=",
                                "="
                            ),
                            SpreadsheetFormulaParserToken.number(
                                Lists.of(
                                    SpreadsheetFormulaParserToken.digits(
                                        "1",
                                        "1"
                                    )
                                ),
                                "1"
                            )
                        ),
                        "=1"
                    )
                )
            ),
            SpreadsheetMetadataTesting.parseFormula(
                "=1"
            )
        );
    }

    @Test
    public void testSetLocaleFails() {
        final Locale locale = Locale.FRANCE;
        
        this.checkNotEquals(
            LOCALE,
            locale
        );
        
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> SPREADSHEET_ENVIRONMENT_CONTEXT.setLocale(locale)
        );
        
        this.localeAndCheck(
            SPREADSHEET_ENVIRONMENT_CONTEXT,
            LOCALE
        );
    }

    @Test
    public void testProviderContextSetEnvironmentValueFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> PROVIDER_CONTEXT.setEnvironmentValue(
                EnvironmentValueName.with(
                    "Hello",
                    String.class
                ),
                "World"
            )
        );
    }

    @Test
    public void testProviderContextRemoveEnvironmentValueFails() {
        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> PROVIDER_CONTEXT.removeEnvironmentValue(
                EnvironmentValueName.with(
                    "Hello",
                    String.class
                )
            )
        );
    }

    @Test
    public void testProviderContextCloneEnvironmentSetLineEnding() {
        final LineEnding lineEnding = LineEnding.CRNL;
        this.checkNotEquals(
            LINE_ENDING,
            lineEnding
        );

        final ProviderContext context = PROVIDER_CONTEXT.cloneEnvironment();
        this.setLineEndingAndCheck(
            context,
            lineEnding
        );
    }

    @Test
    public void testProviderContextCloneEnvironmentSetLocale() {
        final Locale locale = Locale.FRANCE;
        this.checkNotEquals(
            LOCALE,
            locale
        );

        final ProviderContext clone = PROVIDER_CONTEXT.cloneEnvironment();
        clone.setLocale(locale);

        this.localeAndCheck(
            clone,
            locale
        );
    }

    @Test
    public void testProviderContextCloneEnvironmentSetUser() {
        final EmailAddress user = EmailAddress.parse("different@example.com");

        final ProviderContext context = PROVIDER_CONTEXT.cloneEnvironment();
        this.setUserAndCheck(
            context,
            user
        );
    }

    @Test
    public void testProviderContextCloneEnvironmentSetEnvironmentValue() {
        final EnvironmentValueName<String> name = EnvironmentValueName.with(
            "Hello",
            String.class
        );
        final String value = "World123";

        final ProviderContext context = PROVIDER_CONTEXT.cloneEnvironment();
        this.setEnvironmentValueAndCheck(
            context,
            name,
            value
        );
    }

    @Test
    public void testSpreadsheetFormatterContext() {
        METADATA_EN_AU.spreadsheetFormatterContext(
            SpreadsheetMetadata.NO_CELL,
            (final Optional<Object> value) -> {
                throw new UnsupportedOperationException();
            },
            HAS_USER_DIRECTORIES,
            Indentation.SPACES2,
            (label) -> {
                throw new UnsupportedOperationException();
            },
            LINE_ENDING,
            LOCALE_CONTEXT,
            SPREADSHEET_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParser() {
        METADATA_EN_AU.spreadsheetParser(
            SPREADSHEET_PARSER_PROVIDER,
            PROVIDER_CONTEXT
        );
    }

    @Test
    public void testSpreadsheetParserContext() {
        METADATA_EN_AU
            .spreadsheetParserContext(
                SpreadsheetMetadata.NO_CELL,
                LOCALE_CONTEXT,
                () -> LocalDateTime.MIN
            );
    }

    @Test
    public void testSetUserFails() {
        final EmailAddress user = EmailAddress.parse("different@example.com");

        this.checkNotEquals(
            USER,
            user
        );

        assertThrows(
            ReadOnlyEnvironmentValueException.class,
            () -> SPREADSHEET_ENVIRONMENT_CONTEXT.setUser(
                Optional.of(user)
            )
        );

        this.userAndCheck(
            SPREADSHEET_ENVIRONMENT_CONTEXT,
            USER
        );
    }
}
