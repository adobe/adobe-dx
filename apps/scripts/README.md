# Scripts 

this module allows to execute content script on an instance

# pipes.sh client
once below content package is installed, for easier usage, you can symlink ./pipe.sh 
e.g. `ln -s ~/Documents/dx/scripts/pipe.sh /usr/local/bin/pipe`

then you can run pipe from your terminal like
```pipe "echo /content | $ foo/bar"```

or

```pipe ~/Document/myPipeScript.txt```

where myPipeScript.txt contains pipe instruction (see https://sling.apache.org/documentation/bundles/sling-pipes.html for further information)

# app content-package
this package contains pipes and its dependencies.
## pipe utilities`
There are also utility pipes available under `/apps/dx/scripts/libs`
all parameters that are quoted below are bindings to be used with the pipe.
### backup
pls use that pipe with additional binding `search` that is the path of a pipe used for searching.
It will be backed up at `backup` path
### execute
pls use that pipe with additional binding `search` that is the path of a pipe used for searching.
It will be then execute `exec` pipe on each search result
### fix-invalidpages
needs two bindings: `app` and `pageType`. Finds pages in /content/{app} and /content/experience-fragments/{app} that don't have a jcr:content child,
and fix them with empty jcr:content child, with only `sling:resourceType` property, set to XF generic type or for the pages in the
content tree, `pageType` that must be added in the bindings
### replace
pls use that pipe as a reference, specifying in bindings a `property` and `oldValue` you want to replace, plus `newValue`
### seek-and-destroy
hat pipes searches for resources under `root` of a given `type` and removes them
### traverse
Traverses pages in /content/{app} and /content/experience-fragments/{app}, considering there is a country and language sub structure.
This pipe is meant to be used by others (it is used by above fix-invalid-page)
### usage
generates a report under /var/pipes/libs/usage/report of used types
