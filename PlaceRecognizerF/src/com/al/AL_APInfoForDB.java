package com.al;

//This class is for AP information from crowdsource result

public class AL_APInfoForDB {
	private String m_sMAC = "";  //AP MAC address
	private double m_fLbRSS = 0.0f;  //Lower bound RSS
	private double m_fUbRSS = 0.0f;  //Upper bound RSS
	private double m_fMeanRSS = 0.0f;  //AP RSS
	private int m_nOrder = 0;   //Order number of the AP corresponding to the RSS among the detected AP MACs
	
	public AL_APInfoForDB() {
		// TODO Auto-generated constructor stub
	}
	
	public AL_APInfoForDB(String sMAC, double fLbRSS, double fUbRSS, double fMeanRSS, int nOrder) {
		m_sMAC = sMAC;
		m_fLbRSS = fLbRSS;
		m_fUbRSS = fUbRSS;
		m_fMeanRSS = fMeanRSS;
		m_nOrder = nOrder;
	}

	public void setAPInfo(String sMAC, double fLbRSS, double fUbRSS, double fMeanRSS, int nOrder) {
		m_sMAC = sMAC;
		m_fLbRSS = fLbRSS;
		m_fUbRSS = fUbRSS;
		m_fMeanRSS = fMeanRSS;
		m_nOrder = nOrder;
	}
	
	public String getMAC() {
		return m_sMAC;
	}

	public void setMAC(String sMAC) {
		m_sMAC = sMAC;
	}
	
	public double getLbRSS() {
		return m_fLbRSS;
	}

	public void setLbRSS(double fLbRSS) {
		m_fLbRSS = fLbRSS;
	}

	public double getUbRSS() {
		return m_fUbRSS;
	}

	public void setUbRSS(double fUbRSS) {
		m_fUbRSS = fUbRSS;
	}
	
	public double getMeanRSS() {
		return m_fMeanRSS;
	}

	public void setMeanRSS(double fMeanRSS) {
		m_fMeanRSS = fMeanRSS;
	}
	
	public int getOrder() {
		return m_nOrder;
	}

	public void setOrder(int nOrder) {
		m_nOrder = nOrder;
	}
	
}
