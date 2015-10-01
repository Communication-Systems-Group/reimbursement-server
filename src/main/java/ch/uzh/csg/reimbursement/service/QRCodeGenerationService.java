package ch.uzh.csg.reimbursement.service;

import java.io.File;

import net.glxn.qrgen.javase.QRCode;

import org.springframework.stereotype.Service;

@Service
public class QRCodeGenerationService {

	public File generateQRCode(String expenseUid) {
		return QRCode.from("http://localhost:9005/#!/view-expense/"+expenseUid).file();
	}
}
