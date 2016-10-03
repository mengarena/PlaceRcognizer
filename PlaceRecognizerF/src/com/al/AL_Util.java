package com.al;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class AL_Util {
	//Lat 0.0001 degree = 18.5 meters;  Long 0.0001 degree = 14.2 meters
	
	public static final double DISTANCE_THRESHOLD = 25000;  //100 meters
	public static final int COMPARED_AP_COUNT = 20;  //How many AP info (MAC, RSS, Order) will be stored in database and compared to check whether the store is matched or not

	public static final int RECORD_OLD = 0;
	public static final int RECORD_NEW = 1;
	
	public AL_Util() {
		// TODO Auto-generated constructor stub
	}
	

	public double calculateGpsDistance(double fLat1, double fLong1, double fLat2, double fLong2) {
		double fEarthRadius = 6371000;   //Meters
		double fDistance = 0.0;  //In meters
		double fLatGap = fLat2 - fLat1;
		double fLongGap = fLong2 - fLong1;
		
		fLatGap = fLatGap * Math.PI/180;
		fLongGap = fLongGap * Math.PI/180;
		
		double a = Math.sin(fLatGap / 2.0) * Math.sin(fLatGap / 2.0) +  
				Math.cos(fLat1*Math.PI/180) * Math.cos(fLat2*Math.PI/180) * Math.sin(fLongGap/2.0) * Math.sin(fLongGap/2.0);  
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));  
        
        fDistance = fEarthRadius * c;  
		
		return fDistance;
	}
	
	
	//Check whether two GPS coordinates are close
	public boolean isGpsCoordinatesClose(double fLat1, double fLong1, double fLat2, double fLong2) {
		boolean bRet = false;
	//	double fLatGap = fLat1 - fLat2;
	//	double fLongGap = fLong1 - fLong2;
		
	//	double fX = fLongGap/0.0001*14.2;   //Convert Long gap to distance in meters
	//	double fY = fLatGap/0.0001*18.5;  //Convert Lat gap to distance in meters
		
//		double fDistance = Math.sqrt(fX*fX + fY*fY);

		double fDistance = calculateGpsDistance(fLat1, fLong1, fLat2, fLong2);
	
		if (fDistance > DISTANCE_THRESHOLD) {
			bRet = false;
		} else {
			bRet = true;
		}
		
		return bRet;
	}
	
	
/*	
	//Calculate the matching score
	//lstOCRedWords: The list of words OCRed from the pictures taken by the user
	public double calculateMatchingScore(List<String> lstOCRedWords, List<String> lstPlaceKeywords, List<Double> lstfPlaceKeywordWeights) {
		double fMatchingScore = 0.0f;
		
		for (String sOCRedWord:lstOCRedWords) {

			for (int i=0; i<lstPlaceKeywords.size(); i++) {
				if (sOCRedWord.compareToIgnoreCase(lstPlaceKeywords.get(i)) == 0) {
					fMatchingScore = fMatchingScore + lstfPlaceKeywordWeights.get(i);
					break;
				}
			}
		}
		
		return fMatchingScore;
	}

	
	//Calculate the matching score
	//lstOCRedWords: The list of words OCRed from the pictures taken by the user
	public double calculateMatchingScore(List<String> lstOCRedWords, List<AL_KeywordWeight> lstKeywordWeight) {
		double fMatchingScore = 0.0f;
		
		for (String sOCRedWord:lstOCRedWords) {

			for (int i=0; i<lstKeywordWeight.size(); i++) {
				if (sOCRedWord.compareToIgnoreCase(lstKeywordWeight.get(i).getKeyword()) == 0) {
					fMatchingScore = fMatchingScore + lstKeywordWeight.get(i).getWeight();
					break;
				}
			}
		}
		
		return fMatchingScore;
	}
	
*/
	
	public List<AL_APInfo> sortAPInfo(List<AL_APInfo> lstAPInfo) {
		List<AL_APInfo> lstSortedAPInfo = new ArrayList<AL_APInfo>();
		int nIdx = -1;
		int i,j;
		
		if (lstAPInfo.size() == 0) return null;
		
		for (i=0; i<lstAPInfo.size(); i++) {
			if (i == 0) {
				lstSortedAPInfo.add(lstAPInfo.get(i));
			} else {
				nIdx = -1;
				
				for (j=0; j<lstSortedAPInfo.size(); j++) {
					if (lstAPInfo.get(i).getRSS() > lstSortedAPInfo.get(j).getRSS()) {
						nIdx = j;
						break;
					}
				}
				
				if (nIdx == -1) {
					lstSortedAPInfo.add(lstAPInfo.get(i));
				} else {
					lstSortedAPInfo.add(nIdx, lstAPInfo.get(i));
				}
				
			}
		}
		
		for (i=0; i<lstSortedAPInfo.size(); i++) {
			lstSortedAPInfo.get(i).setOrder(i+1);
		}
				
		return lstSortedAPInfo;
	}
	
	
	public double calculateMean(List<Double> lstfValues) {
		double fMean = 0.0;
		double fTotal = 0.0;
		
		if (lstfValues == null) return 0.0;
		
		for (int i=0; i<lstfValues.size(); i++) {
			fTotal = fTotal  + lstfValues.get(i);
		}
		
		fMean = fTotal/lstfValues.size();
		
		return fMean;
	}

	public double calculateStd(List<Double> lstfValues) {
		double fStd = 0.0;
		double fMean = 0.0;
		double fTotal = 0.0;
		int i;
		int nCnt = 0;
		
		if (lstfValues == null) return 0.0;
		
		nCnt = lstfValues.size();
		
		if (nCnt == 1) return 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + lstfValues.get(i);
		}
		
		fMean = fTotal/nCnt;
		
		fTotal = 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + Math.pow((lstfValues.get(i)-fMean), 2);
		}
		
		fStd = Math.sqrt(fTotal/(nCnt-1));
		
		return fStd;
	}

	
	public double calculateMean(Double[] arrfValues) {
		double fMean = 0.0;
		double fTotal = 0.0;
		
		if (arrfValues == null) return 0.0;
		
		for (int i=0; i<arrfValues.length; i++) {
			fTotal = fTotal  + arrfValues[i];
		}
		
		fMean = fTotal/arrfValues.length;
		
		return fMean;
	}
	
	
	public double calculateStd(Double[] arrfValues) {
		double fStd = 0.0;
		double fMean = 0.0;
		double fTotal = 0.0;
		int i;
		int nCnt = 0;
		
		if (arrfValues == null) return 0.0;
		
		nCnt = arrfValues.length;
		
		if (nCnt == 1) return 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + arrfValues[i];
		}
		
		fMean = fTotal/nCnt;
		
		fTotal = 0.0;
		
		for (i=0; i<nCnt; i++) {
			fTotal = fTotal  + Math.pow((arrfValues[i]-fMean), 2);
		}
		
		fStd = Math.sqrt(fTotal/(nCnt-1));
		
		return fStd;
	}
	
	
	public List<AL_PlaceAPInfo> getPlaceAPInfoByFile(String sDatabaseFullPathFile) {
	
    	File flFile;
    	FileReader fr;
    	BufferedReader br;
    	String sLine = "";
    	int i;
    	
    	List<AL_PlaceAPInfo> lstPlaceAPInfo = null;
    	
		flFile = new File(sDatabaseFullPathFile);
		if (flFile.exists()) {
			
			lstPlaceAPInfo = new ArrayList<AL_PlaceAPInfo>();
			//Derive existing labels
			try{
				fr = new FileReader(sDatabaseFullPathFile);
				br = new BufferedReader(fr);
				
				while( (sLine = br.readLine()) != null) {
					String[] fields = null;
					fields = sLine.split(",");

					int nTotalLen = fields.length;
					int nAPCnt = (int)((nTotalLen-3)/5);
					
					AL_PlaceAPInfo placeAPInfo = new AL_PlaceAPInfo();
					
					placeAPInfo.setPlaceName(fields[0]);
					placeAPInfo.setGpsLat(Double.valueOf(fields[1]).doubleValue());
					placeAPInfo.setGpsLong(Double.valueOf(fields[2]).doubleValue());
					
					List<AL_APInfoForDB> lstAPInfoForDB = new ArrayList<AL_APInfoForDB>();
					
					AL_APInfoForDB[] arrAPInfoForDB = new AL_APInfoForDB[AL_Util.COMPARED_AP_COUNT];
					
					for (i=1; i<=nAPCnt; i++) {
						arrAPInfoForDB[i-1] = new AL_APInfoForDB();
												
						arrAPInfoForDB[i-1].setMAC(fields[2+(i-1)*5+1]);
						arrAPInfoForDB[i-1].setLbRSS(Double.valueOf(fields[2+(i-1)*5+2]).doubleValue());
						arrAPInfoForDB[i-1].setUbRSS(Double.valueOf(fields[2+(i-1)*5+3]).doubleValue());
						arrAPInfoForDB[i-1].setMeanRSS(Double.valueOf(fields[2+(i-1)*5+4]).doubleValue());
						arrAPInfoForDB[i-1].setOrder(Integer.valueOf(fields[2+(i-1)*5+5]).intValue());
						
						lstAPInfoForDB.add(arrAPInfoForDB[i-1]);
					}

					for (i=nAPCnt+1; i<=AL_Util.COMPARED_AP_COUNT; i++) {
						arrAPInfoForDB[i-1] = new AL_APInfoForDB();
						
						arrAPInfoForDB[i-1].setMAC("0");
						arrAPInfoForDB[i-1].setLbRSS(0);
						arrAPInfoForDB[i-1].setUbRSS(0);
						arrAPInfoForDB[i-1].setMeanRSS(0);
						arrAPInfoForDB[i-1].setOrder(i);
						
						lstAPInfoForDB.add(arrAPInfoForDB[i-1]);						
					}
					
					placeAPInfo.setAPInfoForDB(lstAPInfoForDB);
					
					lstPlaceAPInfo.add(placeAPInfo);
					
				}
				
				fr.close();
			} catch (Exception e) {
				
			}					
		}
    	
		return lstPlaceAPInfo;
		
	}	
	
	
}
