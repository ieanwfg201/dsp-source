package com.kritter.ex_int.utils.picker;

import java.util.List;
import java.util.Random;

import com.kritter.entity.reqres.entity.ResponseAdInfo;

public class RandomPicker
{

	public static ResponseAdInfo pickRandomlyOneOfTheResponseAdInfoWithHighestSameEcpmValues(
																							 ResponseAdInfo responseAdInfoHighestEcpm,
																							 List<ResponseAdInfo> responseAdInfoList,
																							 Random randomPicker
																							)
	{
		int sameHighestEcpmCount = 1;

		for(int i=1; i< responseAdInfoList.size();i++)
		{
			if(responseAdInfoHighestEcpm.getEcpmValue().compareTo(responseAdInfoList.get(i).getEcpmValue()) == 0)
			{
				sameHighestEcpmCount += 1;
			}
		}

		int index = randomPicker.nextInt(sameHighestEcpmCount);

		if(index >= 0)
			return responseAdInfoList.get(index);

		return responseAdInfoList.get(0);
	}
}