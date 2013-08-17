/*
 * Copyright (C) 2013 Henning Schmiedehausen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.softwareforge.vtte;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Strings;

public class TestShortenString
{
    @Test
    public void testSimpleShorten()
    {
        final String longString = Strings.repeat("ab", 200) + "some error here: {xxx}E" + Strings.repeat("cd", 300);

        final int sIndex = longString.indexOf('s');
        final int eIndex = longString.indexOf('E');

        final String result = VeryTrivialTemplateEngine.shortenString(longString, 0, sIndex, eIndex);

        Assert.assertEquals("...abababababab*some error here: {xxx}*Ecdcdcdcdcdc...", result);
    }

    @Test
    public void testLongField()
    {
        final String longString = Strings.repeat("ab", 200) + " --> S <-- " + Strings.repeat("cd", 300) + " --> E <-- " + Strings.repeat("ef", 400);

        final int sIndex = longString.indexOf('S');
        final int eIndex = longString.indexOf('E') + 1;

        final String result = VeryTrivialTemplateEngine.shortenString(longString, 0, sIndex, eIndex);

        Assert.assertEquals("...bababab --> *S <-- cdcdcd...cdcdcd --> E* <-- efefefe...", VeryTrivialTemplateEngine.shortenString(longString, 0, sIndex, eIndex));
    }

}
