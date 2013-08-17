# Very trivial template engine

VTTE - A Very Trivial Template Engine (pronounced "witty"). 

For the situations where string concatenation and regexp replacement are not enough and adding a fully fledged templating engine is overkill.

## Usage

Add a maven dependency:

    <dependency>
      <groupId>de.softwareforge</groupId>
      <artifactId>vtte</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>

create a new Templating engine:

```java
    VeryTrivialTemplatingEngine vtte = new VeryTrivialTemplatingEngine("Hello, {name}[ {lastName}], welcome to the show.");
```

render strings:

```java
    vtte.render(ImmutableMap.of("name", "John");
```

    == > Hello John, welcome to the show.

```java
    vtte.render(ImmutableMap.of("name", "John", "lastName", "Doe");
```

    == > Hello John Doe, welcome to the show.

## Manual

vtte supports two pieces of syntax: **Property lookups using `{...}`** and **Optional text using `[...]`**. All other characters are copied from the template to the destination as is.

The `\` character can be used to escape `[` and `{` characters. In addition, vtte also supports `\n` for newline and `\t` for tab characters.

### Property lookups

`{key}` is a property lookup. It is looked up in the Map passed in on the `render` method call.

* if the key is not found, the property is replaced with a default value. Unless changed, this is the empty string.
* if the key was found, then
 * if the value in the map is `null`, the property is replaced with the empty string.
 * if the value in the map is a boolean value, the property is replaced with the empty string. If the boolean value was `true`, it triggers inclusion of an optional text group.
 * otherwise, the `toString()` method is invoked on the value and the result replaces the property.

#### Default values

`{key:defaultvalue}` can be used to replace the default value for an absent key. The default value is only used when the key is absent. If the key is present but its value is null, it is still replaced with the empty string.

### Optional text

Any text that is bracketed in `[ ... ]` is an optional text group. Its contents are only included in the output if any of the following conditions is true:

* at least one property lookup in the optional text group has a non null result (**Note:** "" is a non null result)
* at least one absent property in the optional text has a non-empty default value
* at least one property in the optional text has a `true` boolean result.
* at least one nested optional text group is included.

Optional text groups can be nested. 

### Examples

render URIs

```java
    VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine("{scheme:http}://{hostname}[:{port}]/{path}");
    System.out.println(vtte.render(ImmutableMap.<String, Object>of(
        "hostname", "localhost", "path", "/demo")));
    System.out.println(vtte.render(ImmutableMap.<String, Object>of(
        "hostname", "localhost", "port", 8080, "path", "/demo")));
    System.out.println(vtte.render(ImmutableMap.<String, Object>of(
        "scheme", "https"; "hostname", "localhost", "port", 8443, "path", "/demo")));
```

    http://localhost/demo
    http://localhost:8080/demo
    https://localhost:8443/demo

greet users

```java
    VeryTrivialTemplateEngine vtte = new VeryTrivialTemplateEngine("Hello, {firstName}[ {lastName}]!\n[[{firstLogin}This is your first login to the site.][Last login on {lastLoginDate}.]\n]");
    System.out.println(vtte.render(ImmutableMap.<String, Object>of(
        "firstName", "Jane", "firstLogin", true)));
    System.out.println(vtte.render(ImmutableMap.<String, Object>of(
        "firstName", "Jack")));
    System.out.println(vtte.render(ImmutableMap.<String, Object>of(
        "firstName", "John", "lastName", "Doe", "lastLoginDate", date)));
```

    Hello, Jane!
    This is your first login to the site.

    Hello Jack!

    Hello John Doe,
    Last login on January 1st, 2013.
    
### Notes

vtte is fully threadsafe. Multiple threads can render the same time with different properties at the same time.
