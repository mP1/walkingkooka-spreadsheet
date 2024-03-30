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

import java.time.LocalDateTime;

public final class SpreadsheetMetadataTestingTest implements SpreadsheetMetadataTesting {

    @Test
    public void testConverter() {
        this.metadataEnAu()
                .converter();
    }

    @Test
    public void testConverterContext() {
        this.metadataEnAu()
                .converterContext(
                        LocalDateTime::now,
                        (label) -> {
                            throw new UnsupportedOperationException();
                        }
                );
    }

    @Test
    public void testEffectiveStyle() {
        this.metadataEnAu()
                .effectiveStyle();
    }

    @Test
    public void testJsonNodeMarshallContext() {
        this.metadataEnAu().jsonNodeMarshallContext();
    }

    @Test
    public void testJsonNodeUnmarshallContext() {
        this.metadataEnAu().jsonNodeUnmarshallContext();
    }

    @Test
    public void testParser() {
        this.metadataEnAu().parser();
    }

    @Test
    public void testParserContext() {
        this.metadataEnAu()
                .parserContext(LocalDateTime::now);
    }
}
