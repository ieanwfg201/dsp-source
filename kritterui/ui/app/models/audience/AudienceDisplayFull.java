package models.audience;

import com.kritter.api.entity.audience.Audience;
public class AudienceDisplayFull extends AudienceDisplay{

    public AudienceDisplayFull(Audience audience){
        super(audience);
    }

    public int getId(){
        return audience.getId();
    }

    public String getAccountGuid(){return audience.getAccount_guid();}

    public String getTags(){return audience.getTags();}

    public String getFilePath(){return audience.getFile_path();}

    public String getTypeName(){
       int typeId =  audience.getType();
        if (typeId==1){
            return "audience code";
        }else {
            return "audience package";
        }
    }

    public String getName(){return audience.getName();}

    public String getSource(){
        String sourceCode = audience.getSource_id();
        String sourceName ="";
        switch (sourceCode){
            case "010":
                sourceName = "Madhouse";
                break;
            case "011":
                sourceName = "TalkingData";
                break;
            case "012":
                sourceName = "UnionPay";
                break;
            case "018":
                sourceName = "Admaster";
                break;
            case "019":
                sourceName = "QianXun";
            default:
                break;
        }
        return sourceName;
        }

}
