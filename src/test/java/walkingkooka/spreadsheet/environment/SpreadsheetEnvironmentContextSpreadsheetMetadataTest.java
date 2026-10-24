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

package walkingkooka.spreadsheet.environment;

import org.junit.jupiter.api.Test;
import walkingkooka.ToStringTesting;
import walkingkooka.color.Color;
import walkingkooka.environment.EnvironmentContext;
import walkingkooka.environment.EnvironmentValueName;
import walkingkooka.predicate.Predicates;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.HasSpreadsheetMetadataTesting;
import walkingkooka.spreadsheet.meta.SpreadsheetId;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadata;
import walkingkooka.spreadsheet.meta.SpreadsheetMetadataPropertyName;
import walkingkooka.spreadsheet.meta.SpreadsheetName;
import walkingkooka.spreadsheet.storage.SpreadsheetStorageContext;
import walkingkooka.storage.Storage;
import walkingkooka.storage.StorageEnvironmentContext;
import walkingkooka.storage.Storages;

import java.math.RoundingMode;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetEnvironmentContextSpreadsheetMetadataTest implements SpreadsheetEnvironmentContextTesting2<SpreadsheetEnvironmentContextSpreadsheetMetadata>,
    ToStringTesting<SpreadsheetEnvironmentContextSpreadsheetMetadata> {

    private final static Storage<SpreadsheetStorageContext> STORAGE = Storages.fake();

    static {
        final StorageEnvironmentContext context = STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment();
        context.setEnvironmentValue(
            SpreadsheetEnvironmentContext.SERVER_URL,
            SERVER_URL
        );
        CONTEXT = SpreadsheetEnvironmentContexts.readOnly(
            Predicates.always(), // all values are readonly
            SpreadsheetEnvironmentContexts.basic(
                SPREADSHEET_METADATA_LOADER,
                STORAGE,
                context
            )
        );
    }

    private final static SpreadsheetEnvironmentContext CONTEXT;

    private final static SpreadsheetMetadata METADATA = HasSpreadsheetMetadataTesting.SPREADSHEET_METADATA.set(
        SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
        SPREADSHEET_ID
    );

    @Test
    public void testWithNullMetadataFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
                null,
                CONTEXT
            )
        );
    }

    @Test
    public void testWithNullSpreadsheetEnvironmentContextFails() {
        assertThrows(
            NullPointerException.class,
            () -> SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
                SpreadsheetMetadata.EMPTY,
                null
            )
        );
    }

    @Test
    public void testWithSpreadsheetEnvironmentContextSpreadsheetMetadata() {
        final SpreadsheetEnvironmentContextSpreadsheetMetadata spreadsheetEnvironmentContextSpreadsheetMetadata = this.createContext();

        assertSame(
            spreadsheetEnvironmentContextSpreadsheetMetadata,
            SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
                METADATA,
                spreadsheetEnvironmentContextSpreadsheetMetadata
            )
        );
    }

    @Test
    public void testWithSpreadsheetEnvironmentContextSpreadsheetMetadataAndDifferentHasSpreadsheetMetadata() {
        final SpreadsheetEnvironmentContextSpreadsheetMetadata spreadsheetEnvironmentContextSpreadsheetMetadata = SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
            new HasSpreadsheetMetadata() {
                @Override
                public Optional<SpreadsheetMetadata> spreadsheetMetadata() {
                    return NO_METADATA;
                }

                @Override
                public void setSpreadsheetMetadata(final Optional<SpreadsheetMetadata> spreadsheetMetadata) {
                    Objects.requireNonNull(spreadsheetMetadata, "spreadsheetMetadata");
                    throw new UnsupportedOperationException();
                }
            },
            CONTEXT.cloneEnvironment()
        );

        final SpreadsheetEnvironmentContextSpreadsheetMetadata spreadsheetEnvironmentContextSpreadsheetMetadata2 = SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
            METADATA,
            CONTEXT.cloneEnvironment()
        );

        assertNotSame(
            spreadsheetEnvironmentContextSpreadsheetMetadata,
            spreadsheetEnvironmentContextSpreadsheetMetadata2
        );
    }

    // environmentContext...............................................................................................

    @Test
    public void testEnvironmentValueWithSpreadsheetMetadataPropertyName() {
        final SpreadsheetMetadataPropertyName<Color> propertyName = SpreadsheetMetadataPropertyName.numberedColor(
            Color.indexed(1)
        );
        final SpreadsheetEnvironmentContextSpreadsheetMetadata context = this.createContext();

        this.environmentValueAndCheck(
            context,
            propertyName.toEnvironmentValueName(),
            METADATA.getOrFail(propertyName)
        );
    }

    // setEnvironmentContext............................................................................................

    @Test
    public void testSetEnvironmentContextWithSame() {
        final SpreadsheetEnvironmentContext spreadsheetEnvironmentContext = CONTEXT.cloneEnvironment();
        final SpreadsheetMetadata metadata = HasSpreadsheetMetadataTesting.SPREADSHEET_METADATA;

        final SpreadsheetEnvironmentContextSpreadsheetMetadata spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
            metadata,
            spreadsheetEnvironmentContext
        );

        final SpreadsheetEnvironmentContext afterSet = spreadsheetMetadataSpreadsheetEnvironmentContext.setEnvironmentContext(spreadsheetEnvironmentContext);
        this.checkEquals(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            afterSet
        );
    }

    @Test
    public void testSetEnvironmentContextWithDifferent() {
        final SpreadsheetMetadata metadata = METADATA;

        final SpreadsheetEnvironmentContextSpreadsheetMetadata spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
            metadata,
            CONTEXT
        );

        final EnvironmentContext afterSet = spreadsheetMetadataSpreadsheetEnvironmentContext.setEnvironmentContext(DIFFERENT_STORAGE_ENVIRONMENT_CONTEXT);
        assertNotSame(
            spreadsheetMetadataSpreadsheetEnvironmentContext,
            afterSet
        );

        this.checkEquals(
            SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
                metadata,
                SpreadsheetEnvironmentContexts.basic(
                    SPREADSHEET_METADATA_LOADER,
                    STORAGE,
                    DIFFERENT_STORAGE_ENVIRONMENT_CONTEXT
                )
            ),
            afterSet
        );
    }

    // environmentValueNames............................................................................................

    @Test
    public void testEnvironmentValueNamesWithEmptySpreadsheetMetadata() {
        this.environmentValueNamesAndCheck(
            SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
                SpreadsheetMetadata.EMPTY,
                SpreadsheetEnvironmentContexts.basic(
                    SPREADSHEET_METADATA_LOADER,
                    STORAGE,
                    STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
                )
            ),
            SpreadsheetEnvironmentContext.CHARSET,
            SpreadsheetEnvironmentContext.CURRENCY,
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            SpreadsheetEnvironmentContext.HOME_DIRECTORY,
            SpreadsheetEnvironmentContext.INDENTATION,
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.LOGGING_LEVEL,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetEnvironmentContext.SPREADSHEET_METADATA,
            SpreadsheetEnvironmentContext.TIME_OFFSET,
            SpreadsheetEnvironmentContext.USER
        );
    }

    @Test
    public void testEnvironmentValueNames2() {
        this.environmentValueNamesAndCheck(
            SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
                SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    SPREADSHEET_ID
                ),
                SpreadsheetEnvironmentContexts.basic(
                    SPREADSHEET_METADATA_LOADER,
                    STORAGE,
                    STORAGE_ENVIRONMENT_CONTEXT.cloneEnvironment()
                )
            ),
            SpreadsheetEnvironmentContext.CHARSET,
            SpreadsheetEnvironmentContext.CURRENCY,
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            SpreadsheetEnvironmentContext.HOME_DIRECTORY,
            SpreadsheetEnvironmentContext.INDENTATION,
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.LOGGING_LEVEL,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetEnvironmentContext.SPREADSHEET_ID,
            SpreadsheetEnvironmentContext.SPREADSHEET_METADATA,
            SpreadsheetEnvironmentContext.TIME_OFFSET,
            SpreadsheetEnvironmentContext.USER
        );
    }

    @Test
    public void testEnvironmentValueNames3() {
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
                    DIFFERENT_LOCALE
                ).setDefaults(
                    SpreadsheetMetadata.EMPTY
                        .set(
                            SpreadsheetMetadataPropertyName.LOCALE,
                            DIFFERENT_LOCALE
                        ).set(
                            SpreadsheetMetadataPropertyName.ROUNDING_MODE,
                            RoundingMode.FLOOR
                        )
                ).spreadsheetEnvironmentContext(
                    CONTEXT.cloneEnvironment()
                ),
            SpreadsheetEnvironmentContext.CHARSET,
            SpreadsheetEnvironmentContext.CURRENCY,
            SpreadsheetEnvironmentContext.CURRENT_WORKING_DIRECTORY,
            SpreadsheetEnvironmentContext.HOME_DIRECTORY,
            SpreadsheetEnvironmentContext.INDENTATION,
            SpreadsheetEnvironmentContext.LINE_ENDING,
            SpreadsheetEnvironmentContext.LOCALE,
            SpreadsheetEnvironmentContext.LOGGING_LEVEL,
            SpreadsheetEnvironmentContext.NOW,
            SpreadsheetMetadataPropertyName.ROUNDING_MODE.toEnvironmentValueName(),
            SpreadsheetEnvironmentContext.SERVER_URL,
            SpreadsheetMetadataPropertyName.SPREADSHEET_ID.toEnvironmentValueName(),
            SpreadsheetMetadataPropertyName.SPREADSHEET_NAME.toEnvironmentValueName(),
            SpreadsheetEnvironmentContext.SPREADSHEET_METADATA,
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
    public void testLocale() {
        final SpreadsheetEnvironmentContextSpreadsheetMetadata context = SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                DIFFERENT_LOCALE
            ),
            CONTEXT.cloneEnvironment()
        );

        this.checkNotEquals(
            DIFFERENT_LOCALE,
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
        final SpreadsheetEnvironmentContextSpreadsheetMetadata context = SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
            METADATA.set(
                SpreadsheetMetadataPropertyName.LOCALE,
                DIFFERENT_LOCALE
            ),
            CONTEXT.cloneEnvironment()
        );

        this.checkNotEquals(
            DIFFERENT_LOCALE,
            CONTEXT.locale(),
            "locale"
        );

        this.localeAndCheck(
            context,
            CONTEXT.locale()
        );

        this.setLocaleAndCheck(
            context,
            DIFFERENT_LOCALE
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

        final SpreadsheetEnvironmentContextSpreadsheetMetadata spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
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

        final SpreadsheetEnvironmentContextSpreadsheetMetadata spreadsheetMetadataSpreadsheetEnvironmentContext = SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
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
    public SpreadsheetEnvironmentContextSpreadsheetMetadata createContext() {
        return SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
            METADATA,
            CONTEXT.cloneEnvironment()
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
            SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
                SpreadsheetMetadata.EMPTY.set(
                    SpreadsheetMetadataPropertyName.SPREADSHEET_ID,
                    SPREADSHEET_ID
                ),
                CONTEXT
            ),
            "{charset=UTF-8, currency=AUD, currentWorkingDirectory=/current1/working2/directory3, homeDirectory=/users/user123@example.com, indentation=  , lineEnding=\\n, locale=en_AU, loggingLevel=NONE, now=1999-12-31T12:58:59, serverUrl=https://example.com, spreadsheetId=null, spreadsheetMetadata=null, timeOffset=Z, user=user123@example.com}"
        );
    }

    // HasEnvironmentContext............................................................................................

    @Test
    @Override
    public void testEnvironmentContext() {
        final SpreadsheetEnvironmentContext context = SpreadsheetEnvironmentContexts.fake();

        this.environmentContextAndCheck(
            SpreadsheetEnvironmentContextSpreadsheetMetadata.with(
                METADATA,
                context
            ),
            context
        );
    }

    // TreePrintable....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
            this.createContext(),
            "SpreadsheetEnvironmentContextSpreadsheetMetadata\n" +
                "  environment\n" +
                "    auditInfo\n" +
                "      AuditInfo\n" +
                "        created\n" +
                "          user123@example.com 1999-12-31T12:58:59\n" +
                "        modified\n" +
                "          user123@example.com 1999-12-31T12:58:59\n" +
                "    autoHideScrollbars\n" +
                "      false\n" +
                "    cellCharacterWidth\n" +
                "      1\n" +
                "    charset\n" +
                "      UTF-8 (sun.nio.cs.UTF_8)\n" +
                "    color1\n" +
                "      black\n" +
                "    color2\n" +
                "      white\n" +
                "    colorBlack\n" +
                "      1\n" +
                "    colorWhite\n" +
                "      2\n" +
                "    comparators\n" +
                "      background-color\n" +
                "      border-bottom-color\n" +
                "      border-color\n" +
                "      border-left-color\n" +
                "      border-right-color\n" +
                "      border-top-color\n" +
                "      color\n" +
                "      currency\n" +
                "      custom-list\n" +
                "      custom-list-case-insensitive\n" +
                "      date\n" +
                "      date-time\n" +
                "      day-of-month\n" +
                "      day-of-week\n" +
                "      error\n" +
                "      formatter\n" +
                "      hour-of-am-pm\n" +
                "      hour-of-day\n" +
                "      locale\n" +
                "      minute-of-hour\n" +
                "      month-of-year\n" +
                "      nano-of-second\n" +
                "      number\n" +
                "      outline-color\n" +
                "      parser\n" +
                "      seconds-of-minute\n" +
                "      text\n" +
                "      text-case-insensitive\n" +
                "      text-decoration-color\n" +
                "      text-with-numbers\n" +
                "      text-with-numbers-case-insensitive\n" +
                "      time\n" +
                "      validator\n" +
                "      value-type\n" +
                "      year\n" +
                "    converters\n" +
                "      binary\n" +
                "      binary-to-text\n" +
                "      boolean\n" +
                "      boolean-to-text\n" +
                "      collection\n" +
                "      collection-to\n" +
                "      collection-to-list\n" +
                "      color\n" +
                "      color-to-number\n" +
                "      csv\n" +
                "      currency\n" +
                "      currency-code-to-currency\n" +
                "      currency-value-to\n" +
                "      currency-value-to-number\n" +
                "      date-time\n" +
                "      date-time-symbols\n" +
                "      decimal-number-symbols\n" +
                "      environment\n" +
                "      environment-to-binary\n" +
                "      environment-to-text\n" +
                "      error-throwing\n" +
                "      error-to-error\n" +
                "      error-to-number\n" +
                "      expression\n" +
                "      form-and-validation\n" +
                "      format-pattern-to-string\n" +
                "      json\n" +
                "      json-to\n" +
                "      locale\n" +
                "      locale-to-text\n" +
                "      logging\n" +
                "      net\n" +
                "      never\n" +
                "      null-to-number\n" +
                "      number\n" +
                "      number-to-color\n" +
                "      number-to-currency-value\n" +
                "      number-to-number\n" +
                "      number-to-text\n" +
                "      optional-to\n" +
                "      plugins\n" +
                "      properties\n" +
                "      properties-to-date-time-symbols\n" +
                "      properties-to-decimal-number-symbols\n" +
                "      properties-to-spreadsheet-metadata\n" +
                "      properties-to-text-style\n" +
                "      simple\n" +
                "      spreadsheet-cell-set\n" +
                "      spreadsheet-id-to-spreadsheet-metadata\n" +
                "      spreadsheet-metadata\n" +
                "      spreadsheet-metadata-to-text\n" +
                "      spreadsheet-selection\n" +
                "      spreadsheet-selection-to-spreadsheet-selection\n" +
                "      spreadsheet-selection-to-text\n" +
                "      storage\n" +
                "      storage-binary-to-storage-value-binary\n" +
                "      storage-binary-to-storage-value-csv\n" +
                "      storage-binary-to-storage-value-environment\n" +
                "      storage-binary-to-storage-value-expression\n" +
                "      storage-binary-to-storage-value-json\n" +
                "      storage-binary-to-storage-value-properties\n" +
                "      storage-binary-to-storage-value-tsv\n" +
                "      storage-binary-to-storage-value-txt\n" +
                "      storage-value-info-list-to-text\n" +
                "      storage-value-to-storage-binary-binary\n" +
                "      storage-value-to-storage-binary-csv\n" +
                "      storage-value-to-storage-binary-environment\n" +
                "      storage-value-to-storage-binary-expression\n" +
                "      storage-value-to-storage-binary-json\n" +
                "      storage-value-to-storage-binary-properties\n" +
                "      storage-value-to-storage-binary-tsv\n" +
                "      storage-value-to-storage-binary-txt\n" +
                "      style\n" +
                "      system\n" +
                "      template\n" +
                "      text\n" +
                "      text-node\n" +
                "      text-to-binary\n" +
                "      text-to-boolean-list\n" +
                "      text-to-border\n" +
                "      text-to-charset\n" +
                "      text-to-color\n" +
                "      text-to-converter-selector\n" +
                "      text-to-csv-string-list\n" +
                "      text-to-csv-string-set\n" +
                "      text-to-currency\n" +
                "      text-to-currency-code\n" +
                "      text-to-currency-exchange-rater-selector\n" +
                "      text-to-currency-value\n" +
                "      text-to-date-list\n" +
                "      text-to-date-time-list\n" +
                "      text-to-email-address\n" +
                "      text-to-environment\n" +
                "      text-to-environment-value-name\n" +
                "      text-to-error\n" +
                "      text-to-expression\n" +
                "      text-to-expression-function-selector\n" +
                "      text-to-flag\n" +
                "      text-to-form-name\n" +
                "      text-to-has-host-address\n" +
                "      text-to-host-address\n" +
                "      text-to-indentation\n" +
                "      text-to-json\n" +
                "      text-to-json-pointer\n" +
                "      text-to-json-selector\n" +
                "      text-to-line-ending\n" +
                "      text-to-locale\n" +
                "      text-to-locale-language-tag\n" +
                "      text-to-logging-level\n" +
                "      text-to-margin\n" +
                "      text-to-media-type\n" +
                "      text-to-number-list\n" +
                "      text-to-object\n" +
                "      text-to-padding\n" +
                "      text-to-path\n" +
                "      text-to-properties\n" +
                "      text-to-spreadsheet-comparator-selector\n" +
                "      text-to-spreadsheet-formatter-selector\n" +
                "      text-to-spreadsheet-id\n" +
                "      text-to-spreadsheet-metadata\n" +
                "      text-to-spreadsheet-metadata-color\n" +
                "      text-to-spreadsheet-metadata-property-name\n" +
                "      text-to-spreadsheet-name\n" +
                "      text-to-spreadsheet-parser-selector\n" +
                "      text-to-spreadsheet-selection\n" +
                "      text-to-spreadsheet-text\n" +
                "      text-to-storage-path\n" +
                "      text-to-string-list\n" +
                "      text-to-template-value-name\n" +
                "      text-to-text\n" +
                "      text-to-text-node\n" +
                "      text-to-text-style\n" +
                "      text-to-text-style-property-name\n" +
                "      text-to-time-list\n" +
                "      text-to-tsv-string-list\n" +
                "      text-to-tsv-string-set\n" +
                "      text-to-url\n" +
                "      text-to-url-fragment\n" +
                "      text-to-url-query-string\n" +
                "      text-to-validation-error\n" +
                "      text-to-validator-selector\n" +
                "      text-to-value-type\n" +
                "      text-to-web-color-name\n" +
                "      text-to-zone-offset\n" +
                "      to-binary\n" +
                "      to-boolean\n" +
                "      to-color\n" +
                "      to-color-properties\n" +
                "      to-csv-string-list\n" +
                "      to-currency\n" +
                "      to-currency-code\n" +
                "      to-date-time-symbols\n" +
                "      to-decimal-number-symbols\n" +
                "      to-environment\n" +
                "      to-formatter-selector\n" +
                "      to-has-host-address\n" +
                "      to-host-address\n" +
                "      to-json-node\n" +
                "      to-locale\n" +
                "      to-locale-language-tag\n" +
                "      to-multi-line-text\n" +
                "      to-number\n" +
                "      to-parser-selector\n" +
                "      to-properties\n" +
                "      to-spreadsheet-selection\n" +
                "      to-style\n" +
                "      to-styleable\n" +
                "      to-text\n" +
                "      to-text-node\n" +
                "      to-tsv-string-list\n" +
                "      to-validation-checkbox\n" +
                "      to-validation-choice\n" +
                "      to-validation-choice-list\n" +
                "      to-validation-error-list\n" +
                "      to-validator-selector\n" +
                "      to-value\n" +
                "      to-value-type\n" +
                "      tsv\n" +
                "      url\n" +
                "      url-to-hyperlink\n" +
                "      url-to-image\n" +
                "      value\n" +
                "    currency\n" +
                "      AUD (java.util.Currency)\n" +
                "    currentWorkingDirectory\n" +
                "      /current1/working2/directory3\n" +
                "    dateFormatter\n" +
                "      date\n" +
                "        \"yyyy/mm/dd\"\n" +
                "    dateOffset\n" +
                "      -25569L\n" +
                "    dateParser\n" +
                "      date\n" +
                "        \"yyyy/mm/dd\"\n" +
                "    dateTimeFormatter\n" +
                "      date-time\n" +
                "        \"yyyy/mm/dd hh:mm\"\n" +
                "    dateTimeParser\n" +
                "      date-time\n" +
                "        \"yyyy/mm/dd hh:mm\"\n" +
                "    dateTimeSymbols\n" +
                "      DateTimeSymbols\n" +
                "        ampms\n" +
                "          am\n" +
                "          pm\n" +
                "        monthNames\n" +
                "          January\n" +
                "          February\n" +
                "          March\n" +
                "          April\n" +
                "          May\n" +
                "          June\n" +
                "          July\n" +
                "          August\n" +
                "          September\n" +
                "          October\n" +
                "          November\n" +
                "          December\n" +
                "        monthNameAbbreviations\n" +
                "          Jan.\n" +
                "          Feb.\n" +
                "          Mar.\n" +
                "          Apr.\n" +
                "          May\n" +
                "          Jun.\n" +
                "          Jul.\n" +
                "          Aug.\n" +
                "          Sep.\n" +
                "          Oct.\n" +
                "          Nov.\n" +
                "          Dec.\n" +
                "        weekDayNames\n" +
                "          Sunday\n" +
                "          Monday\n" +
                "          Tuesday\n" +
                "          Wednesday\n" +
                "          Thursday\n" +
                "          Friday\n" +
                "          Saturday\n" +
                "        weekDayNameAbbreviations\n" +
                "          Sun.\n" +
                "          Mon.\n" +
                "          Tue.\n" +
                "          Wed.\n" +
                "          Thu.\n" +
                "          Fri.\n" +
                "          Sat.\n" +
                "    decimalNumberDigitCount\n" +
                "      8\n" +
                "    decimalNumberSymbols\n" +
                "      DecimalNumberSymbols\n" +
                "        negativeSign\n" +
                "          '-'\n" +
                "        positiveSign\n" +
                "          '+'\n" +
                "        zeroDigit\n" +
                "          '0'\n" +
                "        currencySymbol\n" +
                "          \"$\"\n" +
                "        decimalSeparator\n" +
                "          '.'\n" +
                "        exponentSymbol\n" +
                "          \"e\"\n" +
                "        groupSeparator\n" +
                "          ','\n" +
                "        infinitySymbol\n" +
                "          \"∞\"\n" +
                "        monetaryDecimalSeparator\n" +
                "          '.'\n" +
                "        nanSymbol\n" +
                "          \"NaN\"\n" +
                "        percentSymbol\n" +
                "          '%'\n" +
                "        permillSymbol\n" +
                "          '‰'\n" +
                "    defaultFormHandler\n" +
                "      basic\n" +
                "    defaultYear\n" +
                "      2000\n" +
                "    errorFormatter\n" +
                "      badge-error\n" +
                "        \"text @\"\n" +
                "    exporters\n" +
                "      collection\n" +
                "      empty\n" +
                "      json\n" +
                "    expressionNumberKind\n" +
                "      BIG_DECIMAL\n" +
                "    formatters\n" +
                "      accounting\n" +
                "      automatic\n" +
                "      badge-error\n" +
                "      collection\n" +
                "      currency\n" +
                "      date\n" +
                "      date-time\n" +
                "      default-text\n" +
                "      expression\n" +
                "      full-date\n" +
                "      full-date-time\n" +
                "      full-time\n" +
                "      general\n" +
                "      hyperlinking\n" +
                "      long-date\n" +
                "      long-date-time\n" +
                "      long-time\n" +
                "      medium-date\n" +
                "      medium-date-time\n" +
                "      medium-time\n" +
                "      number\n" +
                "      percent\n" +
                "      scientific\n" +
                "      short-date\n" +
                "      short-date-time\n" +
                "      short-time\n" +
                "      text\n" +
                "      time\n" +
                "    formattingConverter\n" +
                "      collection\n" +
                "        \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\"\n" +
                "    formattingCurrencyExchangeRater\n" +
                "      storage-properties\n" +
                "        \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "    formattingFunctions\n" +
                "    formHandlers\n" +
                "    formulaConverter\n" +
                "      collection\n" +
                "        \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\"\n" +
                "    formulaCurrencyExchangeRater\n" +
                "      storage-properties\n" +
                "        \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "    formulaFunctions\n" +
                "    functions\n" +
                "    homeDirectory\n" +
                "      /users/user123@example.com\n" +
                "    importers\n" +
                "      collection\n" +
                "      empty\n" +
                "      json\n" +
                "    indentation\n" +
                "      \"  \" (walkingkooka.text.Indentation)\n" +
                "    lineEnding\n" +
                "      \"\\n\"\n" +
                "    locale\n" +
                "      en_AU (java.util.Locale)\n" +
                "    loggingLevel\n" +
                "      NONE\n" +
                "    now\n" +
                "      1999-12-31T12:58:59 (java.time.LocalDateTime)\n" +
                "    numberFormatter\n" +
                "      number\n" +
                "        \"0.#;0.#;0\"\n" +
                "    numberParser\n" +
                "      number\n" +
                "        \"0.#;0.#;0\"\n" +
                "    parsers\n" +
                "      date\n" +
                "      date-time\n" +
                "      full-date\n" +
                "      full-date-time\n" +
                "      full-time\n" +
                "      general\n" +
                "      long-date\n" +
                "      long-date-time\n" +
                "      long-time\n" +
                "      medium-date\n" +
                "      medium-date-time\n" +
                "      medium-time\n" +
                "      number\n" +
                "      short-date\n" +
                "      short-date-time\n" +
                "      short-time\n" +
                "      time\n" +
                "      whole-number\n" +
                "    plugins\n" +
                "    precision\n" +
                "      7\n" +
                "    queryConverter\n" +
                "      collection\n" +
                "        \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, properties, spreadsheet-metadata, style, text-node, template, net, binary, optional-to, collection-to)\"\n" +
                "    queryFunctions\n" +
                "    roundingMode\n" +
                "      HALF_UP\n" +
                "    scriptingConverter\n" +
                "      collection\n" +
                "        \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\"\n" +
                "    scriptingCurrencyExchangeRater\n" +
                "      storage-properties\n" +
                "        \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "    scriptingFunctions\n" +
                "    serverUrl\n" +
                "      https://example.com (walkingkooka.net.AbsoluteUrl)\n" +
                "    showFormulaEditor\n" +
                "      true\n" +
                "    showFormulas\n" +
                "      false\n" +
                "    showGridLines\n" +
                "      true\n" +
                "    showHeadings\n" +
                "      true\n" +
                "    sortComparators\n" +
                "      [background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, datetime, day-of-month, day-of-year, formatter, hour-of-ampm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, time, validator, value-type, year]\n" +
                "    sortConverter\n" +
                "      collection\n" +
                "        \"(null-to-number, simple, text, boolean, number, date-time, locale, value, optional-to, collection-to)\"\n" +
                "    spreadsheetId\n" +
                "      123\n" +
                "    style\n" +
                "      TextStyle\n" +
                "        height=50px\n" +
                "        width=100px\n" +
                "    textFormatter\n" +
                "      text\n" +
                "        \"@\"\n" +
                "    timeFormatter\n" +
                "      time\n" +
                "        \"hh:mm:ss\"\n" +
                "    timeOffset\n" +
                "      Z (java.time.ZoneOffset)\n" +
                "    timeParser\n" +
                "      time\n" +
                "        \"hh:mm:ss\"\n" +
                "    twoDigitYear\n" +
                "      50\n" +
                "    user\n" +
                "      user123@example.com (walkingkooka.net.email.EmailAddress)\n" +
                "    validationConverter\n" +
                "      collection\n" +
                "        \"(null-to-number, simple, text, boolean, number, date-time, environment, value, error-throwing, expression, form-and-validation, locale, logging, plugins, properties, template, json, optional-to, collection-to)\"\n" +
                "    validationCurrencyExchangeRater\n" +
                "      storage-properties\n" +
                "        \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "    validationFunctions\n" +
                "    validationValidators\n" +
                "      absolute-url\n" +
                "      checkbox\n" +
                "      choice-list\n" +
                "      collection\n" +
                "      email-address\n" +
                "      expression\n" +
                "      non-null\n" +
                "      text-length\n" +
                "      text-mask\n" +
                "    validators\n" +
                "      absolute-url\n" +
                "      checkbox\n" +
                "      choice-list\n" +
                "      collection\n" +
                "      email-address\n" +
                "      expression\n" +
                "      non-null\n" +
                "      text-length\n" +
                "      text-mask\n" +
                "    valueSeparator\n" +
                "      ','\n" +
                "  hasSpreadsheetMetadata\n" +
                "    SpreadsheetMetadataNonEmpty\n" +
                "      spreadsheetId: 123\n" +
                "        auditInfo: \n" +
                "          AuditInfo\n" +
                "            created\n" +
                "              user123@example.com 1999-12-31T12:58:59\n" +
                "            modified\n" +
                "              user123@example.com 1999-12-31T12:58:59\n" +
                "        autoHideScrollbars: false\n" +
                "        cellCharacterWidth: 1\n" +
                "        color1: black\n" +
                "        color2: white\n" +
                "        colorBlack: 1\n" +
                "        colorWhite: 2\n" +
                "        comparators: \n" +
                "          background-color\n" +
                "          border-bottom-color\n" +
                "          border-color\n" +
                "          border-left-color\n" +
                "          border-right-color\n" +
                "          border-top-color\n" +
                "          color\n" +
                "          currency\n" +
                "          custom-list\n" +
                "          custom-list-case-insensitive\n" +
                "          date\n" +
                "          date-time\n" +
                "          day-of-month\n" +
                "          day-of-week\n" +
                "          error\n" +
                "          formatter\n" +
                "          hour-of-am-pm\n" +
                "          hour-of-day\n" +
                "          locale\n" +
                "          minute-of-hour\n" +
                "          month-of-year\n" +
                "          nano-of-second\n" +
                "          number\n" +
                "          outline-color\n" +
                "          parser\n" +
                "          seconds-of-minute\n" +
                "          text\n" +
                "          text-case-insensitive\n" +
                "          text-decoration-color\n" +
                "          text-with-numbers\n" +
                "          text-with-numbers-case-insensitive\n" +
                "          time\n" +
                "          validator\n" +
                "          value-type\n" +
                "          year\n" +
                "        converters: \n" +
                "          binary\n" +
                "          binary-to-text\n" +
                "          boolean\n" +
                "          boolean-to-text\n" +
                "          collection\n" +
                "          collection-to\n" +
                "          collection-to-list\n" +
                "          color\n" +
                "          color-to-number\n" +
                "          csv\n" +
                "          currency\n" +
                "          currency-code-to-currency\n" +
                "          currency-value-to\n" +
                "          currency-value-to-number\n" +
                "          date-time\n" +
                "          date-time-symbols\n" +
                "          decimal-number-symbols\n" +
                "          environment\n" +
                "          environment-to-binary\n" +
                "          environment-to-text\n" +
                "          error-throwing\n" +
                "          error-to-error\n" +
                "          error-to-number\n" +
                "          expression\n" +
                "          form-and-validation\n" +
                "          format-pattern-to-string\n" +
                "          json\n" +
                "          json-to\n" +
                "          locale\n" +
                "          locale-to-text\n" +
                "          logging\n" +
                "          net\n" +
                "          never\n" +
                "          null-to-number\n" +
                "          number\n" +
                "          number-to-color\n" +
                "          number-to-currency-value\n" +
                "          number-to-number\n" +
                "          number-to-text\n" +
                "          optional-to\n" +
                "          plugins\n" +
                "          properties\n" +
                "          properties-to-date-time-symbols\n" +
                "          properties-to-decimal-number-symbols\n" +
                "          properties-to-spreadsheet-metadata\n" +
                "          properties-to-text-style\n" +
                "          simple\n" +
                "          spreadsheet-cell-set\n" +
                "          spreadsheet-id-to-spreadsheet-metadata\n" +
                "          spreadsheet-metadata\n" +
                "          spreadsheet-metadata-to-text\n" +
                "          spreadsheet-selection\n" +
                "          spreadsheet-selection-to-spreadsheet-selection\n" +
                "          spreadsheet-selection-to-text\n" +
                "          storage\n" +
                "          storage-binary-to-storage-value-binary\n" +
                "          storage-binary-to-storage-value-csv\n" +
                "          storage-binary-to-storage-value-environment\n" +
                "          storage-binary-to-storage-value-expression\n" +
                "          storage-binary-to-storage-value-json\n" +
                "          storage-binary-to-storage-value-properties\n" +
                "          storage-binary-to-storage-value-tsv\n" +
                "          storage-binary-to-storage-value-txt\n" +
                "          storage-value-info-list-to-text\n" +
                "          storage-value-to-storage-binary-binary\n" +
                "          storage-value-to-storage-binary-csv\n" +
                "          storage-value-to-storage-binary-environment\n" +
                "          storage-value-to-storage-binary-expression\n" +
                "          storage-value-to-storage-binary-json\n" +
                "          storage-value-to-storage-binary-properties\n" +
                "          storage-value-to-storage-binary-tsv\n" +
                "          storage-value-to-storage-binary-txt\n" +
                "          style\n" +
                "          system\n" +
                "          template\n" +
                "          text\n" +
                "          text-node\n" +
                "          text-to-binary\n" +
                "          text-to-boolean-list\n" +
                "          text-to-border\n" +
                "          text-to-charset\n" +
                "          text-to-color\n" +
                "          text-to-converter-selector\n" +
                "          text-to-csv-string-list\n" +
                "          text-to-csv-string-set\n" +
                "          text-to-currency\n" +
                "          text-to-currency-code\n" +
                "          text-to-currency-exchange-rater-selector\n" +
                "          text-to-currency-value\n" +
                "          text-to-date-list\n" +
                "          text-to-date-time-list\n" +
                "          text-to-email-address\n" +
                "          text-to-environment\n" +
                "          text-to-environment-value-name\n" +
                "          text-to-error\n" +
                "          text-to-expression\n" +
                "          text-to-expression-function-selector\n" +
                "          text-to-flag\n" +
                "          text-to-form-name\n" +
                "          text-to-has-host-address\n" +
                "          text-to-host-address\n" +
                "          text-to-indentation\n" +
                "          text-to-json\n" +
                "          text-to-json-pointer\n" +
                "          text-to-json-selector\n" +
                "          text-to-line-ending\n" +
                "          text-to-locale\n" +
                "          text-to-locale-language-tag\n" +
                "          text-to-logging-level\n" +
                "          text-to-margin\n" +
                "          text-to-media-type\n" +
                "          text-to-number-list\n" +
                "          text-to-object\n" +
                "          text-to-padding\n" +
                "          text-to-path\n" +
                "          text-to-properties\n" +
                "          text-to-spreadsheet-comparator-selector\n" +
                "          text-to-spreadsheet-formatter-selector\n" +
                "          text-to-spreadsheet-id\n" +
                "          text-to-spreadsheet-metadata\n" +
                "          text-to-spreadsheet-metadata-color\n" +
                "          text-to-spreadsheet-metadata-property-name\n" +
                "          text-to-spreadsheet-name\n" +
                "          text-to-spreadsheet-parser-selector\n" +
                "          text-to-spreadsheet-selection\n" +
                "          text-to-spreadsheet-text\n" +
                "          text-to-storage-path\n" +
                "          text-to-string-list\n" +
                "          text-to-template-value-name\n" +
                "          text-to-text\n" +
                "          text-to-text-node\n" +
                "          text-to-text-style\n" +
                "          text-to-text-style-property-name\n" +
                "          text-to-time-list\n" +
                "          text-to-tsv-string-list\n" +
                "          text-to-tsv-string-set\n" +
                "          text-to-url\n" +
                "          text-to-url-fragment\n" +
                "          text-to-url-query-string\n" +
                "          text-to-validation-error\n" +
                "          text-to-validator-selector\n" +
                "          text-to-value-type\n" +
                "          text-to-web-color-name\n" +
                "          text-to-zone-offset\n" +
                "          to-binary\n" +
                "          to-boolean\n" +
                "          to-color\n" +
                "          to-color-properties\n" +
                "          to-csv-string-list\n" +
                "          to-currency\n" +
                "          to-currency-code\n" +
                "          to-date-time-symbols\n" +
                "          to-decimal-number-symbols\n" +
                "          to-environment\n" +
                "          to-formatter-selector\n" +
                "          to-has-host-address\n" +
                "          to-host-address\n" +
                "          to-json-node\n" +
                "          to-locale\n" +
                "          to-locale-language-tag\n" +
                "          to-multi-line-text\n" +
                "          to-number\n" +
                "          to-parser-selector\n" +
                "          to-properties\n" +
                "          to-spreadsheet-selection\n" +
                "          to-style\n" +
                "          to-styleable\n" +
                "          to-text\n" +
                "          to-text-node\n" +
                "          to-tsv-string-list\n" +
                "          to-validation-checkbox\n" +
                "          to-validation-choice\n" +
                "          to-validation-choice-list\n" +
                "          to-validation-error-list\n" +
                "          to-validator-selector\n" +
                "          to-value\n" +
                "          to-value-type\n" +
                "          tsv\n" +
                "          url\n" +
                "          url-to-hyperlink\n" +
                "          url-to-image\n" +
                "          value\n" +
                "        currency: AUD (java.util.Currency)\n" +
                "        dateFormatter: \n" +
                "          date\n" +
                "            \"yyyy/mm/dd\"\n" +
                "        dateOffset: -25569L\n" +
                "        dateParser: \n" +
                "          date\n" +
                "            \"yyyy/mm/dd\"\n" +
                "        dateTimeFormatter: \n" +
                "          date-time\n" +
                "            \"yyyy/mm/dd hh:mm\"\n" +
                "        dateTimeParser: \n" +
                "          date-time\n" +
                "            \"yyyy/mm/dd hh:mm\"\n" +
                "        dateTimeSymbols: \n" +
                "          DateTimeSymbols\n" +
                "            ampms\n" +
                "              am\n" +
                "              pm\n" +
                "            monthNames\n" +
                "              January\n" +
                "              February\n" +
                "              March\n" +
                "              April\n" +
                "              May\n" +
                "              June\n" +
                "              July\n" +
                "              August\n" +
                "              September\n" +
                "              October\n" +
                "              November\n" +
                "              December\n" +
                "            monthNameAbbreviations\n" +
                "              Jan.\n" +
                "              Feb.\n" +
                "              Mar.\n" +
                "              Apr.\n" +
                "              May\n" +
                "              Jun.\n" +
                "              Jul.\n" +
                "              Aug.\n" +
                "              Sep.\n" +
                "              Oct.\n" +
                "              Nov.\n" +
                "              Dec.\n" +
                "            weekDayNames\n" +
                "              Sunday\n" +
                "              Monday\n" +
                "              Tuesday\n" +
                "              Wednesday\n" +
                "              Thursday\n" +
                "              Friday\n" +
                "              Saturday\n" +
                "            weekDayNameAbbreviations\n" +
                "              Sun.\n" +
                "              Mon.\n" +
                "              Tue.\n" +
                "              Wed.\n" +
                "              Thu.\n" +
                "              Fri.\n" +
                "              Sat.\n" +
                "        decimalNumberDigitCount: 8\n" +
                "        decimalNumberSymbols: \n" +
                "          DecimalNumberSymbols\n" +
                "            negativeSign\n" +
                "              '-'\n" +
                "            positiveSign\n" +
                "              '+'\n" +
                "            zeroDigit\n" +
                "              '0'\n" +
                "            currencySymbol\n" +
                "              \"$\"\n" +
                "            decimalSeparator\n" +
                "              '.'\n" +
                "            exponentSymbol\n" +
                "              \"e\"\n" +
                "            groupSeparator\n" +
                "              ','\n" +
                "            infinitySymbol\n" +
                "              \"∞\"\n" +
                "            monetaryDecimalSeparator\n" +
                "              '.'\n" +
                "            nanSymbol\n" +
                "              \"NaN\"\n" +
                "            percentSymbol\n" +
                "              '%'\n" +
                "            permillSymbol\n" +
                "              '‰'\n" +
                "        defaultFormHandler: \n" +
                "          basic\n" +
                "        defaultYear: 2000\n" +
                "        errorFormatter: \n" +
                "          badge-error\n" +
                "            \"text @\"\n" +
                "        exporters: \n" +
                "          collection\n" +
                "          empty\n" +
                "          json\n" +
                "        expressionNumberKind: BIG_DECIMAL\n" +
                "        formatters: \n" +
                "          accounting\n" +
                "          automatic\n" +
                "          badge-error\n" +
                "          collection\n" +
                "          currency\n" +
                "          date\n" +
                "          date-time\n" +
                "          default-text\n" +
                "          expression\n" +
                "          full-date\n" +
                "          full-date-time\n" +
                "          full-time\n" +
                "          general\n" +
                "          hyperlinking\n" +
                "          long-date\n" +
                "          long-date-time\n" +
                "          long-time\n" +
                "          medium-date\n" +
                "          medium-date-time\n" +
                "          medium-time\n" +
                "          number\n" +
                "          percent\n" +
                "          scientific\n" +
                "          short-date\n" +
                "          short-date-time\n" +
                "          short-time\n" +
                "          text\n" +
                "          time\n" +
                "        formattingConverter: \n" +
                "          collection\n" +
                "            \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\"\n" +
                "        formattingCurrencyExchangeRater: \n" +
                "          storage-properties\n" +
                "            \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "        formattingFunctions: \n" +
                "        formHandlers: \n" +
                "        formulaConverter: \n" +
                "          collection\n" +
                "            \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\"\n" +
                "        formulaCurrencyExchangeRater: \n" +
                "          storage-properties\n" +
                "            \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "        formulaFunctions: \n" +
                "        functions: \n" +
                "        importers: \n" +
                "          collection\n" +
                "          empty\n" +
                "          json\n" +
                "        locale: en_AU (java.util.Locale)\n" +
                "        numberFormatter: \n" +
                "          number\n" +
                "            \"0.#;0.#;0\"\n" +
                "        numberParser: \n" +
                "          number\n" +
                "            \"0.#;0.#;0\"\n" +
                "        parsers: \n" +
                "          date\n" +
                "          date-time\n" +
                "          full-date\n" +
                "          full-date-time\n" +
                "          full-time\n" +
                "          general\n" +
                "          long-date\n" +
                "          long-date-time\n" +
                "          long-time\n" +
                "          medium-date\n" +
                "          medium-date-time\n" +
                "          medium-time\n" +
                "          number\n" +
                "          short-date\n" +
                "          short-date-time\n" +
                "          short-time\n" +
                "          time\n" +
                "          whole-number\n" +
                "        plugins: \n" +
                "        precision: 7\n" +
                "        queryConverter: \n" +
                "          collection\n" +
                "            \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, properties, spreadsheet-metadata, style, text-node, template, net, binary, optional-to, collection-to)\"\n" +
                "        queryFunctions: \n" +
                "        roundingMode: HALF_UP\n" +
                "        scriptingConverter: \n" +
                "          collection\n" +
                "            \"(null-to-number, simple, text, boolean, number, date-time, environment, locale, value, error-throwing, color, expression, json, currency, logging, plugins, properties, spreadsheet-metadata, storage, style, text-node, template, net, binary, optional-to, collection-to)\"\n" +
                "        scriptingCurrencyExchangeRater: \n" +
                "          storage-properties\n" +
                "            \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "        scriptingFunctions: \n" +
                "        showFormulaEditor: true\n" +
                "        showFormulas: false\n" +
                "        showGridLines: true\n" +
                "        showHeadings: true\n" +
                "        sortComparators: \n" +
                "          [background-color, border-bottom-color, border-color, border-left-color, border-right-color, border-top-color, color, currency, custom-list, custom-list-case-insensitive, date, datetime, day-of-month, day-of-year, formatter, hour-of-ampm, hour-of-day, locale, minute-of-hour, month-of-year, nano-of-second, number, outline-color, parser, seconds-of-minute, text, text-case-insensitive, time, validator, value-type, year]\n" +
                "        sortConverter: \n" +
                "          collection\n" +
                "            \"(null-to-number, simple, text, boolean, number, date-time, locale, value, optional-to, collection-to)\"\n" +
                "        style: \n" +
                "          TextStyle\n" +
                "            height=50px\n" +
                "            width=100px\n" +
                "        textFormatter: \n" +
                "          text\n" +
                "            \"@\"\n" +
                "        timeFormatter: \n" +
                "          time\n" +
                "            \"hh:mm:ss\"\n" +
                "        timeParser: \n" +
                "          time\n" +
                "            \"hh:mm:ss\"\n" +
                "        twoDigitYear: 50\n" +
                "        validationConverter: \n" +
                "          collection\n" +
                "            \"(null-to-number, simple, text, boolean, number, date-time, environment, value, error-throwing, expression, form-and-validation, locale, logging, plugins, properties, template, json, optional-to, collection-to)\"\n" +
                "        validationCurrencyExchangeRater: \n" +
                "          storage-properties\n" +
                "            \"(\\\"/samples/CurrencyExchangeRates.properties\\\")\"\n" +
                "        validationFunctions: \n" +
                "        validationValidators: \n" +
                "          absolute-url\n" +
                "          checkbox\n" +
                "          choice-list\n" +
                "          collection\n" +
                "          email-address\n" +
                "          expression\n" +
                "          non-null\n" +
                "          text-length\n" +
                "          text-mask\n" +
                "        validators: \n" +
                "          absolute-url\n" +
                "          checkbox\n" +
                "          choice-list\n" +
                "          collection\n" +
                "          email-address\n" +
                "          expression\n" +
                "          non-null\n" +
                "          text-length\n" +
                "          text-mask\n" +
                "        valueSeparator: ','\n"
        );
    }

    // class............................................................................................................

    @Override
    public void testTypeNaming() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<SpreadsheetEnvironmentContextSpreadsheetMetadata> type() {
        return SpreadsheetEnvironmentContextSpreadsheetMetadata.class;
    }
}
