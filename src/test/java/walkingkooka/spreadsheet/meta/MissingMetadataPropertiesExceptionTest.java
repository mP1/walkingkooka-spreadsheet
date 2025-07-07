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
import walkingkooka.collect.set.Sets;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.reflect.ThrowableTesting2;

public final class MissingMetadataPropertiesExceptionTest implements ThrowableTesting2<MissingMetadataPropertiesException> {

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testGetMessage() {
        final MissingMetadataPropertiesException thrown = new MissingMetadataPropertiesException(
            Sets.of(
                SpreadsheetMetadataPropertyName.AUDIT_INFO,
                SpreadsheetMetadataPropertyName.HIDE_ZERO_VALUES,
                SpreadsheetMetadataPropertyName.ROUNDING_MODE
            )
        );

        this.checkEquals(
            "Metadata missing: auditInfo, hideZeroValues, roundingMode",
            thrown.getMessage()
        );
    }

    @Override
    public Class<MissingMetadataPropertiesException> type() {
        return MissingMetadataPropertiesException.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
