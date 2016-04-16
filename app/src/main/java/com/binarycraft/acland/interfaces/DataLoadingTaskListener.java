package com.binarycraft.acland.interfaces;

import java.util.List;

import com.binarycraft.acland.entity.ProductObject;

public interface DataLoadingTaskListener {

	public void onDataLoaded(List<ProductObject> items);
	
}
