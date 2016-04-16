package com.binarycraft.acland.datautil;


import com.binarycraft.acland.entity.Mouza;
import com.binarycraft.acland.entity.Union;
import com.binarycraft.acland.entity.UnionMouzaResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Vector;

import retrofit.Retrofit;

public class GetAndSaveData {

	public static Vector<String> getNamesFromUnions(Vector<Union> unions){
		Vector<String> names = new Vector<String>();
		for (Union union :
				unions) {
			names.add(union.getName());
		}

		return names;
	}

	public static Vector<String> getNamesFromMouzas(Vector<Mouza> mouzas){
		Vector<String> names = new Vector<String>();
		for (Mouza mouza :
				mouzas) {
			names.add(mouza.getName());
		}

		return names;
	}



}
