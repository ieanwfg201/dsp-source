factorise <- function(objective_name, csv_data) {
    col_names <- colnames(csv_data)
    pos <- which(col_names == objective_name)
    col_names <- col_names[-pos]
    csv_data[col_names] <- lapply(csv_data[col_names], factor)
    return(csv_data)
}

factorise_cols <- function(csv_data, factor_cols) {
    csv_data[factor_cols] <- lapply(csv_data[factor_cols], factor)
    return(csv_data)
}

aggregate_given_cols <- function(csv_data, objective_name, col_names) {
    fmla <- as.formula(paste("clicks", "~", paste(paste(col_names, sep=""), collapse="+")))
    fmla1 <- as.formula(paste("impressions", "~", paste(paste(col_names, sep=""), collapse="+")))
    #print(fmla)
    aggregated_clicks <- aggregate(fmla, data = csv_data, FUN = sum)
    #print(fmla1)
    aggregated_impressions <- aggregate(fmla1, data = csv_data, FUN = sum)
    #print(aggregated_clicks)
    #print(aggregated_impressions)
    aggregated_data <- merge(aggregated_clicks, aggregated_impressions)
    obj <- aggregated_data$clicks / aggregated_data$impressions
    aggregated_data[objective_name] <- obj
    return(aggregated_data)
}

get_all_factor_levels <- function(csv_data, factor_cols) {
    all_factor_levels <- lapply(csv_data[factor_cols], function(x) levels(x))
    #print(all_factor_levels)
    factor_levels_list <- lapply(names(all_factor_levels), function(x) paste(x,all_factor_levels[[x]], sep=""))
    factor_levels_vector <- unlist(factor_levels_list)
    return(factor_levels_vector)
}

append_relevel_str <- function(train_cols, csv_data) {
    reference_factors <- unlist(csv_data[1, train_cols])
    part_fmla <- paste(paste(paste(paste("relevel(", train_cols, sep=""), paste("\"", reference_factors, "\"", sep=""), sep=","), ")", sep=""), collapse=" + ")
    #reference_factors_str_vec <- paste(names(reference_factors), as.character(reference_factors), sep="")
    return (part_fmla)
}

solve_lr <- function(csv_data, objective_name, train_cols, limit) {
    # Remove factors having a single valuetrain_cols
    names(train_cols) <- train_cols
    rem_factor_cols_na <- train_cols[sapply(csv_data, function(x) length(levels(x)) > 1)]
    rem_factor_cols <- rem_factor_cols_na[!is.na(rem_factor_cols_na)]
    non_factors_na <- train_cols[sapply(csv_data, function(x) !is.factor(x))]
    non_factors <- non_factors_na[!is.na(non_factors_na)]
    good_train_cols <- c(rem_factor_cols, non_factors)
    if(length(good_train_cols) < 1) return(NULL)
    #relevel_formula_str <- append_relevel_str(rem_cols, csv_data)
    #fmla <- as.formula(paste(objective_name, "~", relevel_formula_str))
    fmla <- as.formula(paste(objective_name, "~", paste(paste(good_train_cols, sep=""), collapse="+")))
    lr_logit <- glm(fmla, data = csv_data, family = binomial(link = logit), control = list(maxit = limit))
    return(lr_logit)
}

write_to_file <- function(out_file_name, logit_result) {
    result_df <- summary(logit_result)$coefficients
    write.csv(file = out_file_name, x = result_df)
}

clargs <- commandArgs(trailingOnly=TRUE)

objective_name <- clargs[1]
csv_file_name <- clargs[2]
out_file_name <- clargs[3]
factors_file_name <- clargs[4]
num_cols <- as.integer(clargs[5])
train_cols <- clargs[6:(5+num_cols)]
cur_pos <- 6 + num_cols
num_factors <- as.integer(clargs[cur_pos])
factor_cols <- clargs[(cur_pos+1):(cur_pos+num_factors)]

csv_data <- read.csv(csv_file_name)
#csv_data <- factorise(objective_name, csv_file_name)
aggregated_data <- aggregate_given_cols(csv_data, objective_name, train_cols)
#print(aggregated_data)
if(num_factors == 0) {
    factorised_data <- aggregated_data
} else {
    factorised_data <- factorise_cols(aggregated_data, factor_cols)
    all_fac_levels <- get_all_factor_levels(factorised_data, factor_cols) 
    write(f = factors_file_name, x = all_fac_levels)
}
#q(status=0)

#print(factorised_data)
logit_result <- solve_lr(factorised_data, objective_name, train_cols, 1000)
if(is.null(logit_result)) {
    q(status=0)
}
write_to_file(out_file_name, logit_result)
if(logit_result$converged) {
    q(status=0)
}
q(status=1)
