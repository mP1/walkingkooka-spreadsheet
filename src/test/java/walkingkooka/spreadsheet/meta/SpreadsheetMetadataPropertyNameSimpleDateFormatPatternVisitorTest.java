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
import walkingkooka.datetime.SimpleDateFormatPatternVisitor;
import walkingkooka.datetime.SimpleDateFormatPatternVisitorTesting;
import walkingkooka.reflect.ClassTesting;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.visit.VisitorTesting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public final class SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitorTest implements SimpleDateFormatPatternVisitorTesting<SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor> {

    @Test
    public void testDateFormatFull() {
        this.dateFormatPatternAndCheck(DateFormat.FULL);
    }

    @Test
    public void testDateFormatLong() {
        this.dateFormatPatternAndCheck(DateFormat.LONG);
    }

    @Test
    public void testDateFormatMedium() {
        this.dateFormatPatternAndCheck(DateFormat.MEDIUM);
    }

    @Test
    public void testDateFormatShort() {
        this.dateFormatPatternAndCheck(DateFormat.SHORT);
    }

    private void dateFormatPatternAndCheck(final int style) {
        this.patternAndCheck((locale -> DateFormat.getDateInstance(style, locale)));
    }

    @Test
    public void testDateTimeFormatFullFull() {
        this.dateTimeFormatPatternAndCheck(DateFormat.FULL, DateFormat.FULL);
    }

    @Test
    public void testDateTimeFormatLongLong() {
        this.dateTimeFormatPatternAndCheck(DateFormat.LONG, DateFormat.LONG);
    }

    @Test
    public void testDateTimeFormatMediumMedium() {
        this.dateTimeFormatPatternAndCheck(DateFormat.MEDIUM, DateFormat.MEDIUM);
    }

    @Test
    public void testDateTimeFormatShortShort() {
        this.dateTimeFormatPatternAndCheck(DateFormat.SHORT, DateFormat.SHORT);
    }

    private void dateTimeFormatPatternAndCheck(final int dateStyle, final int timeStyle) {
        this.patternAndCheck((locale -> DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale)));
    }

    @Test
    public void testTimeFormatFull() {
        this.timeFormatPatternAndCheck(DateFormat.FULL);
    }

    @Test
    public void testTimeFormatLong() {
        this.timeFormatPatternAndCheck(DateFormat.LONG);
    }

    @Test
    public void testTimeFormatMedium() {
        this.timeFormatPatternAndCheck(DateFormat.MEDIUM);
    }

    @Test
    public void testTimeFormatShort() {
        this.timeFormatPatternAndCheck(DateFormat.SHORT);
    }

    private void timeFormatPatternAndCheck(final int style) {
        this.patternAndCheck((locale -> DateFormat.getTimeInstance(style, locale)));
    }

    private void patternAndCheck(final Function<Locale, DateFormat> localeToFormatPattern) {
        for (final Locale locale : Locale.getAvailableLocales()) {

            String pattern = "";
            try {
                final SimpleDateFormat dateFormat = (SimpleDateFormat)localeToFormatPattern.apply(locale);
                pattern = dateFormat.toPattern();
                assertNotEquals("",
                        SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor.pattern(pattern),
                        () -> "" + locale);
            } catch (final UnsupportedOperationException cause) {
                throw new UnsupportedOperationException(pattern + " " + locale.toLanguageTag() + " " + cause.getMessage(), cause);
            }
        }
    }

    @Override
    public SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor createVisitor() {
        return new SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor();
    }

    @Override
    public Class<SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor> type() {
        return SpreadsheetMetadataPropertyNameSimpleDateFormatPatternVisitor.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }

    @Override
    public String typeNamePrefix() {
        return SpreadsheetMetadata.class.getSimpleName();
    }

    @Override
    public String typeNameSuffix() {
        return SimpleDateFormatPatternVisitor.class.getSimpleName();
    }
}
