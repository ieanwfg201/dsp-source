package com.kritter.bidreqres.entity;

import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Setter;

public class YoukuBidRequestUserTagDTO {
	   @JsonProperty("1")
	   @Setter
	   private Integer one;
	   public Integer getOne(){
		   return one;
	   }
	   @JsonProperty("2")
	   @Setter
	   private Integer two;
	   public Integer getTwo(){
		   return two;
	   }
	   @JsonProperty("3")
	   @Setter
	   private Integer three;
	   public Integer getThree(){
		   return three;
	   }
	   @JsonProperty("4")
	   @Setter
	   private Integer four;
	   public Integer getFour(){
		   return four;
	   }
	   @JsonProperty("5")
	   @Setter
	   private Integer five;
	   public Integer getFive(){
		   return five;
	   }
}
