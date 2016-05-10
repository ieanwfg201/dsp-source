#include "config_utils.h"
#include "clp_solver.h"
#include "constants.h"
using namespace std;

int main(int argc, char** argv) {
    if(argc != 4) {
        return 1;
    }

    // Read in the config
    string config_file_path = argv[1];
    string mps_file_path = argv[2];
    string outputFileName = argv[3];
    config_params params = read_config_file(config_file_path, mps_file_path, outputFileName);
    int status;
    lp_solution solution = solve_lp(params, status);

    vector<double>& row_values = solution.row_values;
    vector<double>& col_values = solution.col_values;
    vector<string>& row_names = solution.row_names;
    vector<string>& col_names = solution.col_names;

    // Dump the column names and values in the output file
    FILE* fout = fopen(outputFileName.c_str(), "w");
    if(fout == NULL) {
        printf("Unable to open file\n");
        return 1;
    }

    /*
    for(int i = 0; i < row_values.size(); ++i) {
        fprintf("Row number = %d, name = %s, value = %f\n", i, row_names[i].c_str(), row_values[i]);
    }
    */
   
    for(int i = 0; i < col_values.size(); ++i) {
        fprintf(fout, "%s%f\n", col_names[i].c_str(), col_values[i]);
    }

    return 0;
}
