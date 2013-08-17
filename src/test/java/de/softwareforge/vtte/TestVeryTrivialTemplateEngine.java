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

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class TestVeryTrivialTemplateEngine
{
    @Test
    public void testNoTemplate()
    {
        final String expected = "Hello, World";
        final Map<String, Object> properties = ImmutableMap.of();
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(expected);

        final String result = vtte.render(properties);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testSimpleTemplate()
    {
        final String template = "Connect to {host}:{port}";
        final Map<String, ?> properties = ImmutableMap.of("host", "foo", "port", 1234);

        final String expected = "Connect to foo:1234";

        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        final String result = vtte.render(properties);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testNullValue()
    {
        final String template = "Connect to {host}:{port}";
        final Map<String, Object> properties = new HashMap<>();
        properties.put("host", "foo");
        properties.put("port", null);

        final String expected = "Connect to foo:";

        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        final String result = vtte.render(properties);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testAbsentValue()
    {
        final String template = "Connect to {host}:{port}";
        final Map<String, ?> properties = ImmutableMap.of("host", "foo");

        final String expected = "Connect to foo:";

        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        final String result = vtte.render(properties);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testOptionalPresent()
    {
        final String template = "Connect to {host}[:{port}]";
        final Map<String, ?> properties = ImmutableMap.of("host", "foo", "port", 1234);

        final String expected = "Connect to foo:1234";

        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        final String result = vtte.render(properties);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testOptionalAbsent()
    {
        final String template = "Connect to {host}[:{port}]";
        final Map<String, ?> properties = ImmutableMap.of("host", "foo");

        final String expected = "Connect to foo";

        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        final String result = vtte.render(properties);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testOptionalNull()
    {
        final String template = "Connect to {host}[:{port}]";
        final Map<String, Object> properties = new HashMap<>();
        properties.put("host", "foo");
        properties.put("port", null);

        final String expected = "Connect to foo";

        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        final String result = vtte.render(properties);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testNestedOptional()
    {
        final String template = "May be[ [left: {left}][right: {right}]]";

        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        Assert.assertEquals("May be", vtte.render(ImmutableMap.<String, String>of()));
        Assert.assertEquals("May be left: left-value", vtte.render(ImmutableMap.of("left", "left-value")));
        Assert.assertEquals("May be right: right-value", vtte.render(ImmutableMap.of("right", "right-value")));
    }

    @Test
    public void testDefaultValue()
    {
        final String template = "Connect to {host}:{port:8080}";
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        Assert.assertEquals("Connect to foobar:8080",vtte.render(ImmutableMap.of("host", "foobar")));
        Assert.assertEquals("Connect to foobar:4815",vtte.render(ImmutableMap.of("host", "foobar", "port", 4815)));
    }

    @Test
    public void testDefaultValueWithColonAndSquares()
    {
        final String template = "Connect to {host:[fe80::1]}:{port:8080}";
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        Assert.assertEquals("Connect to [fe80::1]:8080",vtte.render(ImmutableMap.<String, String>of()));
        Assert.assertEquals("Connect to foobar:4815",vtte.render(ImmutableMap.of("host", "foobar", "port", 4815)));
    }

    @Test
    public void testEscaping()
    {
        final String template = "Connect to \\[\\{value1}]\\{value2} backslash: \\\\";
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        Assert.assertEquals("Connect to [{value1}]{value2} backslash: \\", vtte.render(ImmutableMap.of("value1", "busted", "value2", "detained")));
    }

    @Test
    public void testNewlineAndTab()
    {
        final String template = "Newline: \\nTab: \\t (regular: \n\t)";
        final VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine(template);

        Assert.assertEquals("Newline: \nTab: \t (regular: \n\t)", vtte.render(ImmutableMap.<String, String>of()));
    }

}
