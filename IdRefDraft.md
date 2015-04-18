Due to some inconsistencies in RAW token names, we'll have to "manually" link some ids and refs together.

For example, while defining armor, `ITEM_ARMOR` container is used. Referencing it from entity or reaction will use `ARMOR` token/category. I propose a simple "map" tag that will link these together:

```
<id name="ITEM_SUB_ID" from="ITEM_ARMOR" to="ARMOR" category="ITEM_ID"/>
<id name="ITEM_SUB_ID" from="ITEM_TOY" to="TOY" category="ITEM_ID"/>
```

`ITEM_ID` is now a placeholder for `ARMOR` and `TOY` (in other words, the destination name, or content, of the `category` attribute)

Some tokens, like `[BODY_DETAIL_PLAN:STANDARD_MATERIALS]` are called the same both in definition and reference. There could be a [COC](http://en.wikipedia.org/wiki/Convention_over_configuration) rule to link these by default.

Another issue is, for example in `[REAGENT:A:1:ARMOR:NONE:METAL:NONE]`, the `ARMOR` argument is not the name of the token. While the `<id`> tag only links tokens and containers, we'll need another flag to say "this argument is a reference to category, a sub category follows".

A simple attribute should fix this problem easily.

```
<a type="string" category="ITEM_ID"/>
```
Means, the next ref argument will be
```
<a type="string" ref="ITEM_SUB_ID"/>
```

Another benefit is, with the category flag, the argument value can be inferred from `to` attribute of `<id>` tag (as in, if the category is "`ITEM_ID`", we can scan all the id definitions belonging to this category and where they point, and this provide user with a list of categories as well)

This is a concern mostly for reactions. I haven't found similar behaviour elsewhere.

For the aforementioned `[BODY_DETAIL_PLAN:STANDARD_MATERIALS]`, a simple `ref="BODY_DETAIL_PLAN_ID"` will suffice (since there is no category above).

By convention, if `ref` attribute is present, it should look to the previous tag's value to infer the "category", if feasable. In the previous example `BODY_DETAIL_PLAN` will be defaulted to the category, since both defining `id` and `ref` attributes are in the token/container with same name.


This will require adding 1 attribute to argument/token/container definition (`category`), and one element `id` with 3 attributes `from`, `to`, and `category`.

It will be possible to rename `category` attribute in `id` element to `id` and in `a` element to `ref`, and treat them the same way as the rest (with the rule that if we're unsure about subcategory, we'll check previous argument `ref/category`). I've used different names to make the article more comprehensive. The examples in "renamed" format will look like this

```
<id name="ITEM_SUB_ID" from="ITEM_ARMOR" to="ARMOR" id="ITEM_ID"/>
<id name="ITEM_SUB_ID" from="ITEM_TOY" to="TOY" id="ITEM_ID"/>

<t name="REAGENT"/>
  ...
  <a type="string" ref="ITEM_ID"/>
  <a type="string" ref="ITEM_SUB_ID"/>
  ...
</t>
```