package controllers.advertiser;
import com.kritter.geo.utils.IPTransformer;

public class ValidateIpFile {
    private static final String COLON = ":";
    private static final String DELIMITER = "-";

    /*Should return null if ip line is valid*/
    public static String validate(String line){
        String lineParts[] = line.split(DELIMITER);
        if(null == lineParts || lineParts.length != 2)
        {
            return "Line " +line+ " does not adhere to startip-endip format . Example Usage: 10.123.1.100-10.123.1.300";
        }
        if(line.contains(COLON)){
            try{
                IPTransformer.fetchBigIntValueForIPV6(lineParts[0]);
                IPTransformer.fetchBigIntValueForIPV6(lineParts[1]);
            }catch(Exception e){
                return "The input ip range "+line+" from file is invalid ipv6";
            }
        }else{
            try{
                IPTransformer.fetchLongValueForIPV4(lineParts[0]);
                IPTransformer.fetchLongValueForIPV4(lineParts[1]);
            }catch (Exception e){
                return "The input ip range "+line+" from file is invalid ipv4";
            }
        }
        return null;
    }
}
