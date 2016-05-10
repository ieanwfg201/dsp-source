#ifndef _CLP_SOLVER_H_
#define _CLP_SOLVER_H_

#include "config_utils.h"
#include "coin/ClpSimplex.hpp"

#include <vector>

class lp_solution {
    public :
        double optimal_value;
        std::vector<std::string> row_names;
        std::vector<double> row_values;
        std::vector<std::string> col_names;
        std::vector<double> col_values;
};

ClpSimplex init_model(const config_params& params, int& status);
int solve(ClpSimplex& model);
lp_solution get_solution(ClpSimplex& model);
lp_solution solve_lp(const config_params& params, int& status);

#endif // _CLP_SOLVER_H_
