/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2018 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.message.web;

import java.io.IOException;

import javax.mail.MessagingException;

import com.axelor.apps.message.db.EmailAccount;
import com.axelor.apps.message.db.repo.EmailAccountRepository;
import com.axelor.apps.message.exception.IExceptionMessage;
import com.axelor.apps.message.service.MailAccountService;
import com.axelor.exception.service.TraceBackService;
import com.axelor.i18n.I18n;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MailAccountController {
	
	@Inject
	private MailAccountService mailAccountService;
	
	@Inject
	private EmailAccountRepository mailAccountRepo;
	
	public void validateSmtpAccount(ActionRequest request,ActionResponse response){
		
		EmailAccount account = request.getContext().asType(EmailAccount.class);
		
		try {
			
			mailAccountService.checkMailAccountConfiguration(account);

			response.setValue("isValid", Boolean.TRUE );
			response.setFlash( I18n.get(IExceptionMessage.MAIL_ACCOUNT_3) );
			
		} catch ( Exception e) {
			
			TraceBackService.trace(response, e);
			response.setValue("isValid",Boolean.FALSE);
			
		}
		
	}
	
	public void checkDefaultMailAccount(ActionRequest request, ActionResponse response){
		EmailAccount account = request.getContext().asType(EmailAccount.class);
		if(!mailAccountService.checkDefaultMailAccount(account)){
			response.setError(I18n.get(IExceptionMessage.MAIL_ACCOUNT_5));
			response.setValue("isDefault", false);
		}
	}
	
	public void fetchEmails(ActionRequest request, ActionResponse response) throws MessagingException, IOException  {
		
		EmailAccount account = request.getContext().asType(EmailAccount.class);
		account = mailAccountRepo.find(account.getId());
		
		int totalFetched = mailAccountService.fetchEmails(account, true);
		
		response.setFlash(I18n.get(String.format("Total email fetched: %s", totalFetched)));
	}

	public void validate(ActionRequest request, ActionResponse response) {

		if (request.getContext().get("newPassword") != null)
			response.setValue("password", mailAccountService.getEncryptPassword(request.getContext().get("newPassword").toString()));
	}
}
