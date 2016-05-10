import re
import sys

def convert_lr_string_to_output(variable_str, out_var_delimiter, out_value_delimiter, internal_others, external_others):
    """

    :param variable_str: The variable string in lr output
    :type variable_str: str
    :param out_var_delimiter: delimiter between dimension name and value
    :type out_var_delimiter: str
    :param out_value_delimiter: delimiter between dimension and coefficient
    :type out_value_delimiter: str
    :param internal_others: others value used internally
    :type internal_others: str
    :param external_others: others value to be sent out
    :type external_others: str
    :return: output string in the format
    :rtype: str
    """
    # Find the occurrence of numeric value in the variable, then insert the out_var_delimiter between the dimension and variable value
    m = re.search("\d", variable_str)
    pos = -1
    if m:
        pos = m.start()
    else:
        # This is a quantitative variable
        return variable_str + out_value_delimiter

    dim_name = variable_str[:pos]
    dim_value = variable_str[pos:]
    if dim_value == internal_others:
        dim_value = external_others
    res = dim_name + out_var_delimiter + dim_value
    res += out_value_delimiter
    return res

def parse_and_convert(line_str, in_delimiter, intercept_str, out_intercept_str, out_var_delimiter, out_value_delimiter, internal_others, external_others):
    """
    Parses the input line from the output of logistic regression of r and converts to the format required by online system

    :param line_str: 1 input line from the output of logistic regression
    :type line_str: str
    :param in_delimiter: delimiter between values in r output. r output file is a csv, so should be ',' (comma)
    :type in_delimiter: str
    :param intercept_str: string corresponding to the intercept in r output
    :type intercept_str: str
    :param out_intercept_str: string corresponding to the intercept in output of this
    :type out_intercept_str
    :param out_var_delimiter: delimiter between dimension name and value
    :type out_var_delimiter: str
    :param out_value_delimiter: delimiter between dimension and coefficient
    :type out_value_delimiter: str
    :param internal_others: others value used internally
    :type internal_others: str
    :param external_others: others value to be sent out
    :type external_others: str
    :return: tuple containing the dimension name, value couple in the r output followed by line converted to output format
    :rtype: tuple
    """
    # tokenize the line on delimiter
    tokens = line_str.split(in_delimiter)
    variable = tokens[0][1:-1]
    if variable == intercept_str:
        res = out_intercept_str
        res += out_value_delimiter
        res += tokens[1]
        return (None, res)

    res = convert_lr_string_to_output(variable, out_var_delimiter, out_value_delimiter, internal_others, external_others)
    res += tokens[1]
    return (variable, res)


def read_file_and_convert(in_file_name, all_factors_file_name, lines_to_ignore, intercept_str, out_intercept_str, in_delimiter, out_var_delimiter, out_value_delimiter, internal_others, external_others):
    """
    Reads the input file name and converts to the format required by online systems and returns the result

    :param in_file_name: input file, i.e., output of r
    :type in_file_name: str
    :param lines_to_ignore: lines to be ignored at the beginning, since the csv output contains HEADER
    :type lines_to_ignore: int
    :param intercept_str: string corresponding to the intercept in r output
    :type intercept_str: str
    :param out_intercept_str: string corresponding to the intercept in output of this
    :type out_intercept_str
    :param in_delimiter: delimiter between values in r output. r output file is a csv, so should be ',' (comma)
    :type in_delimiter: str
    :param out_var_delimiter: delimiter between dimension name and value
    :type out_var_delimiter: str
    :param out_value_delimiter: delimiter between dimension and coefficient
    :type out_value_delimiter: str
    :param internal_others: others value used internally
    :type internal_others: str
    :param external_others: others value to be sent out
    :type external_others: str
    :return: contents of the file transformed into the format used by online systems
    :rtype: str
    """
    in_file = None
    all_factors_file = None

    res = ''
    variableMap = {}
    try:
        in_file = open(in_file_name, 'r')
        counter = 0
        for line in in_file:
            if counter < lines_to_ignore:
                counter += 1
                continue

            if line == None or line.strip() == '':
                continue

            variable, res_str = parse_and_convert(line, in_delimiter, intercept_str, out_intercept_str, out_var_delimiter, out_value_delimiter, internal_others, external_others)
            if variable is not None:
                variableMap[variable] = 1
            res += res_str
            if res is None:
                continue

            res += '\n'

        # If all factors file is not available, that implies that there's not categorical variable in the logistic
        # regression. Catch the exception and continue as if everything is normal and under control
        all_factors_file = open(all_factors_file_name, 'r')
        # Iterate through all the remaining factors
        for factor_name in all_factors_file:
            factor_name = factor_name.strip()
            if factor_name not in variableMap:
                # Dump a 0 for the value if not present in regression output
                output_variable_str = convert_lr_string_to_output(factor_name, out_var_delimiter, out_value_delimiter, internal_others, external_others)
                res += output_variable_str
                res += '0.0'
                res += '\n'

    except:
        # No file found. This means that the csv would have been empty or have a row count of 1
        pass
    finally:
        if in_file is not None:
            in_file.close()
        if all_factors_file is not None:
            all_factors_file.close()

    return res

if __name__ == '__main__':
    in_file_name = sys.argv[1]
    all_factors_file_name = sys.argv[2]
    lines_to_ignore = 1
    intercept_str = '(Intercept)'
    out_intercept_str = 'intercept'
    out_file_name = 'outfile.local'
    in_delimiter = ','
    out_var_delimiter = '_'
    out_value_delimiter = u'\x01'
    internal_others = '9999999997'
    external_others = '-1'

    res_str = read_file_and_convert(in_file_name, all_factors_file_name, lines_to_ignore, intercept_str, out_intercept_str, in_delimiter, out_var_delimiter, out_value_delimiter, internal_others, external_others)
    fw = open(out_file_name, 'w')
    fw.write(res_str)
    fw.close()
