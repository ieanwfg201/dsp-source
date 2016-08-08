import base64
import sys
import traceback
import glob

from data_structs.AdservingRequestResponse.ttypes import *
from data_structs.PostImpressionRequestResponse.ttypes import *
from optparse import OptionParser
from thrift.protocol import TBinaryProtocol
from thrift.transport import TTransport
from signal import signal, SIGPIPE, SIG_DFL

def parseLogLine(line) :
    """ base64 decode the line """
    decodedLine = base64.b64decode(line)
    # Deserialize using binary protocol
    transport = TTransport.TMemoryBuffer(decodedLine)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    #requestResponse = AdservingRequestResponse()
    requestResponse = AdservingRequestResponse()
    requestResponse.read(protocol)
    return requestResponse

def parsePostLogLine(line) :
    """ base64 decode the line """
    decodedLine = base64.b64decode(line)
    # Deserialize using binary protocol
    transport = TTransport.TMemoryBuffer(decodedLine)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    #requestResponse = AdservingRequestResponse()
    requestResponse = PostImpressionRequestResponse()
    requestResponse.read(protocol)
    return requestResponse

def encode(requestResponse) :
    transport = TTransport.TMemoryBuffer()
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    requestResponse.write(protocol)
    byteArray = transport.getvalue()
    encoded = base64.b64encode(byteArray)
    return encoded

def parseAndPrintPostLogLine(line):
    line = line.strip()
    if line is '':
        return
    try:
        requestResponse = parsePostLogLine(line)
        print requestResponse
    except:
        print('******MALFORMED LINE******:' + line.strip())

def parseAndPrintAdsLogLine(line):
    line = line.strip()
    if line is '':
        return
    try:
        requestResponse = parseLogLine(line)
        print requestResponse
    except Exception:
        print('******MALFORMED LINE******:' + line.strip())

def main():
    signal(SIGPIPE,SIG_DFL) 
    usage = "usage: %prog [options] arg1 arg2 ..."
    parser = OptionParser(usage=usage)
    parser.add_option("-a", "--ads", action="store_true", dest="ads", help="If the logs are adserving logs, use this. This is default")
    parser.add_option("-p", "--post", action="store_true", dest="post", help="If the logs are post impression logs, use this")
    (options, args) = parser.parse_args()
#print "options ads = " + str(options.ads)
#print "options post = " + str(options.post)
    if options.ads and options.post:
        parser.error("ads and post are mutually exclusive. Specify only one. If you don't specify anything, it assumes ads")

    if options.post:
        if len(args) == 0:
            for line in sys.stdin:
                parseAndPrintPostLogLine(line)
        else:
            for fileRegex in args:
                filesToProcess = glob.glob(fileRegex)
                for filename in filesToProcess:
                    with open(filename) as f:
                        for line in f:
                            parseAndPrintPostLogLine(line)
    else:
        if len(args) == 0:
            for line in sys.stdin:
                parseAndPrintAdsLogLine(line)
        else:
            for fileRegex in args:
                filesToProcess = glob.glob(fileRegex)
                for filename in filesToProcess:
                    with open(filename) as f:
                        for line in f:
                            parseAndPrintAdsLogLine(line)

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        pass
    except Exception:
        traceback.print_exc(file=sys.stdout)

