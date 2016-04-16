package com.binarycraft.acland.util;

import com.binarycraft.acland.entity.ProductObject;

public class SharedData {
	private static SharedData instance = null;

	protected SharedData() {
		// Exists only to defeat instantiation.
	}

	public static SharedData getInstance() {
		if (instance == null) {
			instance = new SharedData();
		}
		return instance;
	}
	
	private ProductObject item;
	private String productName;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public ProductObject getItem() {
		return item;
	}

	public void setItem(ProductObject item) {
		this.item = item;
	}
}
