package com.al;

//This class is used for the information detected by End-User when the system is finally deployed
//End-user detects rough GPS Lat, Long, AP Info List (MAC, RSS)

import java.util.ArrayList;
import java.util.List;

public class AL_UserDetectedInfo {

	private double m_fGpsLat = 0.0f;
	private double m_fGpsLong = 0.0f;
	private List<AL_APInfo> m_lstAPInfo = null;
	
	public AL_UserDetectedInfo() {
		// TODO Auto-generated constructor stub
	}

	public AL_UserDetectedInfo(double fGpsLat, double fGpsLong, List<AL_APInfo> lstAPInfo) {
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfo = lstAPInfo;
	}
	
	public void setUserDetectionInfo(double fGpsLat, double fGpsLong, List<AL_APInfo> lstAPInfo) {
		m_fGpsLat = fGpsLat;
		m_fGpsLong = fGpsLong;
		m_lstAPInfo = lstAPInfo;
	}
	
	public double getGpsLat() {
		return m_fGpsLat;
	}
	
	public void setGpsLat(double fGpsLat) {
		m_fGpsLat = fGpsLat;
	}
	
	public double getGpsLong() {
		return m_fGpsLong;
	}

	public void setGpsLong(double fGpsLong) {
		m_fGpsLong = fGpsLong;
	}
	
	public List<AL_APInfo> getAPInfo() {
		return m_lstAPInfo;
	}
	
	public void setAPInfo(List<AL_APInfo> lstAPInfo) {
		m_lstAPInfo = lstAPInfo;
	}
}
