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
import walkingkooka.convert.Converters;
import walkingkooka.currency.CurrencyCode;
import walkingkooka.currency.CurrencyCodeLanguageTagContext;
import walkingkooka.locale.LocaleLanguageTag;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.tree.expression.ExpressionNumberKind;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.JsonObject;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContexts;

import java.math.MathContext;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

public final class SpreadsheetMetadataDefaultTextResourceTest implements ClassTesting<SpreadsheetMetadataDefaultTextResource> {

    @Test
    public void testDateTimeOffsetExcelOffset() {
        SpreadsheetMetadata.EMPTY.id();

        final JsonObject resource = JsonNode.parse(new SpreadsheetMetadataDefaultTextResourceProvider().text())
            .objectOrFail();
        final SpreadsheetMetadata metadata = JsonNodeUnmarshallContexts.basic(
                ExpressionNumberKind.DEFAULT,
                new CurrencyCodeLanguageTagContext() {
                    @Override
                    public Optional<Currency> currencyForCurrencyCode(final CurrencyCode currencyCode) {
                        return Optional.ofNullable(
                            Currency.getInstance(
                                currencyCode.value()
                            )
                        );
                    }

                    @Override
                    public Optional<Locale> localeForLanguageTag(final LocaleLanguageTag languageTag) {
                        return Optional.of(
                            Locale.forLanguageTag(
                                languageTag.value()
                            )
                        );
                    }
                },
                MathContext.DECIMAL32
            )
            .unmarshall(resource, SpreadsheetMetadata.class);
        this.checkEquals(
            Converters.EXCEL_1900_DATE_SYSTEM_OFFSET,
            metadata.getOrFail(
                SpreadsheetMetadataPropertyName.DATE_TIME_OFFSET
            ),
            resource::toString
        );
    }

    @Override
    public Class<SpreadsheetMetadataDefaultTextResource> type() {
        return SpreadsheetMetadataDefaultTextResource.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
