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
import walkingkooka.color.Color;
import walkingkooka.environment.AuditInfo;
import walkingkooka.net.email.EmailAddress;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.spreadsheet.format.SpreadsheetColorName;

import java.time.LocalDateTime;

public final class SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitorTest extends SpreadsheetMetadataTestCase2<SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor>
        implements SpreadsheetMetadataVisitorTesting<SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor> {

    @Override
    public void testAllConstructorsVisibility() {
    }

    @Override
    public void testIfClassIsFinalIfAllConstructorsArePrivate() {
    }

    @Test
    public void testToString() {
        final SpreadsheetMetadata metadata = SpreadsheetMetadata.EMPTY
                .set(SpreadsheetMetadataPropertyName.AUDIT_INFO,
                        AuditInfo.with(
                                EmailAddress.parse("creator@example.com"),
                                LocalDateTime.MIN,
                                EmailAddress.parse("modified@example.com"),
                                LocalDateTime.MAX
                        )
                ).set(SpreadsheetMetadataPropertyName.numberedColor(1), Color.parse("#123456"))
                .set(SpreadsheetMetadataPropertyName.numberedColor(2), Color.parse("#89abcd"))
                .set(SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.with("apple")), 1)
                .set(SpreadsheetMetadataPropertyName.namedColor(SpreadsheetColorName.with("banana")), 3);
        final SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor visitor = new SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor();

        visitor.accept(metadata);
        this.toStringAndCheck(
                visitor,
                "{1=apple, 3=banana}"
        );
    }

    @Override
    public SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor createVisitor() {
        return new SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor();
    }

    // ClassTesting.....................................................................................................

    @Override
    public Class<SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor> type() {
        return SpreadsheetMetadataNumberToColorNameSpreadsheetMetadataVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    // TypeNameTesting...................................................................................................

    @Override
    public String typeNamePrefix() {
        return SpreadsheetMetadata.class.getSimpleName();
    }
}
