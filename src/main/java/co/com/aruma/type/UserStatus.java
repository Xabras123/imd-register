package co.com.aruma.type;

public enum UserStatus {
	
    REGISTER_PROCCES_INITIALIZED(11,"En registro, email verificado"),
    REGISTER_EMAIL_VERIFIED(12,"En registro, email verificado"),
    ACCOUNT_DISABLED(21, "La cuenta fue deshabilitada (en otras palabras eliminada)");

	
	
    private int code;
    private String detail;

    UserStatus(int _code, String _detail) {
        this.code = _code;
        this.detail = _detail;
    }

    public static UserStatus valueOfByCode(int code) {
    	for (int i = 0; i < UserStatus.values().length; i++) {
    		UserStatus us = UserStatus.values()[i];
    		if(us.getCode()==code)
    			return us;
		}
        return null;
    }

	public int getCode() {
		return code;
	}

	public String getDetail() {
		return detail;
	}

}
