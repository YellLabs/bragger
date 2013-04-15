from fabric.api import env, runs_once
import sys, traceback

try:
    from yellfabric import defaults
    from yellfabric.operations import *
    from yellfabric.play import *
except ImportError:
    # We're not being called from a virtual env with yellfabric installed
    print '\nERROR: Could not import yellfabric\n'
    pass

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# global environment settings
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
env.project_name = "play2-bragger"
env.project_version = "1.2.3"

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# template files to be rendered
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
env.custom_config_files = [
    { "source": "fabric/build.sbt.template"          , "dest": "build.sbt"},
    { "source": "fabric/publish_credentials.template", "dest": "publish_credentials"}
]

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# variables required to render local settings
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
env.settings_vars = [
    "project_name",
    "project_version",
    "artifactory_host",
    "artifactory_user",
    "artifactory_password",
    "custom_config_files"
]


@runs_once
def templates(data):
    load_extdata(data)
    env.artifactory_host = extlookup("artifactory_host")
    env.artifactory_user = "play"
    env.artifactory_password = extlookup("project_placesapi_artifactory_password")
    utils.render_custom_templates(".", env.settings_vars, False)

   