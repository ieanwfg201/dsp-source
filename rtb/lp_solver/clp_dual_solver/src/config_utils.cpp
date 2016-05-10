#include "config_utils.h"
#include "libconfig.h++"
#include "constants.h"
using namespace std;
using namespace libconfig;
using namespace lp_constants;

// Reads the config file and returns the config parameters read
// from that file
config_params read_config_file(const string& filename, const string& mps_file_path, const string& output_file_path) {
    Config config;
    config.readFile(filename.c_str());

    config_params params;

    config.lookupValue(optimization_direction_key, params.optimization_direction);
    // config.lookupValue(mps_file_key, params.mps_file);
    // config.lookupValue(output_file_key, params.output_file);
    params.mps_file = mps_file_path;
    params.output_file = output_file_path;

    return params;
}

/*
int main() {
    read_config_file(config_file_path);

    return 0;
}
*/
