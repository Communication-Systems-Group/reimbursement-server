-- create a few initial users
INSERT INTO User_ VALUES (1000001, 'prof', 'Velo', 'Mech', 'reimbursement-debug@ifi.uzh.ch', 'depman', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1000002, 'junior', 'Bus', 'Fahrer', 'reimbursement-debug@ifi.uzh.ch', 'prof', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1000003, 'senior', 'Milch', 'Maa', 'reimbursement-debug@ifi.uzh.ch', 'prof', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1000004, 'fadmin', 'Böser', 'Bube', 'reimbursement-debug@ifi.uzh.ch', 'depman', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1000005, 'fadmin2', 'Töff', 'Fahrer', 'reimbursement-debug@ifi.uzh.ch', 'depman', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1000006, 'depman', 'Han', 'Solo', 'reimbursement-debug@ifi.uzh.ch', 'headinst', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (1000007, 'headinst', 'Marco', 'Polo', 'reimbursement-debug@ifi.uzh.ch', 'depman', null, null, false, 'DE', null, null, true);

ALTER TABLE User_ ADD CONSTRAINT USER_UID_UNIQUE UNIQUE(uid);
ALTER TABLE User_ ADD FOREIGN KEY (signature_id) REFERENCES Signature_(id);
ALTER TABLE User_ ADD FOREIGN KEY (manager_id) REFERENCES User_(id);

INSERT INTO Role_ VALUES (1000001, 'USER');
INSERT INTO Role_ VALUES (1000001, 'PROF');
INSERT INTO Role_ VALUES (1000002, 'USER');
INSERT INTO Role_ VALUES (1000003, 'USER');
INSERT INTO Role_ VALUES (1000004, 'USER');
INSERT INTO Role_ VALUES (1000004, 'FINANCE_ADMIN');
INSERT INTO Role_ VALUES (1000005, 'USER');
INSERT INTO Role_ VALUES (1000005, 'FINANCE_ADMIN');
INSERT INTO Role_ VALUES (1000006, 'USER');
INSERT INTO Role_ VALUES (1000006, 'DEPARTMENT_MANAGER');
INSERT INTO Role_ VALUES (1000007, 'USER');
INSERT INTO Role_ VALUES (1000007, 'HEAD_OF_INSTITUTE');
