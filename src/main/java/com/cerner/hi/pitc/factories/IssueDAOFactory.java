package com.cerner.hi.pitc.factories;

import com.cerner.hi.pitc.contract.IssueDAO;
import com.cerner.hi.pitc.repository.JRJCIssueDAO;

public class IssueDAOFactory {
	
	public static IssueDAO getIssueDAO() {
		return new JRJCIssueDAO();
	}

}
