# File Structure of Config File

Each CRubber file must have a config file in the root directory of the project and should be named `fop.tail`

`fop.tail` file has the following structure:
```declarative
ProjectName("<Project Name>")
Version("<Project Version>")
Description("<Project Description>")
Authors("<Author1>, <Author2>, ...")
AddContact("<For Author>", "<Contact Detail>", "<Contact Type>")
...


AddLanguage("<language1>")
AddLanguage("<language2>")
...

EntryPoint("path/to/entry/from/project/root.ext")

```