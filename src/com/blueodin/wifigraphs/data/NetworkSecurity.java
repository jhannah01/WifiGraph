package com.blueodin.wifigraphs.data;

import java.util.ArrayList;
import java.util.List;

import android.net.wifi.ScanResult;

public class NetworkSecurity {
	private String mCapabilities = "";
	
    private static final String ADHOC_CAPABILITY = "[IBSS]";
    private static final String ENTERPRISE_CAPABILITY = "-EAP-";
    private static final String WPS_CAPABILITIES = "[WPS]";
    private static final String WPA_CAPABILITIES = "[WPA-";
    private static final String WPA2_CAPABILITIES = "[WPA2-";
    private static final String PSK_CAPABILITIES = "-PSK-";
    private static final String TKIP_CAPABILITIES = "-TKIP";
    private static final String CCMP_CAPABILITIES = "CCMP]";
    private static final String ESS_CAPABILITIES = "[ESS]";
	
	public static enum SecurityType {
		Unknown,
		WEP,
		WPA,
		WPA2,
		Open,;
		
		public static List<SecurityType> parseCapabilities(String capabilities) {
			List<SecurityType> types = new ArrayList<NetworkSecurity.SecurityType>();
			
			if (capabilities.equals(WEP)) 
	        	types.add(SecurityType.WEP);

	        if (capabilities.contains(WPA_CAPABILITIES))
	        	types.add(SecurityType.WPA);
	        
	        if(capabilities.contains(WPA2_CAPABILITIES))
	        	types.add(SecurityType.WPA2);
	        
	        if(types.size() < 1)
	        	types.add(SecurityType.Open);
	        	
	        return types;
	    }
	}
	
	public static boolean isAdhoc(String capabilities) {
        return capabilities.contains(ADHOC_CAPABILITY);
    }
	
	public static boolean isEnterprise(String capabilities) {
		return capabilities.contains(ENTERPRISE_CAPABILITY);
	}
	
	public static boolean hasWPS(String capabilities) {
		return capabilities.contains(WPS_CAPABILITIES);
	}
	
	public static boolean usesPSK(String capabilities) {
		return capabilities.contains(PSK_CAPABILITIES);
	}
	
	public static boolean usesTKIP(String capabilities) {
		return capabilities.contains(TKIP_CAPABILITIES);
	}
	
	public static boolean usesCCMP(String capabilities) {
		return capabilities.contains(CCMP_CAPABILITIES);
	}
	
	public static boolean hasESS(String capabilities) {
		return capabilities.contains(ESS_CAPABILITIES);
	}
	
	public NetworkSecurity(ScanResult result) {
		this(result.capabilities);
	}
	
	public NetworkSecurity(String capabilities) {
		setCapabilities(capabilities);
	}
	
	public String getCapabilities() {
		return mCapabilities;
	}
	
	public void setCapabilities(String capabilities) {
		this.mCapabilities = capabilities;
	}
	
	public List<SecurityType> getSecurityTypes() {
		return SecurityType.parseCapabilities(mCapabilities);
	}
	
	public boolean isAdhoc() {
		return isAdhoc(mCapabilities);
	}
	
	public boolean isEnterprise() {
		return isEnterprise(mCapabilities);
	}
	
	public boolean hasWPS() {
		return hasWPS(mCapabilities);
	}
	
	public boolean usesPSK() {
		return usesPSK(mCapabilities);
	}
	
	public boolean usesTKIP() {
		return usesTKIP(mCapabilities);
	}
	
	public boolean usesCCMP() {
		return usesCCMP(mCapabilities);
	}
	
	public boolean hasESS() {
		return hasESS(mCapabilities);
	}
	
	public static String getFriendlyString(String capabilities) {
		NetworkSecurity sec = new NetworkSecurity(capabilities);
		
		String hasWPS = (sec.hasWPS() ? "Yes" : "No");
		String netType = "Other";
		
		if(sec.isAdhoc())
			netType = "Ad-Hoc";
		else if(sec.isEnterprise())
			netType = "Enterprise";
		else if(sec.hasESS())
			netType = "Access Point";
		
		return String.format("%s (WPS: %s, Type: %s)", formatListOfSecurities(sec.getSecurityTypes()), hasWPS, netType);
	}
	
	public static String stringFromCapabilities(String capabilities) {
		return (new NetworkSecurity(capabilities)).toString();
	}
	
	public static String formatListOfSecurities(List<SecurityType> securities) {
		if(securities.size() < 1)
			return "None";
		
		if(securities.size() == 1)
			return securities.get(0).toString();
		
		String result = securities.remove(0).toString();
		
		for(SecurityType sec : securities)
			result += ", " + sec.toString();
			
		return result;
	}
	
	@Override
	public String toString() {
		return getFriendlyString(this.mCapabilities);
	}
}