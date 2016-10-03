package com.al;

//This class is for Place Recognition

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AL_PlaceRecognizer {

	private AL_Util m_Util = new AL_Util();
	
	//Possible maximul matching score is 1/1*1/1 + 1/2*1/2 + .....1/10*1/10 = 1.5498
	private static double MATCHED_SCORE_THRESHOLD = 0.5;    //?????
	
	public AL_PlaceRecognizer() {
		// TODO Auto-generated constructor stub
	}

	
	//Compare AP info to get a matching score
	//lstUserDetectedAPInfo: The AP information detected by end-user's smarphone, the order field is meaningless
	//lstCandidateAPInfo: The AP Info of the candidate places/stores, the order field is meaningful
	//The formula for calculating the matching score:
	//	Sum (Weight of Matched AP MAC * 1/Order of Matched AP MAC in User Detected AP List)
	//Where "Weight of Matched AP MAC" = 1/Order of the AP in the AP list for the store (which is deployed in the end-user's phone)
	//"Order of Matched AP MAC in User Detected AP List" is the Order of the AP in the End-user detected AP List based on RSS
	//For the MAC to be counted, its RSS must be in the range of the MAC's lower bound and upper bound
	private double matchAPInfo(List<AL_APInfo> lstUserDetectedAPInfo, List<AL_APInfoForDB> lstCandidateAPInfoForDB) {
		double fMatchingScore = 0.0;
		List<AL_APInfo> lstOrderedUserDetectedAPInfo = new ArrayList<AL_APInfo>();
		int i;
		int nIdx = -1;
		int nMatchedIdx = -1;
		
		//Here compare the whether the AP MAC address matches, and the order of the RSS are matched, and calculate a score
		//First, Order the APs detected by end-user's phone at a store
		for (i=0; i<lstUserDetectedAPInfo.size(); i++) {
			if (i == 0) {
				lstOrderedUserDetectedAPInfo.add(lstUserDetectedAPInfo.get(i));
			} else {
				nIdx = -1;
				for (int j=0; j<lstOrderedUserDetectedAPInfo.size(); j++) {
					if (lstUserDetectedAPInfo.get(i).getRSS() > lstOrderedUserDetectedAPInfo.get(j).getRSS()) {
						nIdx = j;
						break;
					}
				}
				
				if (nIdx == -1) {
					lstOrderedUserDetectedAPInfo.add(lstUserDetectedAPInfo.get(i));
				} else {
					lstOrderedUserDetectedAPInfo.add(nIdx, lstUserDetectedAPInfo.get(i));
				}
				
			}
		}
		
		//Set the order for AP in lstOrderedUserDetectedAPInfo
		//Calculate the matching score
		for (i=0; i<lstOrderedUserDetectedAPInfo.size(); i++) {
			lstOrderedUserDetectedAPInfo.get(i).setOrder(i+1);   //Update the order field. But if directly calculate the matching score like what is doing below, this is not necessary, because the order is just i.
			
			nMatchedIdx = -1;
			for (int j=0; j<lstCandidateAPInfoForDB.size(); j++) {
				//Check whether MAC matched and also check whether the RSS is in the range of Lower bound and Upper bound of the matched MAC
				if (lstOrderedUserDetectedAPInfo.get(i).getMAC().compareToIgnoreCase(lstCandidateAPInfoForDB.get(j).getMAC()) == 0 &&
					lstOrderedUserDetectedAPInfo.get(i).getRSS() >= lstCandidateAPInfoForDB.get(j).getLbRSS() &&
					lstOrderedUserDetectedAPInfo.get(i).getRSS() <= lstCandidateAPInfoForDB.get(j).getUbRSS()) {
					
					nMatchedIdx = j;
					break;
				}
			}
			
			if (nMatchedIdx != -1) {
				fMatchingScore = fMatchingScore + 1.0/(i+1)*1.0/lstOrderedUserDetectedAPInfo.get(nMatchedIdx).getOrder();
			}
			
		}
		
		
		return fMatchingScore;
	}
	
	
	//userDetectedInfo:  The information detected by end-user's phone in a place/store when the system is deployed in end-user's phone
	//lstPlaceAPInfo: The candidate GPS--Store/Place Name--AP Info List, which is deployed in the end-user's smartphone
	public String recognizePlace(AL_UserDetectedInfo userDetectedInfo, List<AL_PlaceAPInfo> lstPlaceAPInfo) {
		String sPlaceName = "null";
		int i;
		double fMaxMatchingScore = 0.0f;
		double fMatchingScore = 0.0f;
		int nMatchedIdx = -1;
		AL_PlaceAPInfo placeAPInfo;
		boolean bClose = false;
		
		String sRecognizeResult = "";
		double fGpsLat = 0.0f;
		double fGpsLong = 0.0f; 
		
		sRecognizeResult = fMaxMatchingScore + "," + sPlaceName; 
		
		if (userDetectedInfo == null || lstPlaceAPInfo == null) return "";
		
		List<AL_APInfo> lstUserDetectedAPInfo = userDetectedInfo.getAPInfo(); //In this, the order information is meaningless
		
		fGpsLat = userDetectedInfo.getGpsLat();
		fGpsLong = userDetectedInfo.getGpsLong();
		
		for (i=0; i<lstPlaceAPInfo.size(); i++) {
			placeAPInfo = lstPlaceAPInfo.get(i);
			
			bClose = m_Util.isGpsCoordinatesClose(fGpsLat, fGpsLong, placeAPInfo.getGpsLat(), placeAPInfo.getGpsLong());
			if (bClose == false) {
			//	continue;
			}
			
			fMatchingScore = matchAPInfo(lstUserDetectedAPInfo, placeAPInfo.getAPInfoForDB());
			
			if (fMatchingScore > fMaxMatchingScore) {
				fMaxMatchingScore = fMatchingScore;
				nMatchedIdx = i;
			}
			
		}
		
		
//		if (nMatchedIdx != -1 && fMaxMatchingScore >= MATCHED_SCORE_THRESHOLD) {
		if (nMatchedIdx != -1) {
			sPlaceName = lstPlaceAPInfo.get(nMatchedIdx).getPlaceName() ;
			DecimalFormat df = new DecimalFormat("0.0000");
			//sPlaceName = "[" +  df.format(fMaxMatchingScore) + "] " + sPlaceName;
			
			sRecognizeResult = df.format(fMaxMatchingScore) + "," + sPlaceName; 
		}
		
		
		return sRecognizeResult;
	}
	
}
