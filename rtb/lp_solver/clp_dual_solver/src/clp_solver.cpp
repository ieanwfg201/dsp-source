#include "clp_solver.h"

#include <string>
#include <vector>
using namespace std;

ClpSimplex init_model(const config_params& params, int& status) {
    ClpSimplex model;
    // second parameter to readMps means we've to retain the row and column names
    status = model.readMps(params.mps_file.c_str(), true);
    if(!status) {
        model.setOptimizationDirection(params.optimization_direction);
    }
    return model;
}

int solve(ClpSimplex& model) {
    int status = model.primal();
    // If the solution is infeasible or could not be found due to some reason
    // Report in the log.

    return status;
}

lp_solution get_solution(ClpSimplex& model) {
    lp_solution solution;
    solution.optimal_value = model.objectiveValue();

    // assert(model.lengthNames());
    if(model.lengthNames() == 0) {
        return solution;
    }

    solution.row_names = *(model.rowNames());
    solution.col_names = *(model.columnNames());

    int num_rows = model.numberRows();
    double* row_primal = model.primalRowSolution();
    for(int irow = 0; irow < num_rows; ++irow) {
        solution.row_values.push_back(row_primal[irow]);
    }

    int num_cols = model.numberColumns();
    double* col_primal = model.primalColumnSolution();
    for(int icol = 0; icol < num_cols; ++icol) {
        solution.col_values.push_back(col_primal[icol]);
    }

    return solution;
}

lp_solution solve_lp(const config_params& params, int& status) {
    ClpSimplex model = init_model(params, status);
    if(status) {
        return lp_solution();
    }

    status = solve(model);
    if(status) {
        return lp_solution();
    }

    return get_solution(model);
}
