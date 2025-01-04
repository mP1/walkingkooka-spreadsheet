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
import walkingkooka.environment.EnvironmentContextTesting2;
import walkingkooka.environment.EnvironmentValueName;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class SpreadsheetMetadataEnvironmentContextTest implements EnvironmentContextTesting2<SpreadsheetMetadataEnvironmentContext>,
        ToStringTesting<SpreadsheetMetadataEnvironmentContext> {

    @Test
    public void testWithNullFails() {
        assertThrows(
                NullPointerException.class,
                () -> SpreadsheetMetadataEnvironmentContext.with(null)
        );
    }

    @Test
    public void testEnvironmentValueWithoutPrefix() {
        this.environmentValueAndCheck(
                EnvironmentValueName.with("missing.prefix")
        );
    }

    @Test
    public void testEnvironmentValueWithPrefixButUnknown() {
        this.environmentValueAndCheck(
                EnvironmentValueName.with("spreadsheet.missing")
        );
    }

    @Test
    public void testEnvironmentValue() {
        this.environmentValueAndCheck(
                EnvironmentValueName.with("spreadsheet." + SpreadsheetMetadataPropertyName.DECIMAL_SEPARATOR),
                '.'
        );
    }

    @Override
    public void testUser() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpreadsheetMetadataEnvironmentContext createContext() {
        return SpreadsheetMetadataEnvironmentContext.with(
                SpreadsheetMetadataTesting.METADATA_EN_AU
        );
    }

    // toString.........................................................................................................

    @Test
    public void testToString() {
        this.toStringAndCheck(
                this.createContext(),
                SpreadsheetMetadataTesting.METADATA_EN_AU.toString()
        );
    }

    // TypeName.........................................................................................................

    @Override
    public String typeNameSuffix() {
        return EnvironmentContext.class.getSimpleName();
    }

    @Override
    public Class<SpreadsheetMetadataEnvironmentContext> type() {
        return SpreadsheetMetadataEnvironmentContext.class;
    }
}
