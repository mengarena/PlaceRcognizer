package com.al;

//This class is for AP information from crowdsource result

public class AL_APInfo {
	private String m_sMAC = "";  //AP MAC address
	private double m_fRSS = 0.0f;  //AP RSS
	private int m_nOrder = 0;   //Order number of the AP corresponding to the RSS among the detected AP MACs
	
	public AL_APInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public AL_APInfo(String sMAC, double fRSS, int nOrder) {
		m_sMAC = sMAC;
		m_fRSS = fRSS;
		m_nOrder = nOrder;
	}

	public void setAPInfo(String sMAC, double fRSS, int nOrder) {
		m_sMAC = sMAC;
		m_fRSS = fRSS;
		m_nOrder = nOrder;
	}
	
	public String getMAC() {
		return m_sMAC;
	}

	public void setMAC(String sMAC) {
		m_sMAC = sMAC;
	}
	
	public double getRSS() {
		return m_fRSS;
	}

	public void setRSS(double fRSS) {
		m_fRSS = fRSS;
	}
	
	public int getOrder() {
		return m_nOrder;
	}

	public void setOrder(int nOrder) {
		m_nOrder = nOrder;
	}
	
}
