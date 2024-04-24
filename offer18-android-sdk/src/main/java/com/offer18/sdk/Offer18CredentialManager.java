package com.offer18.sdk;

import com.offer18.sdk.exception.Offer18InvalidCredentialException;
import com.offer18.sdk.contract.CredentialManager;

public class Offer18CredentialManager implements CredentialManager {
    private String domain;
    private String accountID;

    public Offer18CredentialManager(String domain, String accountID) throws Offer18InvalidCredentialException {
        if ((domain == null || domain.isEmpty()) && (accountID == null || accountID.isEmpty())) {
            throw new Offer18InvalidCredentialException();
        }
        if (domain == null || domain.isEmpty()) {
            throw new Offer18InvalidCredentialException();
        }
        if (accountID == null || accountID.isEmpty()) {
            throw new Offer18InvalidCredentialException();
        }
        this.setDomain(domain);
        this.setAccountID(accountID);
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public String getAccountID() {
        return this.accountID;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }
}
