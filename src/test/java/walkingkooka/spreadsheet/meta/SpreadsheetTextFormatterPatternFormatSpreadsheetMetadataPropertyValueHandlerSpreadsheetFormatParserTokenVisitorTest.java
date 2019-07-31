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
import walkingkooka.spreadsheet.format.SpreadsheetTextFormatterPattern;
import walkingkooka.spreadsheet.format.parser.SpreadsheetFormatParserTokenVisitorTesting;
import walkingkooka.type.JavaVisibility;

public final class SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatParserTokenVisitorTest implements SpreadsheetFormatParserTokenVisitorTesting<SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatParserTokenVisitor> {

    @Test
    public void testToString() {
        this.toStringAndCheck(this.createVisitor(), this.name() + "=" + this.pattern());
    }

    @Override
    public SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatParserTokenVisitor createVisitor() {
        return new SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatParserTokenVisitor(this.name(), this.pattern());
    }

    private SpreadsheetMetadataPropertyName<?> name() {
        return SpreadsheetMetadataPropertyName.CREATOR;
    }

    private SpreadsheetTextFormatterPattern pattern() {
        return SpreadsheetTextFormatterPattern.with("#.00");
    }

    @Override
    public Class<SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatParserTokenVisitor> type() {
        return SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandlerSpreadsheetFormatParserTokenVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetTextFormatterPatternFormatSpreadsheetMetadataPropertyValueHandler.class.getSimpleName();
    }
}
