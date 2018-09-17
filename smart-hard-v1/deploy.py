from fabric.api import *
import os

keyFileName = "aws-key-us-ad.pem"
env.hosts="127.0.0.1"
env.user = 'ubuntu'
env.key_filename = keyFileName
services = {
    "sh":{
        "host":"35.162.249.93",
        "remotePath":"/data/smart-hard",
        "localFile":"dist/dist/smart-hard-v1.jar"
    }
}
def cp2Server(localFile,remotePath):
    put(localFile,remotePath)
    with cd(remotePath):
        run("sudo sh init_deploy.sh")

def deploy():
    os.system("ant -f jar.xml")
    with settings(parallel=True, host_string=services["sh"]['host']):
        cp2Server(services["sh"]['localFile'],services["sh"]['remotePath'])
def restart():
    with settings(parallel=True, host_string=services["sh"]['host']):
        with cd(services["sh"]['remotePath']):
            run("sudo sh init_restart.sh")
def jar():
    os.system("ant -f jar.xml")

