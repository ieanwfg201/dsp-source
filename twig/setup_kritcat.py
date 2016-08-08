import os

from setuptools import setup

# Utility function to read the README file.
# Used for the long_description.  It's nice, because now 1) we have a top level
# README file and 2) it's easier to type in the README file than to put a raw
# string in below ...
def read(fname):
    return open(os.path.join(os.path.dirname(__file__), fname)).read()

# Generate the thrift python files from data_structs
os.system("thrift --gen py -out kritcat/ ../data_structs/src/main/thrift/AdservingRequestResponse.thrift")
os.system("thrift --gen py -out kritcat/ ../data_structs/src/main/thrift/PostImpressionRequestResponse.thrift")

setup(
    name = "kritcat",
    version = "1.0.0",
    author='Kshitij Sooryavanshi',
    author_email='kshitij@kritter.in',
    description='Tool for reading kritter thrift logs.',
    license = "BSD",
    keywords = "thrift log reader",
    url='http://github.com/hirohanin/ysoserious/log_readers/kritcat',
    packages=['kritcat','kritcat/AdservingRequestResponse','kritcat/PostImpressionRequestResponse'],
    long_description=read('README.md'),
    classifiers=[
    "Development Status :: 3 - Alpha",
    "Topic :: Utilities",
    "License :: OSI Approved :: BSD License",
    ],
    install_requires=['thrift'],
    include_package_data=True,
)
