# Releasing

## Prerequesites
- to do a release, you must know what module, with what [version](https://semver.org/) you should use.
- to prepare or perform a adobe-dx release, you must have write access to that repository, and a [token that has write access to it](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages)
- each and every release must be signed, so you need to have GPG key (https://help.github.com/en/github/authenticating-to-github/generating-a-new-gpg-key)
- once done, you can add in your `~/.m2/settings.xml` following profile
```
...
<profiles>
    <profile>
        <id>dx-release</id>
        <properties>
            <gpg.keyname><!--your public key--></gpg.keyname>
            <gpg.passphrase><!--encrypted gpg passphrase--></gpg.passphrase>
        </properties>
    </profile>
</profiles>
```
- note that depending on your OS, you may hit https://issues.apache.org/jira/browse/MGPG-59 which is plugin's gpg agent trying 
to access IO. Message being something like 

```gpg: signing failed: Inappropriate ioctl for device```

As proposed in the ticket, in order to counter balance this, pls execute following command _before_ preparing release:

```gpg -u <your key's email> --use-agent --armor --detach-sign --output $(mktemp) pom.xml```

## Release

### commands

1. check that your local is up to date with origin,
2. check that a release would work by running a dry run release
```mvn release:prepare -Pdx-release -DdryRun=true```
if things looks good to you, you can rollback all changes
```mvn release:rollback -Pdx-release -DdryRun=true```
3. prepare the release 
```mvn release:prepare -Pdx-release```
4. perform the release
```mvn release:perform -Pdx-release```
5. (if anything goes wrong) rollback the release
```mvn release:rollback -Pdx-release``` 
6. (if things went well) go to https://github.com/adobe/adobe-dx/packages 
and check there released artifacts. Eventually do an announce / gh release if important one.