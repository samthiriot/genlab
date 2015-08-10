'''

Takes two parameters in command line: the json file from which to read the inputs from, and the json file write results in.
Prints on standard output the progress of the computation: 0-5 is the internal processing, 6-90 the server process, 91-95 the download of results, 96-100 the local processing of results

'''

# system imports
import logging
import random
import time
import sys
import urllib2
import netCDF4 
import tempfile
import os
import copy
import math
import ssl
import json

# local imports
from dao0_5.RestCall import RestCall, RestDataException, RestServerException
from dao0_5.configRestCall import ConfigRestCall
from cigs2_3.everestInteractions import EverestDataLoader

def writeProgress(v):
    print "PROGRESS %s" % v
    sys.stdout.flush()

def writeError(v):
    print "ERROR %s" % v
    sys.stdout.flush()


class ExecuteASimCase():

    def __init__(self, parameters):

        # store parametrers
        self.parameters = parameters

        # future members
        self.restcall = None

        pass

    def connect(self):
        '''
        connects to Everest. Returns None if success or the error raised. 
        '''
    
        # configure proxy 
        ConfigRestCall.proxyEnabled = self.parameters['proxy_enabled']
        ConfigRestCall.proxyHost = str(self.parameters['proxy_host'])
        ConfigRestCall.proxyPort = self.parameters['proxy_port']
        ConfigRestCall.httpDisableCertificateValidation = self.parameters['server_disable_ssl']
        ConfigRestCall.loggingDebug = self.parameters['debug_rest']

        try :
            # connect
            self.restcall = RestCall(
                    url=str(self.parameters['server_url']),
                    username=str(self.parameters['server_login']),  
                    password=str(self.parameters['server_password'])
                    )

        except Exception as e:
            print e
            return e

        return None

    def runASimulation(self, inputs, outputs):
        '''
        expects inputs to be in the form 
            { 
                'pid': {
                    'attribute_technical_name':'value', 
                    'attribute_techname2':value, 
                    [...]
                }, 
                [...]
                }
        expects outputs to be in the form
            { 
                'Building': {
                    'pid': ['output_tech_name1', 'output_tech_name2', 'output_tech_name3' [...] ],
                    'pid2': ['output_tech_name2', 'output_tech_name2', 'output_tech_name3' [...] ] ,
                    [...]
                },
                'City': {
                    'pid': ['res1']
                }
            }
        '''

        bundleId = None
        simCaseId = None

        try: 
            # create a bundle
            bundle = {}
            bundle["name"]        = "genlab_xp"
            bundle["description"] = "bundle created by genlab for optimization"
            bundle["modelId"]     = str(self.parameters['model_executable_id'])
            try:
                bundleCreated = self.restcall.postCreateBundle(bundle)
                bundleId = bundleCreated['id']
            except:
                writeError("parameter error: wrong model")
                return

            writeProgress("4")

            # create a simcase
            try:
                simCase = {}
                simCase["name"]        = "genlab simcase for optimization"
                simCase["description"] = "simcase created by genlab for optimization"
                simCaseCreated = self.restcall.postCreateSimCaseBundle(bundleId, simCase)
                simCaseId = simCaseCreated['id']
            except:
                writeError("server error: unable to create simcase")
                return

            writeProgress("5")

            # associate the simcase to a scenario (only if provided / requested)
            if self.parameters['model_scenario'] is not None:
                try:
                    self.restcall.putCasesRun(simCaseId, {'scenarioId': scenarioId})
                except:
                    writeError("parameter error: unable to associate with this scenario")
                    return

            # push the parameters that were defined
            for pid, attributes in inputs.items():
                try:
                    self.restcall.putCasesAttributes(
                            simCaseId, 
                            pid, 
                            attributes
                            )
                except: 
                    writeError("parameter error: unable to set %s for %s" % (attributes, pid))
                    return

            # start the simcase
            try:
                infoStart = self.restcall.postCasesRun(simCaseId)
            except:
                writeError("server error: unable to start simcase")
                return

            runId = infoStart['runId']
            runStatus = "inProgress"
            runProgress = 0
            
            # wait for the end of the run
            while runStatus == "inProgress":
                if runProgress < 50:
                    time.sleep(5)
                elif runProgress < 85:
                    time.sleep(1)
                else:
                    time.sleep(0.5)
                progressStatus = self.restcall.getCasesRun(simCaseId)
                runStatus = str(progressStatus["status"])
                runProgress = int(progressStatus["progress"])
                writeProgress(int(round(5 + runProgress*80/100)))

            if runStatus != "finished":
                writeError("model error: error during the simulation")
                return

            # download the resulting file
            writeProgress("91")
            try:
                # install the proxy (if required !)
                if self.parameters['proxy_enabled']:
                    proxy = urllib2.ProxyHandler({
                                'http': str(self.parameters['proxy_host'])+':'+str(self.parameters['proxy_port']),
                                'https': str(self.parameters['proxy_host'])+':'+str(self.parameters['proxy_port'])
                                })
                    opener = urllib2.build_opener(proxy)
                    urllib2.install_opener(opener)
                # download the data
                url = self.parameters['server_download_url'] + "CASE-" + str(simCaseId) + "/dataset.nc?sessionid=" + str(self.restcall.token)
                response = urllib2.urlopen(url)
                cityCDF = response.read()
                # create tmp file
                [fDesc, fPath] = tempfile.mkstemp(suffix='xpplan_'+str(simCaseId))
                # write the content of the distant URL to this file (== download)
                with open(fPath, 'w') as f:
                    f.write(cityCDF)

            except Exception as e:
                writeError("server error: unable to download results: %s" % e)
                return
            
            writeProgress("95")

        finally:
            if simCaseId is not None:
                self.restcall.deleteSimCase(simCaseId)
            if bundleId is not None:
                self.restcall.deleteBundle(bundleId)

        writeProgress("96")

        res = {}
        try:
            # open this file as a netcdf dataset
            netcdfResult = netCDF4.Dataset(fPath,  mode='r',  diskless=False,  persist=False,  format='NETCDF4')
            
            # read the expected outputs
            for entityTypeName, pid2kpis in outputs.items(): 
                res[entityTypeName] = {}
                for pid, kpis in pid2kpis.items():
                    res[entityTypeName][pid] = {}
                    for kpi in kpis:
                        try:
                            # find the corresponding indexes to find data
                            idxPID = list(netcdfResult.variables["PID-"+entityTypeName]).index(pid)
                            # retrieve this value
                            res[entityTypeName][pid][kpi] = list(netcdfResult.groups["Results"].groups[entityTypeName].variables[kpi][:, idxPID])
                        except:
                            writeError("parameter error: unable to analyze result for %s of %s:%s" % (kpi, entityTypeName, pid))
                            return
        finally:
            # close the dataset
            if netcdfResult is not None:
                netcdfResult.close()
            # delete file
            if fDesc is not None:
                os.close(fDesc)
                os.remove(fPath)

        writeProgress("100")

        # return these values
        return res



writeProgress("0")

# decode and check parameters
if len(sys.argv) < 3:
    writeError('usage error: expecting python scriptname.py /tmp/input.json /tmp/output.json')
    quit()

filenameParams = sys.argv[1]
outputsFilename = sys.argv[2]
#if os.path.exists(outputsFilename):
#    writeError('usage error: the output file already exists: %s' % outputsFilename)
#    quit()

writeProgress("1")

# parse this file
data = None
with open(filenameParams, 'r') as fileParams:
    data = json.load(fileParams)

writeProgress("2")

t = ExecuteASimCase(data['parameters'])

connectionResult = t.connect()

if connectionResult is not None:
    # error
    writeError('connection to server failed')
else:
    writeProgress("3")

    result = t.runASimulation(data['inputs'], data['outputs'])

    # print result to file
    with open(outputsFilename, 'w') as fileRes:
        json.dump(result, fileRes, sort_keys=True, indent=4)


'''
parameters = {
    'model_executable_id': 81000,
    'model_scenario': None,
    'server_url': 'https://eifero229.eifer.uni-karlsruhe.de/everest-server/', 
    'server_download_url': 'https://eifero229.eifer.uni-karlsruhe.de/data/export/results/',
    'server_login': "samuel.thiriot@edf.fr",
    'server_password': 'thiriot123',
    'server_disable_ssl': False,
    'proxy_enabled': True,
    'proxy_host': 'pcyvipncp2n.edf.fr',
    'proxy_port': 3128,
    'debug_rest': True
}

inputs = {
    # one building
    '1400003061': {
        # SOFC;PEMFC
        'TechTypeENDFC': ['SOFC'],
        # 1,2,3
        'ControlTypeENDFC': ['1'] 
    }
}
outputs = {
    'Neighborhood': {
        '1200000001': ['Neighborhood_TotAggGHG', 'Neighborhood_AnTotalCost', 'Neighborhood_PointsGreenMarkBUIGM']
    }
}

d = { 'parameters': parameters, 'inputs': inputs, 'outputs': outputs}
print json.dumps(d);

quit();

'''

