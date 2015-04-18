## Power Goals ##
### Fully user-modifyable syntax highlighting (partialy done) ###
Users will be able to modify the XML database of keywords and add new when DF format changes. Users will be able to modify the color and text decoration scheme via XML css-like configuration.

Each keyword has a type signature which governs how the token is rendered.

### Autocomplete (partialy done) ###
User will be able to invoke a keystrok which will offer him a list of valid tokens. The list will be dynamicly searched and narrowed as the user type in more letters.

Future features might include learning frequent tags and prioritizing those, as well as some more elaborate patterns.

### DOM ###
As the current approach to HL/AC is best described as "ad-hoc", we should develop a DOM (Document Object Model) similar to that of XML. Editing, validation, HL/AC and crosslinks will be much more effective and easy to implement. Various 3rd party WYSIVYG plugins working with this DOM will be fast and easy to implement.

### Project browser & tabbed editor enviroment ###
User will be able to browse the project files in the tree-like structure (similar to how most IDEs handle project files). Double-clicking the file will open it on new tab (if not already opened), or center user to the already opened tab. User will be easily able to open and close tabs, rearrange them etc.

### Smart links ###
User will be able to navigate between RAW files via hyperlinks. The editor will figure out the relations between several RAW files (like body details and creature raws, buildings and reactions etc.) and link these together.

### Token description library ###
A token definition library. Where the program could give you the function of a specific token (perhaps moused-over or highlighted) and tell you default values, warnings, and other information like that.
(Author: Shukaro)

### Template library ###
A template library. A collection of creature, item, and such entries that have all the important tokens in them, so that if you wanted to make a new creature or something you could just load up the template for it.
(Author: Shukaro)