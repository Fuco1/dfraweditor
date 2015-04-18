To those who can't get what the format is about from the "formal" XSD specification, here's a little description (well, to be honest I get it only because I've wrote it :D)

Each RAW language token (a string delimited by "`[:]\n`") can be one of the following:
  * Argument. Simply, the argument delimited by '`:`'. Each argument has a one of the following types:
    * int - any decimal number (\d+)
    * string
    * enum - set of allowed options
    * range - integer min-max range
  * Token is a RAW directive (for example `[ARMORLEVEL:3]` as a whole). It has a name "ARMORLEVEL" and one argument of type int. Change of the name is possible. (token might be understood simply as any lexem of the raw language - as in the usual concept of parsers and lexers)
  * Container, that can hold both another containers and tokens. It can also have arguments on it's own.

The nesting is limited to only 1 level, to prevent deep nesting (which WILL result in unmanagable code) and to simplify the parser. The only exception is the enumeration of enum values, which have to nest at least two levels.

The top-level root element is called "`raws`". Inside it, any number of "`c`" or "`t`" elements may occur.

The simple example of the file:
```
<raws>
  <c name="CONTAINER">
    <t name="TOKEN_INSIDE"/>
  </c>
 
  <t name="TOKEN_INSIDE>
    <d>Description of this token</d>
    <a type="int"/>
  </t>
</raws>
```

The argument element (`a`) can occur in both container tags and token tags.

Each element has a required attribute called "`name`", specifying the name from the RAW format.

Containers and tokens can define a special "anchor" arguments via attributes "`id`" and "`ref`". The main idea is, whenever an argument is specifying the logical name of the entity within the RAW universe, you mark it as `id` with the apropriate ID placeholder. Say, for ITEM\_ARMOR, the placeholder might be ITEM\_SUB\_ID. Then, whenever there's a token that require an armor entity to be specified, it will use the `ref` attribute with the same placeholder ID to cross-reference to the definition of entities of given type. Since all items will use the same ID placeholder, there will be a map specifying the `id` top-level entities to `ref` top-level entities. For example, we link ITEM\_ARMOR and ARMOR tokens to be crossreferenced via ITEM\_SUB\_ID and ITEM\_ID="ARMOR" (ITEM\_ARMOR is used in `[OBJECT:ITEM]` while ARMOR is used in `[OBJECT:ENTITY]`.

This can be better seen in reaction definitions, say:

`[REAGENT:B:150:BAR:NONE:METAL:NONE]`

directive.
The BAR is the top level ITEM\_ID, and any sub level ID can follow. Since on the place of the BAR can be any ITEM\_ID, we can't use specific ITEM\_SUB\_IDs, such as SUB\_ARMOR, SUB\_BAR etc. This has to be infered from the "super" category ID.

(There will possibly be a confusion at this point. Contact me and ask for any details.)

The XSD for the format can be found here: http://code.google.com/p/dfraweditor/source/browse/doc/raws.xsd