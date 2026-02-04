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
import walkingkooka.ToStringTesting;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentContexts;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContext;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContextTesting2;
import walkingkooka.spreadsheet.environment.SpreadsheetEnvironmentContexts;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.Storages;
import walkingkooka.text.Indentation;
import walkingkooka.text.LineEnding;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataSpreadsheetEnvironmentContextTest implements SpreadsheetEnvironmentContextTesting2<SpreadsheetMetadataSpreadsheetEnvironmentContext>,
    ToStringTesting<SpreadsheetMetadataSpreadsheetEnvironmentContext> {

    private final static Indentation INDENTATION = Indentation.SPACES4;

    private final static LocalDateTime NOW = LocalDateTime.of(
        1999,
        12,
        31,
        12,
        58
    );

    private final static AbsoluteUrl SERVER_URL = Url.parseAbsolute("https://example.com");

    private final static EmailAddress USER = EmailAddress.parse("user@example.com");

    private final static Storage<SpreadsheetStorageContext> STORAGE = Storages.fake();

    static {
        final EnvironmentContext context = EnvironmentContexts.map(
            EnvironmentContexts.empty(
                INDENTATION,
                LineEnding.NL,
                Locale.FRENCH,
                () -> NOW,
                Optional.of(USER)
            )
        );
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );
        CONTEXT = SpreadsheetEnvironmentContexts.basic(
            STORAGE,
            EnvironmentContexts.readOnly(
                Predicates.always(), // all properties are read-only
                context
            )
        );
    }

    private final static SpreadsheetEnvironmentContext CONTEXT;

    private final static SpreadsheetId SPREADSHEET_ID = SpreadsheetId.with(1);

    private final static SpreadsheetMetadata METADATA = SpreadsheetMetadataTesting.METADATA_EN_AU.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                null,
                CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                SpreadsheetMetadata.EMPTY,
                null
            )
        );
    }

    @Test
    public void testWithUnwrapsSpreadsheetMetadataEnvironmentContext() {
        final SpreadsheetMetadata spreadsheetMetadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.FRENCH
        );
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.fake();

        final SpreadsheetMetadataSpreadsheetEnvironmentContext wrap = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            spreadsheetMetadata,
            spreadsheetEnvironmentContext
        );

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            spreadsheetMetadata,
            wrap
        );

        this.checkEquals(
            wrap,
            spreadsheetMetadataSpreadsheetEnvironmentContext
        );
    }

    @Test
    public void testWithSpreadsheetMetadataEnvironmentContextDifferentSpreadsheetMetadata() {
        final SpreadsheetMetadata spreadsheetMetadata = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.FRENCH
        );
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = SpreadsheetEnvironmentContexts.fake();

        final SpreadsheetMetadataSpreadsheetEnvironmentContext wrap = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            spreadsheetMetadata,
            spreadsheetEnvironmentContext
        );

        final SpreadsheetMetadata spreadsheetMetadata2 = SpreadsheetMetadata.EMPTY.set(
            SpreadsheetMetadataPropertyName.LOCALE,
            Locale.GERMANY
        );

        this.checkEquals(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                spreadsheetMetadata2,
                spreadsheetEnvironmentContext
            ),
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                spreadsheetMetadata2,
                wrap
            )
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSame() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = CONTEXT;
        final SpreadsheetMetadata metadata = SpreadsheetMetadataTesting.METADATA_EN_AU;

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            metadata,
            spreadsheetEnvironmentContext
        );

        final SpreadsheetEnvironmentContext afterSet = spreadsheetMetadataSpreadsheetEnvironmentContext.setEnvironmentContext(spreadsheetEnvironmentContext);
        assertSame(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            afterSet
        );
    }

    @Test
    public void testSetEnvironmentContextWithDifferent() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = CONTEXT;
        final SpreadsheetMetadata metadata = METADATA;

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            metadata,
            spreadsheetEnvironmentContext
        );

        final EnvironmentContext differentEnvironmentContext = EnvironmentContexts.map(
            CONTEXT.cloneEnvironment()
        );
        differentEnvironmentContext.setLocale(Locale.GERMAN);

        this.checkNotEquals(
            spreadsheetEnvironmentContext,
            differentEnvironmentContext
        );

        final EnvironmentContext afterSet = spreadsheetMetadataSpreadsheetEnvironmentContext.setEnvironmentContext(differentEnvironmentContext);
        assertNotSame(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            afterSet
        );

        this.checkEquals(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                metadata,
                SpreadsheetEnvironmentContexts.basic(
                    STORAGE,
                    differentEnvironmentContext
                )
            ),
            afterSet
        );
    }

    // environmentValueNames............................................................................................

    @Test
    public void testEnvironmentValueNamesWithEmptySpreadsheetMetadata() {
        this.environmentValueNamesAndCheck(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                SpreadsheetMetadata.EMPTY,
                SpreadsheetEnvironmentContexts.basic(
                    STORAGE,
                    EnvironmentContexts.empty(
                        INDENTATION,
                        LineEnding.NL,
                        Locale.FRENCH,
                        () -> NOW,
                        Optional.of(USER)
                    )
                )
            ),
            SpreadsheetEnvironmentContext.INDENTATION,
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetEnvironmentContext.TIME_OFFSET,
            SpreadsheetEnvironmentContext.USER
        );
    }

    @Test
    public void testEnvironmentValueNames2() {
        this.environmentValueNamesAndCheck(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    SpreadsheetId.with(1)
                ),
                SpreadsheetEnvironmentContexts.basic(
                    STORAGE,
                    EnvironmentContexts.empty(
                        INDENTATION,
                        LineEnding.NL,
                        Locale.FRENCH,
                        () -> NOW,
                        Optional.of(USER)
                    )
                )
            ),
            SpreadsheetEnvironmentContext.INDENTATION,
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SpreadsheetEnvironmentContext.TIME_OFFSET,
            SpreadsheetEnvironmentContext.USER
        );
    }

    @Test
    public void testEnvironmentValueNames3() {
        final Locale locale = Locale.GERMAN;

        this.environmentValueNamesAndCheck(
            SpreadsheetMetadata.EMPTY
                .set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    SpreadsheetId.with(1)
                ).set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_NAME,
                    SpreadsheetName.with("Hello-spreadsheet-123")
                ).set(
                    SpreadsheetMetadataPropertyName.LOCALE,
                    locale
                ).setDefaults(
                    SpreadsheetMetadata.EMPTY
                        .set(
                            SpreadsheetMetadataPropertyName.LOCALE,
                            locale
                        ).set(
                            SpreadsheetMetadataPropertyName.ROUNDING_MODE,
                            RoundingMode.FLOOR
                        )
                ).spreadsheetEnvironmentContext(
                    CONTEXT.cloneEnvironment()
                ),
            SpreadsheetEnvironmentContext.INDENTATION,
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetMetadataPropertyName.ROUNDING_MODE.toEnvironmentValueName(),
            SpreadsheetEnvironmentContext.SERVER_URL,
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME.toEnvironmentValueName(),
            SpreadsheetEnvironmentContext.TIME_OFFSET,
            SpreadsheetEnvironmentContext.USER
        );
    }

    // environmentValue.................................................................................................

    @Test
    public void testEnvironmentValueWithPrefixButUnknown() {
        this.environmentValueAndCheck(
            EnvironmentValueName.with(
                "missing",
                Void.class
            )
        );
    }

    @Test
    public void testEnvironmentValueWrappedContextFirst() {
        final EnvironmentValueName<Locale> name = EnvironmentValueName.LOCALE;

        this.environmentValueAndCheck(
            name,
            CONTEXT.locale()
        );
    }

    @Test
    public void testEnvironmentValueDefaultsToSpreadsheetMetadata() {
        final SpreadsheetMetadataPropertyName<Integer> property = SpreadsheetMetadataPropertyName.PRECISION;

        this.environmentValueAndCheck(
            property.toEnvironmentValueName(),
            METADATA.getOrFail(property)
        );
    }

    @Test
    public void testIndentation() {
        final SpreadsheetMetadataSpreadsheetEnvironmentContext context = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA,
            CONTEXT.cloneEnvironment()
        );

        this.indentationAndCheck(
            context,
            INDENTATION
        );
    }
    
    @Test
    public void testLocale() {
        final Locale metadataLocale = Locale.ENGLISH;

        final SpreadsheetMetadataSpreadsheetEnvironmentContext context = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                metadataLocale
            ),
            CONTEXT.cloneEnvironment()
        );

        this.checkNotEquals(
            metadataLocale,
            CONTEXT.locale(),
            "locale"
        );

        this.localeAndCheck(
            context,
            CONTEXT.locale()
        );
    }


    @Test
    public void testSetLocaleUpdatesContext() {
        final Locale metadataLocale = Locale.ENGLISH;

        final SpreadsheetMetadataSpreadsheetEnvironmentContext context = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                metadataLocale
            ),
            CONTEXT.cloneEnvironment()
        );

        this.checkNotEquals(
            metadataLocale,
            CONTEXT.locale(),
            "locale"
        );

        this.localeAndCheck(
            context,
            CONTEXT.locale()
        );

        this.setLocaleAndCheck(
            context,
            Locale.GERMANY
        );
    }

    @Test
    public void testServerUrl() {
        this.serverUrlAndCheck(
            this.createContext(),
            SERVER_URL
        );
    }

    // spreadsheetId....................................................................................................

    @Test
    public void testSpreadsheetIdMissingFromWrappedEnvironmentContext() {
        final SpreadsheetId metadataSpreadsheetId = SpreadsheetId.with(2);

        this.checkNotEquals(
            SPREADSHEET_ID,
            metadataSpreadsheetId
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = CONTEXT.cloneEnvironment();
        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName()
        );

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                metadataSpreadsheetId
            ),
            spreadsheetEnvironmentContext
        );

        this.spreadsheetIdAndCheck(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            metadataSpreadsheetId
        );
    }

    @Test
    public void testSpreadsheetIdInEnvironmentContext() {
        final SpreadsheetId metadataSpreadsheetId = SpreadsheetId.with(2);

        this.checkNotEquals(
            SPREADSHEET_ID,
            metadataSpreadsheetId
        );

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setEnvironmentValue(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
                SPREADSHEET_ID
            );

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            SPREADSHEET_ID
        );
    }

    @Test
    public void testSetSpreadsheetIdUpdatesContext() {
        final SpreadsheetId metadataSpreadsheetId = SpreadsheetId.with(2);
        final SpreadsheetId environmentSpreadsheetId = SpreadsheetId.with(3);

        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = CONTEXT.cloneEnvironment();
        spreadsheetEnvironmentContext.setEnvironmentValue(
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            environmentSpreadsheetId
        );

        final SpreadsheetMetadataSpreadsheetEnvironmentContext spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                metadataSpreadsheetId
            ),
            spreadsheetEnvironmentContext
        );

        this.checkNotEquals(
            metadataSpreadsheetId,
            CONTEXT.environmentValue(
                SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName()
            ).orElse(null),
            "spreadsheetId"
        );

        this.spreadsheetIdAndCheck(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            environmentSpreadsheetId
        );

        final SpreadsheetId differentSpreadsheetId = SpreadsheetId.with(4);

        this.setSpreadsheetIdAndCheck(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            differentSpreadsheetId
        );

        this.environmentValueAndCheck(
            spreadsheetEnvironmentContext,
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            differentSpreadsheetId
        );
    }

    @Test
    public void testUser() {
        this.userAndCheck(USER);
    }

    @Test
    public void testNow() {
        this.checkEquals(
            this.createContext().now(),
            NOW
        );
    }

    @Override
    public SpreadsheetMetadataSpreadsheetEnvironmentContext createContext() {
        return SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
            METADATA,
            CONTEXT.cloneEnvironment()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetMetadataSpreadsheetEnvironmentContext.with(
                SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    SpreadsheetId.with(1)
                ),
                CONTEXT
            ),
            "{indentation=    , lineEnding=\\n, locale=fr, now=1999-12-31T12:58, serverUrl=https://example.com, spreadsheetId=1, timeOffset=Z, user=user@example.com}"
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createContext(),
            "SpreadsheetMetadataSpreadsheetEnvironmentContext\n" +
                "  auditInfo\n" +
                "    AuditInfo\n" +
                "      created\n" +
                "        user@example.com 1999-12-31T12:58\n" +
                "      modified\n" +
                "        user@example.com 1999-12-31T12:58\n" +
                "  autoHideScrollbars\n" +
                "    false\n" +
                "  cellCharacterWidth\n" +
                "    1\n" +
                "  color1\n" +
                "    black (walkingkooka.color.OpaqueRgbColor)\n" +
                "  color2\n" +
                "    white (walkingkooka.color.OpaqueRgbColor)\n" +
                "  colorBlack\n" +
                "    1\n" +
                "  colorWhite\n" +
                "    2\n" +
                "  comparators\n" +
                "    date\n" +
                "    date-time\n" +
                "    day-of-month\n" +
                "    day-of-week\n" +
                "    hour-of-am-pm\n" +
                "    hour-of-day\n" +
                "    minute-of-hour\n" +
                "    month-of-year\n" +
                "    nano-of-second\n" +
                "    number\n" +
                "    seconds-of-minute\n" +
                "    text\n" +
                "    text-case-insensitive\n" +
                "    time\n" +
                "    year\n" +
                "  converters\n" +
                "    basic\n" +
                "    boolean\n" +
                "    boolean-to-text\n" +
                "    collection\n" +
                "    collection-to\n" +
                "    collection-to-list\n" +
                "    color\n" +
                "    color-to-color\n" +
                "    color-to-number\n" +
                "    date-time\n" +
                "    date-time-symbols\n" +
                "    decimal-number-symbols\n" +
                "    environment\n" +
                "    error-throwing\n" +
                "    error-to-error\n" +
                "    error-to-number\n" +
                "    expression\n" +
                "    form-and-validation\n" +
                "    format-pattern-to-string\n" +
                "    has-formatter-selector\n" +
                "    has-host-address\n" +
                "    has-parser-selector\n" +
                "    has-spreadsheet-selection\n" +
                "    has-style\n" +
                "    has-text-node\n" +
                "    has-validator-selector\n" +
                "    json\n" +
                "    json-to\n" +
                "    locale\n" +
                "    locale-to-text\n" +
                "    net\n" +
                "    null-to-number\n" +
                "    number\n" +
                "    number-to-color\n" +
                "    number-to-number\n" +
                "    number-to-text\n" +
                "    optional-to\n" +
                "    plugins\n" +
                "    spreadsheet-cell-set\n" +
                "    spreadsheet-metadata\n" +
                "    spreadsheet-selection-to-spreadsheet-selection\n" +
                "    spreadsheet-selection-to-text\n" +
                "    spreadsheet-value\n" +
                "    storage\n" +
                "    storage-path-json-to-class\n" +
                "    storage-value-info-list-to-text\n" +
                "    style\n" +
                "    system\n" +
                "    template\n" +
                "    text\n" +
                "    text-node\n" +
                "    text-to-boolean-list\n" +
                "    text-to-color\n" +
                "    text-to-csv-string-list\n" +
                "    text-to-date-list\n" +
                "    text-to-date-time-list\n" +
                "    text-to-email-address\n" +
                "    text-to-environment-value-name\n" +
                "    text-to-error\n" +
                "    text-to-expression\n" +
                "    text-to-form-name\n" +
                "    text-to-has-host-address\n" +
                "    text-to-host-address\n" +
                "    text-to-json\n" +
                "    text-to-line-ending\n" +
                "    text-to-locale\n" +
                "    text-to-number-list\n" +
                "    text-to-object\n" +
                "    text-to-spreadsheet-color-name\n" +
                "    text-to-spreadsheet-formatter-selector\n" +
                "    text-to-spreadsheet-id\n" +
                "    text-to-spreadsheet-metadata\n" +
                "    text-to-spreadsheet-metadata-color\n" +
                "    text-to-spreadsheet-metadata-property-name\n" +
                "    text-to-spreadsheet-name\n" +
                "    text-to-spreadsheet-selection\n" +
                "    text-to-spreadsheet-text\n" +
                "    text-to-storage-path\n" +
                "    text-to-string-list\n" +
                "    text-to-template-value-name\n" +
                "    text-to-text\n" +
                "    text-to-text-node\n" +
                "    text-to-text-style\n" +
                "    text-to-text-style-property-name\n" +
                "    text-to-time-list\n" +
                "    text-to-url\n" +
                "    text-to-url-fragment\n" +
                "    text-to-url-query-string\n" +
                "    text-to-validation-error\n" +
                "    text-to-validator-selector\n" +
                "    text-to-value-type\n" +
                "    text-to-zone-offset\n" +
                "    to-boolean\n" +
                "    to-json-node\n" +
                "    to-json-text\n" +
                "    to-number\n" +
                "    to-string\n" +
                "    to-styleable\n" +
                "    to-validation-checkbox\n" +
                "    to-validation-choice\n" +
                "    to-validation-choice-list\n" +
                "    to-validation-error-list\n" +
                "    url\n" +
                "    url-to-hyperlink\n" +
                "    url-to-image\n" +
                "  dateFormatter\n" +
                "    date\n" +
                "      \"yyyy/mm/dd\"\n" +
                "  dateParser\n" +
                "    date\n" +
                "      \"yyyy/mm/dd\"\n" +
                "  dateTimeFormatter\n" +
                "    date-time\n" +
                "      \"yyyy/mm/dd hh:mm\"\n" +
                "  dateTimeOffset\n" +
                "    -25569L\n" +
                "  dateTimeParser\n" +
                "    date-time\n" +
                "      \"yyyy/mm/dd hh:mm\"\n" +
                "  dateTimeSymbols\n" +
                "    DateTimeSymbols\n" +
                "      ampms\n" +
                "        am\n" +
                "        pm\n" +
                "      monthNames\n" +
                "        January\n" +
                "        February\n" +
                "        March\n" +
                "        April\n" +
                "        May\n" +
                "        June\n" +
                "        July\n" +
                "        August\n" +
                "        September\n" +
                "        October\n" +
                "        November\n" +
                "        December\n" +
                "      monthNameAbbreviations\n" +
                "        Jan.\n" +
                "        Feb.\n" +
                "        Mar.\n" +
                "        Apr.\n" +
                "        May\n" +
                "        Jun.\n" +
                "        Jul.\n" +
                "        Aug.\n" +
                "        Sep.\n" +
                "        Oct.\n" +
                "        Nov.\n" +
                "        Dec.\n" +
                "      weekDayNames\n" +
                "        Sunday\n" +
                "        Monday\n" +
                "        Tuesday\n" +
                "        Wednesday\n" +
                "        Thursday\n" +
                "        Friday\n" +
                "        Saturday\n" +
                "      weekDayNameAbbreviations\n" +
                "        Sun.\n" +
                "        Mon.\n" +
                "        Tue.\n" +
                "        Wed.\n" +
                "        Thu.\n" +
                "        Fri.\n" +
                "        Sat.\n" +
                "  decimalNumberDigitCount\n" +
                "    8\n" +
                "  decimalNumberSymbols\n" +
                "    DecimalNumberSymbols\n" +
                "      negativeSign\n" +
                "        '-'\n" +
                "      positiveSign\n" +
                "        '+'\n" +
                "      zeroDigit\n" +
                "        '0'\n" +
                "      currencySymbol\n" +
                "        \"$\"\n" +
                "      decimalSeparator\n" +
                "        '.'\n" +
                "      exponentSymbol\n" +
                "        \"e\"\n" +
                "      groupSeparator\n" +
                "        ','\n" +
                "      infinitySymbol\n" +
                "        \"∞\"\n" +
                "      monetaryDecimalSeparator\n" +
                "        '.'\n" +
                "      nanSymbol\n" +
                "        \"NaN\"\n" +
                "      percentSymbol\n" +
                "        '%'\n" +
                "      permillSymbol\n" +
                "        '‰'\n" +
                "  defaultFormHandler\n" +
                "    basic\n" +
                "  defaultYear\n" +
                "    2000\n" +
                "  errorFormatter\n" +
                "    badge-error\n" +
                "      \"text @\"\n" +
                "  exporters\n" +
                "    collection\n" +
                "    empty\n" +
                "    json\n" +
                "  expressionNumberKind\n" +
                "    BIG_DECIMAL\n" +
                "  findConverter\n" +
                "    collection\n" +
                "      \"(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, spreadsheet-metadata, style, text-node, template, net)\"\n" +
                "  findFunctions\n" +
                "  formatters\n" +
                "    accounting\n" +
                "    automatic\n" +
                "    badge-error\n" +
                "    collection\n" +
                "    currency\n" +
                "    date\n" +
                "    date-time\n" +
                "    default-text\n" +
                "    expression\n" +
                "    full-date\n" +
                "    full-date-time\n" +
                "    full-time\n" +
                "    general\n" +
                "    hyperlinking\n" +
                "    long-date\n" +
                "    long-date-time\n" +
                "    long-time\n" +
                "    medium-date\n" +
                "    medium-date-time\n" +
                "    medium-time\n" +
                "    number\n" +
                "    percent\n" +
                "    scientific\n" +
                "    short-date\n" +
                "    short-date-time\n" +
                "    short-time\n" +
                "    text\n" +
                "    time\n" +
                "  formattingConverter\n" +
                "    collection\n" +
                "      \"(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, plugins, style, text-node, template, net)\"\n" +
                "  formattingFunctions\n" +
                "  formHandlers\n" +
                "  formulaConverter\n" +
                "    collection\n" +
                "      \"(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, locale, template, net, json)\"\n" +
                "  formulaFunctions\n" +
                "  functions\n" +
                "  importers\n" +
                "    collection\n" +
                "    empty\n" +
                "    json\n" +
                "  indentation\n" +
                "    \"    \" (walkingkooka.text.Indentation)\n" +
                "  lineEnding\n" +
                "    \"\\n\"\n" +
                "  locale\n" +
                "    fr (java.util.Locale)\n" +
                "  now\n" +
                "    1999-12-31T12:58 (java.time.LocalDateTime)\n" +
                "  numberFormatter\n" +
                "    number\n" +
                "      \"0.#;0.#;0\"\n" +
                "  numberParser\n" +
                "    number\n" +
                "      \"0.#;0.#;0\"\n" +
                "  parsers\n" +
                "    date\n" +
                "    date-time\n" +
                "    general\n" +
                "    number\n" +
                "    time\n" +
                "    whole-number\n" +
                "  plugins\n" +
                "  precision\n" +
                "    7\n" +
                "  roundingMode\n" +
                "    HALF_UP\n" +
                "  scriptingConverter\n" +
                "    collection\n" +
                "      \"(text, boolean, number, date-time, basic, spreadsheet-value, error-throwing, color, expression, environment, json, locale, plugins, spreadsheet-metadata, storage, storage-path-json-to-class, style, text-node, text-to-line-ending, template, net)\"\n" +
                "  scriptingFunctions\n" +
                "  serverUrl\n" +
                "    https://example.com (walkingkooka.net.AbsoluteUrl)\n" +
                "  showFormulaEditor\n" +
                "    true\n" +
                "  showFormulas\n" +
                "    false\n" +
                "  showGridLines\n" +
                "    true\n" +
                "  showHeadings\n" +
                "    true\n" +
                "  sortComparators\n" +
                "    [date, datetime, day-of-month, day-of-year, hour-of-ampm, hour-of-day, minute-of-hour, month-of-year, nano-of-second, number, seconds-of-minute, text, text-case-insensitive, time, year]\n" +
                "  sortConverter\n" +
                "    collection\n" +
                "      \"(text, boolean, number, date-time, basic, spreadsheet-value, locale)\"\n" +
                "  spreadsheetId\n" +
                "    1\n" +
                "  style\n" +
                "    TextStyle\n" +
                "      height=50px (walkingkooka.tree.text.PixelLength)\n" +
                "      width=100px (walkingkooka.tree.text.PixelLength)\n" +
                "  textFormatter\n" +
                "    text\n" +
                "      \"@\"\n" +
                "  timeFormatter\n" +
                "    time\n" +
                "      \"hh:mm:ss\"\n" +
                "  timeOffset\n" +
                "    Z (java.time.ZoneOffset)\n" +
                "  timeParser\n" +
                "    time\n" +
                "      \"hh:mm:ss\"\n" +
                "  twoDigitYear\n" +
                "    50\n" +
                "  user\n" +
                "    user@example.com (walkingkooka.net.email.EmailAddress)\n" +
                "  validationConverter\n" +
                "    collection\n" +
                "      \"(text, boolean, number, date-time, basic, spreadsheet-value, environment, error-throwing, expression, form-and-validation, locale, plugins, template, json)\"\n" +
                "  validationFunctions\n" +
                "  validationValidators\n" +
                "    absolute-url\n" +
                "    checkbox\n" +
                "    choice-list\n" +
                "    collection\n" +
                "    email-address\n" +
                "    expression\n" +
                "    non-null\n" +
                "    text-length\n" +
                "    text-mask\n" +
                "  validators\n" +
                "    absolute-url\n" +
                "    checkbox\n" +
                "    choice-list\n" +
                "    collection\n" +
                "    email-address\n" +
                "    expression\n" +
                "    non-null\n" +
                "    text-length\n" +
                "    text-mask\n" +
                "  valueSeparator\n" +
                "    ','\n"
        );
    }

    // TypeName.........................................................................................................

    @Override
    public String typeNameSuffix() {
        return EnvironmentContext.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataSpreadsheetEnvironmentContext> type() {
        return SpreadsheetMetadataSpreadsheetEnvironmentContext.class;
    }
}
