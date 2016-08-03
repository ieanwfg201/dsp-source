import logging
import pandas
import numpy
import statsmodels.api
import sys
from time import strftime, gmtime

def solveLogisticRegression(csvFileName, outputFileName, intercept, objectiveName, delimiter, internalOthersId, othersId, emptyFile):
    """
    Load the csv file and solve the regression. Output the values for coefficients in the output file.

    :param csvFileName: name of the csv file containing segments and their ctr values
    :type csvFileName: str
    :param outputFileName: name of the output file
    :type outputFileName: str
    :param intercept: float value signifying value of the constant term(intercept)
    :type intercept: float
    :param objectiveName: Name of the dependent variable
    :type objectiveName: str
    :param delimiter: delimiter between dimension value and coefficient
    :type delimiter: str
    :param internalOthersId: Others id to be used internally (Since statsmodels doesn't seem to support negative id)
    :type internalOthersId: long
    :param othersId: Others id to be exposed
    :type othersId: int
    :param emptyFile: 0 if empty, 1 if not empty
    :type emptyFile: int
    """
    appLogger = logging.getLogger(__name__)

    if emptyFile == 0:
        # Empty csv file. No need to do anything
        outputFile = None
        try:
            outputFile = open(outputFileName, 'w')
        finally:
            outputFile.close()
            return

    # Read in the data
    csvData = pandas.read_csv(csvFileName)
    # Look at the dimensions(columns) in the data and binarize the data for each dimension
    data = csvData[[objectiveName]]
    for column in csvData.columns:
        if column != objectiveName:
            # Dummify the data
            dummyData = pandas.get_dummies(csvData[column], prefix=column)
            data = data.join(dummyData.ix[:, 1:])

    # Get the dictionary from data
    data_dict = data.to_dict()
    # Iterate over the dictionary and add identity matrix to the end (only those rows that are missing)
    good_dims = {}
    num_rows = 0
    dim_names = data_dict.keys()
    if len(dim_names) != 0:
        num_rows =  len(data_dict[dim_names[0]])

    if num_rows != 0:
        for i in range(0, num_rows):
            non_zero_count = 0
            non_zero_dim = ''
            for dim_name in dim_names:
                if data_dict[dim_name][i] != 0:
                    non_zero_count += 1
                    non_zero_dim = dim_name
                if non_zero_count > 1:
                    break
            if non_zero_count == 1:
                good_dims[non_zero_dim] = 1

    for i in range(0, len(dim_names)):
        dim_name = dim_names[i]
        if dim_name in good_dims:
            continue
        for j in range(0, len(dim_names)):
            if i == j:
                data_dict[dim_names[j]][num_rows] = 1
            else:
                data_dict[dim_names[j]][num_rows] = 0
        num_rows += 1

    data = pandas.DataFrame.from_dict(data_dict)

    if intercept is not None:
        data['intercept'] = intercept

    # Columns to be used for training
    trainCols = []
    for column in data.columns:
        if column != objectiveName:
            trainCols.append(column)

    if len(trainCols) == 0:
        outputFile = None
        try:
            outputFile = open(outputFileName, 'w')
        finally:
            outputFile.close()
            return

    logit = statsmodels.api.Logit(data[objectiveName], data[trainCols])
    result = logit.fit()

    outputFile = None

    try:
        # output the coefficient values in the output file
        outputFile = open(outputFileName, 'w')
        for dim, coeff in result.params.iteritems():
            dimStr = str(dim).replace(str(internalOthersId), str(othersId))
            outputFile.write(dimStr)
            outputFile.write(delimiter)
            outputFile.write(str(coeff))
            outputFile.write('\n')
    except Exception, e:
        appLogger.info('Run failed at %s %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()), e)
        sys.exit(1)
    finally:
        if outputFile is not None:
            outputFile.close()

    appLogger.info('Run succeeded at %s', strftime("%Y-%m-%d %H:%M:%S", gmtime()))
