package com.dragons.aurora;

public class TokenDispenserMirrors {

    static private String[] mirrors = new String[]{
            "https://token-dispenser.herokuapp.com",
            "http://route-token-dispenser.193b.starter-ca-central-1.openshiftapps.com",
            "http://token-dispenser.duckdns.org:8080"
    };
    private int n = 0;

    public void reset() {
        n = 0;
    }

    public String get() {
        if (n >= mirrors.length) {
            reset();
        }
        return mirrors[n++];
    }
}
