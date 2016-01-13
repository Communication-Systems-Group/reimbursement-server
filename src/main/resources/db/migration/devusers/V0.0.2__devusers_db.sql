-- create a few initial users
INSERT INTO User_ VALUES (1, 'prof', 'Velo', 'Mech', 'reimbursement-debug@ifi.uzh.ch', 'depman', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (2, 'junior', 'Bus', 'Fahrer', 'reimbursement-debug@ifi.uzh.ch', 'prof', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (3, 'senior', 'Milch', 'Maa', 'reimbursement-debug@ifi.uzh.ch', 'prof', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (4, 'fadmin', 'Böser', 'Bube', 'reimbursement-debug@ifi.uzh.ch', 'depman', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (5, 'fadmin2', 'Töff', 'Fahrer', 'reimbursement-debug@ifi.uzh.ch', 'depman', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (6, 'depman', 'Han', 'Solo', 'reimbursement-debug@ifi.uzh.ch', 'headinst', null, null, false, 'DE', null, null, true);
INSERT INTO User_ VALUES (7, 'headinst', 'Marco', 'Polo', 'reimbursement-debug@ifi.uzh.ch', 'depman', null, null, false, 'DE', null, null, true);

INSERT INTO Role_ VALUES (1, 'USER');
INSERT INTO Role_ VALUES (1, 'PROF');
INSERT INTO Role_ VALUES (2, 'USER');
INSERT INTO Role_ VALUES (3, 'USER');
INSERT INTO Role_ VALUES (4, 'USER');
INSERT INTO Role_ VALUES (4, 'FINANCE_ADMIN');
INSERT INTO Role_ VALUES (5, 'USER');
INSERT INTO Role_ VALUES (5, 'FINANCE_ADMIN');
INSERT INTO Role_ VALUES (6, 'USER');
INSERT INTO Role_ VALUES (6, 'DEPARTMENT_MANAGER');
INSERT INTO Role_ VALUES (7, 'USER');
INSERT INTO Role_ VALUES (7, 'HEAD_OF_INSTITUTE');
