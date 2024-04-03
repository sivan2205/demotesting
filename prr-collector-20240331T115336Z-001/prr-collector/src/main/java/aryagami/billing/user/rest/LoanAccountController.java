package aryagami.billing.user.rest;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import aryagami.billing.userrepo.model.LoanAccount;
import aryagami.billing.userrepo.repository.LoanAccountRepository;
import aryagami.billing.transactionrepo.model.BankAccount;
import aryagami.billing.user.cmd.LoanAccountCommand;
import aryagami.billing.user.vo.LoanAccountVo;

@RestController
@RequestMapping("/loan")
public class LoanAccountController {

	@Autowired
	LoanAccountRepository loanAccountRepo;
	DozerBeanMapper mapper = new DozerBeanMapper();

	@PostMapping("/send")
	public @ResponseBody Map<String, Object> saveLoanAccount(@RequestBody LoanAccountCommand loanAccountCommand) {
		Map<String, Object> mMap = new HashMap<>();
		LoanAccount act = mapper.map(loanAccountCommand, LoanAccount.class);
		loanAccountRepo.save(act);
		mMap.put("status", "success");
		return mMap;
	}

	@GetMapping("/get")
	public @ResponseBody List<LoanAccountVo> getAllBankAccounts() {
		List<LoanAccount> loanAccounts = loanAccountRepo.findAll();
		List<LoanAccountVo> vos = new ArrayList<>();
		for (LoanAccount act : loanAccounts) {
			LoanAccountVo vo = mapper.map(act, LoanAccountVo.class);
			vos.add(vo);
		}
		return vos;
	}

	@GetMapping("{id}")
	public @ResponseBody LoanAccount getBankAccountById(@PathVariable Long id) {
		LoanAccount vi = loanAccountRepo.findByAccountNumber(id);

		if (vi == null) {

			return null;
		}

		return vi;
	}

	// to send the values from user to transaction
	// an API to Perform transaction from user service to transaction service using
	// HTTP

	@PostMapping("/create_account_details")
	public @ResponseBody Map<String, Object> saveAccountDetails(@RequestBody LoanAccountCommand loanAccountCommand) {
		RestTemplate template = new RestTemplate();
		Map<String, Object> hMap = new HashMap<>();
		String apiurl = "http://localhost:8980/tap_mgmt/bank/send";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		BankAccount wr = template.postForObject(apiurl, loanAccountCommand, BankAccount.class);
		LoanAccountCommand in = mapper.map(wr, LoanAccountCommand.class);

		hMap.put("status", "succes");
		return hMap;
	}
}
