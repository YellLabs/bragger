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

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# template files to be rendered
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
env.custom_config_files = [
    { "source": "fabric/hibu.sbt.template"           , "dest": "hibu.sbt"},
    { "source": "fabric/publish_credentials.template", "dest": "publish_credentials"}
]

# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# variables required to render local settings
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
env.settings_vars = [
    "artifactory_host",
    "artifactory_password",
    "custom_config_files"
]

@runs_once
def templates(data):
    load_extdata(data)
    env.artifactory_host = extlookup("artifactory_host")
    env.artifactory_password = extlookup("project_placesapi_artifactory_password")
    utils.render_custom_templates(".", env.settings_vars, False)

   