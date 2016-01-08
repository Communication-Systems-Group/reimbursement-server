package ch.uzh.csg.reimbursement.application.archive;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.uzh.csg.reimbursement.model.Expense;
import ch.uzh.csg.reimbursement.service.ExpenseService;

@Service
@Transactional
public class ExpenseArchivist {

	private static final Logger LOG = LoggerFactory.getLogger(ExpenseArchivist.class);

	@Autowired
	private ExpenseService expenseService;

	@Value("${reimbursement.token.guest.expirationInMonths}")
	private int guestTokenExpirationInMonths;

	@Scheduled(fixedRateString = "${reimbursement.archive.archivePrintedExpenses.intervalInMilliseconds}")
	public void archivePrintedExpenses() {
		List<Expense> printedExpenses = expenseService.getPrintedExpenses();
		for (Expense expense : printedExpenses) {
			// The expenses can be archived as soon as the guestToken will be deleted
			if (expense.canBeArchived(guestTokenExpirationInMonths)) {
				expense.goToNextState();
				LOG.debug("An expense from user " + expense.getUserUid() + " has been archived.");
			}
		}
	}
}
