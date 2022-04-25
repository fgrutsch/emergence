# eMERGEnce

[![Maven](https://img.shields.io/maven-central/v/com.fgrutsch.emergence/core_3.svg?logo=Apache%20Maven&style=for-the-badge)](https://search.maven.org/search?q=g:%22com.fgrutsch.emergence%22%20AND%20a:%22core_3%22)
[![Github Actions CI Workflow](https://img.shields.io/github/workflow/status/fgrutsch/emergence/CI/main?logo=Github&style=for-the-badge)](https://github.com/fgrutsch/emergence/actions/workflows/ci.yml?query=branch%3Amain)
[![Codecov](https://img.shields.io/codecov/c/github/fgrutsch/emergence/main?logo=Codecov&style=for-the-badge)](https://codecov.io/gh/fgrutsch/emergence)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge)](https://opensource.org/licenses/Apache-2.0)
[![Docker Pulls](https://img.shields.io/docker/pulls/fgrutsch/emergence.svg?logo=Docker&style=for-the-badge)](https://hub.docker.com/r/fgrutsch/emergence)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=for-the-badge&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

eMERGEnce is a bot that helps you to get rid of all your emerged pull requests automatically based on configurable conditions.

## Documentation

The full documentation can be found [here](https://emergence.fgrutsch.com).

## Quickstart

To install eMERGEnce as an executable you can use coursier's [install](https://get-coursier.io/docs/cli-install) command. The only thing you have to do is to run:

`coursier install --channel http://coursier.fgrutsch.com emergence`

To get a list of available options run: `emergence --help` and you should get the following output:

```
Usage: emergence --config <path> --vcs-type <bitbucket-cloud> --vcs-api-host <uri> --vcs-login <string> --git-ask-pass <path> [--repo-config-name <string>]

eMERGEnce <version>

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

An example command might look like this (depending on the selected VCS type):

```bash
emergence \
    --config "/opt/emergence/run-config.yml" \
    --vcs-type "bitbucket-cloud" \
    --vcs-api-host "https://api.bitbucket.org/2.0" \
    --vcs-login ${BITBUCKET_USERNAME} \ 
    --git-ask-pass "/opt/emergence/git-ask-pass.sh"
```

For full documentation and how to run it via docker please check the link above.

## Credits

The code of this project is heavily inspired and based on [fthomas's](https://github.com/fthomas) awesome [scala-steward](https://github.com/scala-steward-org/scala-steward) project. Go check it out!

## Contributors

* [Fabian Grutsch](https://github.com/fgrutsch)

## License

This code is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0.txt).