import base64

from data.AdservingRequestResponse.ttypes import *
from thrift.protocol import TBinaryProtocol
from thrift.transport import TTransport

def parseLogLine(line) :
    """ base64 decode the line """
    decodedLine = base64.b64decode(line)
    # Deserialize using binary protocol
    transport = TTransport.TMemoryBuffer(decodedLine)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    requestResponse = AdservingRequestResponse()
    requestResponse.read(protocol)
    return requestResponse

def encode(requestResponse) :
    transport = TTransport.TMemoryBuffer()
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    requestResponse.write(protocol)
    byteArray = transport.getvalue()
    encoded = base64.b64encode(byteArray)
    return encoded


if __name__ == '__main__':
    line = 'CAABAAAAAQsAAgAAACQwMTFjM2U4NC1lY2Q1LWQ1MDEtNDNkNC0yZmEyMTkwMDAwMDEIAAMAAAACCAAEAAAAAQgABgAAAAEKAAgAAAAAAABf1QgACQAAAA8IAAoAAABMCAALAAAABgsADwAAABVXZ2V0LzEuMTQgKGxpbnV4LWdudSkLABAAAAALMTE3Ljk3Ljg3LjYNABMLCwAAAAIAAAAHc2l0ZS1pZAAAAA50ZXN0X3NpdGVfZ3VpZAAAAAJ1YQAAABRBcHBsZS1pUGhvbmUvNTAxLjM0NwA='
    #line = 'CAABAAAAAQsAAgAAAAlyZXF1MjM2NzgIAAMAAAABCgAEAAAAAFLh+vQLAAUAAAAHc2l0ZTEyMw8ABgYAAAADM0sAewLzCgAHAAAAAAAAAVkIAAgAAAKmCAAJAAAADAgACgAAAHsIAAsAAATSCwAMAAAAAlVBCwANAAAACTEuMzguMzguMQsADgAAAAJmZg8ADwwAAAABCAABAAAAAQsAAgAAAARJbXAxCgADAAAAAAAAADsKAAQAAAAAAAAAEAAGABED2wYAEgAKBgATAAoA'
    requestResponse = parseLogLine(line)
    print requestResponse

    #impressions = requestResponse.impressions
    #impressions[0].adId = 59
    #impressions[0].campaignId = 16
    #print impressions

    #s = encode(requestResponse)
    #print s
