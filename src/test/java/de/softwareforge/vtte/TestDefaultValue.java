/*
 * Copyright (C) 2013-2014 Henning Schmiedehausen
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
/*
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

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;

public class TestDefaultValue
{
    @Test
    public void testDefaultValue()
    {
        final String template = "Connect to {host}:{port:8080}";
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        assertEquals("Connect to foobar:8080",vtte.render(ImmutableMap.of("host", "foobar")));
        assertEquals("Connect to foobar:4815",vtte.render(ImmutableMap.of("host", "foobar", "port", 4815)));
    }

    @Test
    public void testDefaultValueWithColonAndSquares()
    {
        final String template = "Connect to {host:[fe80::1]}:{port:8080}";
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        assertEquals("Connect to [fe80::1]:8080",vtte.render(ImmutableMap.<String, String>of()));
        assertEquals("Connect to foobar:4815",vtte.render(ImmutableMap.of("host", "foobar", "port", 4815)));
    }


    @Test
    public void testDefaultValueWithNull()
    {
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine("{key:defaultValue}");

        assertEquals("defaultValue", vtte.render(ImmutableMap.<String, String>of()));
        assertEquals("keyValue", vtte.render(ImmutableMap.<String, String>of("key", "keyValue")));
        assertEquals("", vtte.render(singletonMap("key", null)));
    }

    @Test
    public void testDefaultValueWithOptionalNull()
    {
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine("[Key: {key:defaultValue}]");

        assertEquals("Key: defaultValue", vtte.render(ImmutableMap.<String, String>of()));
        assertEquals("Key: keyValue", vtte.render(ImmutableMap.<String, String>of("key", "keyValue")));
        assertEquals("", vtte.render(singletonMap("key", null)));
    }
}
