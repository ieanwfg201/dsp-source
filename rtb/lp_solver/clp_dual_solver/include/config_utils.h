#ifndef _CONFIG_UTILS_H_
#define _CONFIG_UTILS_H_

#include <string>

class config_params {
    public :
        std::string mps_file;
        int optimization_direction;
        std::string output_file;
};

config_params read_config_file(const std::string& filename, const std::string& mps_file_path, const std::string& output_file_path);

#endif // _CONFIG_UTILS_H_
