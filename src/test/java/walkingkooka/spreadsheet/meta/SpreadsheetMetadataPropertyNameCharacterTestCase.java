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
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.text.CharSequences;

public abstract class SpreadsheetMetadataPropertyNameCharacterTestCase<N extends SpreadsheetMetadataPropertyNameCharacter> extends SpreadsheetMetadataPropertyNameTestCase<N, Character> {

    SpreadsheetMetadataPropertyNameCharacterTestCase() {
        super();
    }

    @Test
    public final void testAllControlCharactersFails() {
        for (int i = Character.MIN_VALUE; i < 0x20; i++) {
            this.checkValueFails2((char) i);
        }
    }

    @Test
    public final void testAllWhitespaceFails() {
        this.checkValueFails2(Character::isWhitespace);
    }

    @Test
    public final void testAllLettersFails() {
        this.checkValueFails2(Character::isLetter);
    }

    @Test
    public final void testAllDigitsFails() {
        this.checkValueFails2(Character::isDigit);
    }

    private void checkValueFails2(final CharPredicate predicate) {
        for (int i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
            final char c = (char) i;
            if (predicate.test(c)) {
                this.checkValueFails2(c);
            }
        }
    }

    // Metadata negative-sign='\0', Expected Character symbol, not control character, whitespace, letter or digit
    private void checkValueFails2(final char c) {
        this.checkValueFails(
            c,
            "Metadata " + this.createName() + "=" + CharSequences.quoteIfChars(c) + ", Expected Character symbol, not control character, whitespace, letter or digit"
        );
    }

    @Test
    public final void testSemiColon() {
        this.checkValue(';');
    }

    @Test
    public final void testDollarSign() {
        this.checkValue('$');
    }

    @Test
    public final void testDecimalPoint() {
        this.checkValue('.');
    }

    @Test
    public final void testAllSymbols() {
        for (int i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
            final char c = (char) i;
            if (c < 0x20) {
                continue;
            }
            if (Character.isWhitespace(c)) {
                continue;
            }
            if (Character.isLetter(c)) {
                continue;
            }
            if (Character.isDigit(c)) {
                continue;
            }
            this.checkValue(c);
        }
    }

    @Override final Character propertyValue() {
        return '$';
    }

    @Override final String propertyValueType() {
        return Character.class.getSimpleName() + " symbol, not control character, whitespace, letter or digit";
    }
}
