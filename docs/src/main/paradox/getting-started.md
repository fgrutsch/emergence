# Getting Started

You can run eMERGEnce with [Docker](https://www.docker.com/) or with [coursier](https://get-coursier.io/docs/cli-install).

## Docker

To get a list of available options run: `docker run fgrutsch/emergence:latest --help` and you should get the following output:

@@@vars

```
Usage: emergence --config <path> --vcs-type <bitbucket-cloud> --vcs-api-host <uri> --vcs-login <string> --git-ask-pass <path> [--repo-config-name <string>]

eMERGEnce $project.version$

Options and flags:
    --help
        Display this help text.
    --version, -v
        Print the version number and exit.
    --config <path>
        The path to the eMERGEnce run config file.
    --vcs-type <bitbucket-cloud>
        The type of VCS you want to run eMERGEnce.
    --vcs-api-host <uri>
        The base URI for VCS api calls. e.g. https://api.bitbucket.org/2.0
    --vcs-login <string>
        The username for authenticating VCS API calls.
    --git-ask-pass <path>
        The path to the executable script file that returns your VCS secret for authenticating VCS API calls.
    --repo-config-name <string>
        The name/path of the eMERGEnce config file inside the repository. Default: .emergence.yml
```

@@@

An example `docker run` command might look like this (depending on the selected VCS type):

```bash
docker run -v $HOST_DIR:/opt/emergence -it fgrutsch/emergence:latest \
    --config "/opt/emergence/run-config.yml" \
    --vcs-type "bitbucket-cloud" \
    --vcs-api-host "https://api.bitbucket.org/2.0" \
    --vcs-login ${BITBUCKET_USERNAME} \ 
    --git-ask-pass "/opt/emergence/git-ask-pass.sh"
```

## Coursier

To install eMERGEnce as an executable you can use coursier's [install](https://get-coursier.io/docs/cli-install) command. The only thing you have to do is to run:

`coursier install --channel http://coursier.fgrutsch.com emergence`

To get a list of available options run: `emergence --help` and you should get the following output:

@@@vars

```
Usage: emergence --config <path> --vcs-type <bitbucket-cloud> --vcs-api-host <uri> --vcs-login <string> --git-ask-pass <path> [--repo-config-name <string>]

eMERGEnce $project.version$

Options and flags:
    --help
        Display this help text.
    --version, -v
        Print the version number and exit.
    --config <path>
        The path to the eMERGEnce run config file.
    --vcs-type <bitbucket-cloud>
        The type of VCS you want to run eMERGEnce.
    --vcs-api-host <uri>
        The base URI for VCS api calls. e.g. https://api.bitbucket.org/2.0
    --vcs-login <string>
        The username for authenticating VCS API calls.
    --git-ask-pass <path>
        The path to the executable script file that returns your VCS secret for authenticating VCS API calls.
    --repo-config-name <string>
        The name/path of the eMERGEnce config file inside the repository. Default: .emergence.yml
```

@@@

An example command might look like this (depending on the selected VCS type):

```bash
emergence \
    --config "/opt/emergence/run-config.yml" \
    --vcs-type "bitbucket-cloud" \
    --vcs-api-host "https://api.bitbucket.org/2.0" \
    --vcs-login ${BITBUCKET_USERNAME} \ 
    --git-ask-pass "/opt/emergence/git-ask-pass.sh"
```

## Notes

Please check the @ref:[Configuration](configuration.md) for the format of the file passed via the `--config` option.

The [--git-ask-pass](https://git-scm.com/docs/gitcredentials) option must be the path to an executable that returns to stdout. For example:

```
#!/bin/sh
echo "my-git-secret"
```