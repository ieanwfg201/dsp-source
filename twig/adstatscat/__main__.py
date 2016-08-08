import base64
import sys
import traceback
import glob

from data_structs.AdStats.ttypes import *
from thrift.protocol import TBinaryProtocol
from thrift.transport import TTransport
from signal import signal, SIGPIPE, SIG_DFL

def parseLogLine(line) :
    """ base64 decode the line """
    decodedLine = base64.b64decode(line)
    # Deserialize using binary protocol
    transport = TTransport.TMemoryBuffer(decodedLine)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    adStats = AdStats()
    adStats.read(protocol)
    return adStats

def parseAndPrintLine(line):
    line = line.strip()
    if line is '':
        return
    try:
        adStats = parseLogLine(line)
        print adStats
    except:
        print('******MALFORMED LINE******:' + line.strip())

def main():
    signal(SIGPIPE,SIG_DFL) 

    if len(sys.argv) == 1:
        for line in sys.stdin:
            parseAndPrintLine(line)
    else:
        for fileRegex in sys.argv[1:]:
            filesToProcess = glob.glob(fileRegex)
            for filename in filesToProcess:
                with open(filename) as f:
                    for line in f:
                        parseAndPrintLine(line)

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        pass
    except Exception:
        traceback.print_exc(file=sys.stdout)

