import os

from setuptools import setup, find_packages

# Utility function to read the README file.
# Used for the long_description.  It's nice, because now 1) we have a top level
# README file and 2) it's easier to type in the README file than to put a raw
# string in below ...
def read(fname):
    return open(os.path.join(os.path.dirname(__file__), fname)).read()

# Generate the thrift python files from data_structs
os.system("thrift --gen py -out data_structs/ data_structs/src/main/thrift/AdStats.thrift")
os.system("thrift --gen py -out data_structs/ data_structs/src/main/thrift/AdservingRequestResponse.thrift")
os.system("thrift --gen py -out data_structs/ data_structs/src/main/thrift/Billing.thrift")
os.system("thrift --gen py -out data_structs/ data_structs/src/main/thrift/PostImpressionRequestResponse.thrift")
os.system("thrift --gen py -out data_structs/ data_structs/src/main/thrift/RecentUserHistory.thrift")
os.system("thrift --gen py -out data_structs/ data_structs/src/main/thrift/UserSegment.thrift")

setup(
    name = "kritter",
    version = "1.0.0",
    author='Kshitij Sooryavanshi',
    author_email='kshitij@kritter.in',
    description='kritter base package',
    license = "BSD",
    keywords = "kritter base",
    url='http://github.com/hirohanin/ysoserious/',
    #packages=['bidder'],
    #packages=find_packages(),
    packages=['data_structs', 'data_structs/AdStats', 'data_structs/AdservingRequestResponse', 'data_structs/Billing', 'data_structs/PostImpressionRequestResponse', 'data_structs/RecentUserHistory', 'data_structs/UserSegment', 'twig', 'twig/adstatscat', 'twig/kritcat'],
    long_description=read('SETUP_README.txt'),
    classifiers=[
    "Development Status :: 3 - Alpha",
    "Topic :: Utilities",
    "License :: OSI Approved :: BSD License",
    ],
    install_requires=['thrift'],
    include_package_data=True,
)
